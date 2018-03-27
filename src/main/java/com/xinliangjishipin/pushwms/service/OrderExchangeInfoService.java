package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.mapper.POrderDetailMapper;
import com.xinliangjishipin.pushwms.mapper.POrderMapper;
import com.xinliangjishipin.pushwms.mapper.WarehouseConfigMapper;
import com.xinliangjishipin.pushwms.utils.HttpClientUtils;
import com.xinliangjishipin.pushwms.utils.MD5EncoderUtil;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderExchangeInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private POrderMapper pOrderMapper;

    @Autowired
    private POrderDetailMapper pOrderDetailMapper;

    @Autowired
    private WarehouseConfigMapper warehouseConfigMapper;

    @Value("${push.order.fail}")
    private String pushOrderFail;

    @Value("${push.order.process.result.fail.maxcount}")
    private String processResultFailMaxCount;

    @Value("${push.order.success}")
    private String pushOrderSuccess;

    @Value("${push.order.maxcount}")
    private int pushOrderMaxCount;

    @Value("${push.order.porder}")
    private String pushOrderPOrder;

    @Value("${wms.wj.system}")
    private String wmsWJSystem;

    @Value("${wms.msp.system}")
    private String wmsMSPSystem;

    @Value("${wms.hy.system}")
    private String wmsHYSystem;

    @Value("${wms.wj.url}")
    private String wjUrl;

    @Value("${wms.wj.appId}")
    private String appId;

    @Value("${wms.wj.secretKey}")
    private String secretKey;

    @Value("${wms.msp.url}")
    private String mspUrl;

    @Value("${send.host}")
    private String host;

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //采购订单推送
    public void process() {
        logger.info("start.........");
        //一.查询需要推送的NC采购单信息及明细列表
        List<PushPoOrder> resultPOrder = outputPushPoOrder();
        //二. 把订单列表推送到各个wms系统
        pushOrder(resultPOrder);
        logger.info("end.....");
    }

    private void pushOrder(List<PushPoOrder> resultPOrder) {
        for (PushPoOrder pushPoOrder : resultPOrder) {
            //查询第三方wms配置表
            WarehouseConfig warehouseConfig = warehouseConfigMapper.getByNcWarehouseCode(pushPoOrder.getWarehouseCode().toUpperCase());
            if (warehouseConfig == null) {
                OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushPoOrder.getPkOrder());
                logger.info(orderExchangeInfo.getRequestContent() + ";仓库编码不存在对应wms系统仓库编码,采购单号为：" + pushPoOrder.getPkOrder() + "，对应仓库编码为：" + pushPoOrder.getWarehouseCode());
                orderExchangeInfo.setRequestContent(orderExchangeInfo.getRequestContent() + ";仓库编码不存在对应wms系统仓库编码,采购单号为：" + pushPoOrder.getPkOrder() + "，对应仓库编码为：" + pushPoOrder.getWarehouseCode());
                orderExchangeInfo.setPushChannel("");
                orderExchangeInfo.setUpdatedUser("");
                orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                continue;
            }
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushPoOrder.getPkOrder());

            if (warehouseConfig.getWmsShort().equals(wmsWJSystem)) {
                //转换成wj需要的json格式数据
                String json = convertToWJJson(pushPoOrder);
                remoteClient(orderExchangeInfo, wjUrl, json, wmsWJSystem);
                orderExchangeInfo.setPushChannel(wmsWJSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsMSPSystem)) {
                //转换成wj需要的json格式数据
                String json = convertToMSPJson(pushPoOrder, warehouseConfig.getWmsWarehouseCode());
                remoteClient(orderExchangeInfo, mspUrl, json, wmsMSPSystem);
                orderExchangeInfo.setPushChannel(wmsMSPSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsHYSystem)) {
                //转换成wj需要的json格式数据
                System.out.println("调用 " + warehouseConfig.getWmsName());
                orderExchangeInfo.setPushChannel(wmsHYSystem);
            }
            orderExchangeInfo.setUpdatedUser("");
            orderExchangeInfo.setUpdatedTime(new Date());
            orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
        }
    }

    private List<PushPoOrder> outputPushPoOrder() {
        List<PushPoOrder> resultPOrder = new ArrayList<PushPoOrder>();

        //一. 查询已审核的采购单主表
        List<POrder> pOrderList = pOrderMapper.getPOrders();
        for (POrder pOrder : pOrderList) {
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pOrder.getPkOrder());
            //1、过滤掉已经推送成功的采购订单
            if (orderExchangeInfo == null || (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount)) {
                PushPoOrder pushPoOrder = new PushPoOrder(pOrder);

                if (orderExchangeInfo == null) {
                    OrderExchangeInfo newOrderExchangeInfo = new OrderExchangeInfo(pOrder, pushOrderPOrder);
                    orderExchangeInfoMapper.insertOrderExchangeInfo(newOrderExchangeInfo);
                } else {
                    orderExchangeInfo.setUpdatedUser("");
                    orderExchangeInfo.setPushChannel("");
                    orderExchangeInfo.setUpdatedTime(new Date());
                    orderExchangeInfo.setResponseContent("");
                    orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                }
                //根据采购订单主ID获取采购订单明细
                List<POrderDetail> allPOrderDetailList = pOrderDetailMapper.getPOrdersDetail(pOrder.getPkOrder());

                //过滤掉订单明细中包含多个仓库的采购单
                if (!isSync(allPOrderDetailList, pOrder.getPkOrder())) {
                    continue;
                }

                List<POrderDetail> pushPoOrderDetailList = new ArrayList<>();
                for (POrderDetail pOrderDetail : allPOrderDetailList) {
                    pOrderDetail.setBillCode(pOrder.getBillCode());
                    pOrderDetail.setPkOrderHeader(pOrder.getPkOrder());
                    pushPoOrderDetailList.add(pOrderDetail);
                }

                pushPoOrder.setWarehouseCode(allPOrderDetailList.get(0).getWarehouseCode());
                pushPoOrder.setItems(pushPoOrderDetailList);
                resultPOrder.add(pushPoOrder);
            }
        }

        return resultPOrder;
    }

    //调用封装的远程访问
    private void remoteClient(OrderExchangeInfo orderExchangeInfo, String url, String json, String wmsSystem) {
        try {
            logger.info("start remoteClient...........");
            Response response = new HttpClientUtils().post(url, json);
            orderExchangeInfo.setRequestContent(json);
            orderExchangeInfo.setResponseTime(new Date());
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            logger.info("response data:" + jsonObject.toString());
            boolean isSuccess = false;
            if (wmsSystem.equals(wmsMSPSystem)) {
                isSuccess = jsonObject.get("status").toString().equals("success");
            }
            if (wmsSystem.equals(wmsWJSystem)) {

            }
            if (wmsSystem.equals(wmsHYSystem)) {

            }

            if (response.isSuccessful()) {
                orderExchangeInfo.setResponseContent(jsonObject.toString());
                //推送成功或者（对方返回的结果是处理失败并且推送处理次数小于设定的次数）
                if (isSuccess || (orderExchangeInfo.getPushProcessCount() > Integer.parseInt(processResultFailMaxCount) && !isSuccess)) {
                    orderExchangeInfo.setPushStatus(pushOrderSuccess);
                } else {
                    orderExchangeInfo.setPushProcessCount(orderExchangeInfo.getPushProcessCount() + 1);
                }
            } else {
                orderExchangeInfo.setResponseContent(jsonObject.toString());
            }
            logger.info("end remoteClient...........");
        } catch (IOException e) {
            logger.error("remoteClient fail:" + e.getMessage());
            orderExchangeInfo.setResponseContent(e.getMessage());
        }
    }


    private String convertToWJJson(PushPoOrder pushPoOrder) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bizContent = new StringBuilder();
        stringBuilder.append("{\"action\": \"weijie.wms.purchase.add\",\"appId\":\"").append(appId).append("\",");

        bizContent.append("\"bizContent\": {\"purchaseOrderDate\":\"").append(new Date()).append("\",\"goodsSupplierCode\": \"").append(pushPoOrder.getPkSupplierCode()).append("\",\"goodsSupplierName\": \"").append(pushPoOrder.getPkSupplierName()).append("\",\"upstreamNumber\": \"").append(pushPoOrder.getBillCode()).append("\", \"transportNumber\": \"\", \"contractNumber\": \"\", \"areaCode\": \"\", \"provinceCode\": \"\", \"cityCode\": \"\", \"countyCode\": \"\", \"province\": \"\", \"city\": \"\", \"county\": \"\", \"wjCompanyCode\": \"\", \"wjWarehouseCode\": \"\", \"remark\": \"\",");
        bizContent.append("\"goods\": [");
        for (POrderDetail pOrderDetail : pushPoOrder.getItems()) {
            bizContent.append("{\"goodsNo\": \"").append(pOrderDetail.getMaterialCode()).append("\",\"batchNo\": \"\",\"goodsCount\": \"").append(pOrderDetail.getQuantity()).append("\",\"goodsUnit\": \"\",\"goodsPrice\": \"\",\"goodsTotalPrice\": \"\",\"goodsWeight\": \"\",\"goodsSize\": \"\",\"goodsPromoteType\": \"\",\"extendInfo\":{\"cfirstbillhid\":\"").append(pOrderDetail.getPkOrderHeader()).append("\",\"cfirstbillbid\": \"").append(pOrderDetail.getPkOrderBody()).append("\", \"vsourcebillcode\": \"").append(pushPoOrder.getBillCode()).append("\"}, \"remark\": \"\"},");
        }
        bizContent = bizContent.delete(bizContent.length() - 1, bizContent.length());

        bizContent.append("]");
        bizContent.append("}");

        String sign = MD5EncoderUtil.MD5Encoder(bizContent.toString() + secretKey, "utf-8");
        stringBuilder.append("\"sign\": \"").append(sign).append("\",");
        stringBuilder.append("\"charset\": \"utf-8\",\"version\": \"1.0\",\"timestamp\":\"").append(fmt.format(new Date())).append("\",");

        stringBuilder.append(bizContent);
        stringBuilder.append("}");
        return stringBuilder.toString();

    }

    private String convertToMSPJson(PushPoOrder pushPoOrder, String wmsWarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(wmsWarehouseCode).append("\",\"customer_number\": \"").append(pushPoOrder.getBillCode()).append("\",\"delivery_address\": \"\",\"sign_up\": \"\",\"sign_up_tel\": \"\",\"remarks\": \"\"},");
        stringBuilder.append("\"goods\": [");
        for (POrderDetail pOrderDetail : pushPoOrder.getItems()) {
            stringBuilder.append("{\"goods_name\": \"").append(pOrderDetail.getMaterialName()).append("\",\"goods_number\": \"").append(pOrderDetail.getMaterialCode()).append("\",\"gifts\": \"").append(pOrderDetail.getIsGift()).append("\",\"specification\": \"\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"\",\"large_unit_quantity\": \"\",\"small_unit_quantity\": \"").append(pOrderDetail.getQuantity()).append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"21\",\"cfirstbillhid\": \"").append(pOrderDetail.getPkOrderHeader()).append("\",\"cfirstbillbid\": \"").append(pOrderDetail.getPkOrderBody()).append("\",\"vsourcebillcode\": \"").append(pushPoOrder.getBillCode()).append("\",\"remark\": \"\"},");
        }
        stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        stringBuilder.append("],");

        stringBuilder.append("\"supply\": {\"name\": \"").append(pushPoOrder.getPkSupplierName()).append("\",\"code\": \"").append(pushPoOrder.getPkSupplierCode()).append("\"}");
        stringBuilder.append("}");
        return stringBuilder.toString();

    }

    //过滤仓库编码不一致的采购订单
    private boolean isSync(List<POrderDetail> pOrderDetailList, String pkOrderId) {
        boolean flag = false;
        Set<String> warehouseCodeSet = new HashSet<>();
        for (POrderDetail pOrderDetail : pOrderDetailList) {
            warehouseCodeSet.add(pOrderDetail.getWarehouseCode());
        }
        if (warehouseCodeSet.size() == 1) {
            flag = true;
        } else {
            logger.info("采购订单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }
}
