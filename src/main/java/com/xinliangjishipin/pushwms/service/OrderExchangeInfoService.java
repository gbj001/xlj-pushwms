package com.xinliangjishipin.pushwms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.mapper.PurchaseOrderDetailMapper;
import com.xinliangjishipin.pushwms.mapper.PurchaseOrderMapper;
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

/**
 * @author gengbeijun
 */
@Service
public class OrderExchangeInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderExchangeInfoMapper orderExchangeInfoMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private PurchaseOrderDetailMapper purchaseOrderDetailMapper;

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

    @Value("${wms.wj.companyCode}")
    private String wjCompanyCode;

    @Value("${wms.msp.url}")
    private String mspUrl;

    @Value("${send.host}")
    private String host;

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 采购订单推送处理方法
     */
    public void process() {
        logger.info("start.........");
        List<PushPoOrder> resultPOrder = outputPushPoOrder();
        pushOrder(resultPOrder);
        logger.info("end.....");
    }

    /**
     * 采购订单推送
     */
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
                String json = convertToWJJson(pushPoOrder, warehouseConfig.getWmsWarehouseCode() );
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
                orderExchangeInfo.setPushChannel(wmsHYSystem);
            }
            orderExchangeInfo.setUpdatedUser("");
            orderExchangeInfo.setUpdatedTime(new Date());
            orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
        }
    }

    /**
     * 从NC获取需要推送的采购订单
     */
    private List<PushPoOrder> outputPushPoOrder() {
        List<PushPoOrder> resultPOrder = new ArrayList<PushPoOrder>();

        //一. 查询已审核的采购单主表
        List<PurchaseOrder> purchaseOrderList = purchaseOrderMapper.getPurchaseOrders();
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(purchaseOrder.getPkOrder());
            //1、过滤掉已经推送成功的采购订单
            boolean flag = orderExchangeInfo == null || (orderExchangeInfo != null && orderExchangeInfo.getPushStatus().equals(pushOrderFail) && orderExchangeInfo.getPushCount() < pushOrderMaxCount);
            if (flag) {
                PushPoOrder pushPoOrder = new PushPoOrder(purchaseOrder);

                if (orderExchangeInfo == null) {
                    OrderExchangeInfo newOrderExchangeInfo = new OrderExchangeInfo(purchaseOrder, pushOrderPOrder);
                    orderExchangeInfoMapper.insertOrderExchangeInfo(newOrderExchangeInfo);
                } else {
                    orderExchangeInfo.setUpdatedUser("");
                    orderExchangeInfo.setPushChannel("");
                    orderExchangeInfo.setUpdatedTime(new Date());
                    orderExchangeInfo.setResponseContent("");
                    orderExchangeInfoMapper.updateOrderExchangeInfo(orderExchangeInfo);
                }
                //根据采购订单主ID获取采购订单明细
                List<PurchaseOrderDetail> allPurchaseOrderDetailList = purchaseOrderDetailMapper.getPurchaseOrderDetail(purchaseOrder.getPkOrder());

                //过滤掉订单明细中包含多个仓库的采购单
                if (!isSync(allPurchaseOrderDetailList, purchaseOrder.getPkOrder())) {
                    continue;
                }

                List<PurchaseOrderDetail> pushPoOrderDetailList = new ArrayList<>();
                for (PurchaseOrderDetail purchaseOrderDetail : allPurchaseOrderDetailList) {
                    purchaseOrderDetail.setBillCode(purchaseOrder.getBillCode());
                    purchaseOrderDetail.setPkOrderHeader(purchaseOrder.getPkOrder());
                    pushPoOrderDetailList.add(purchaseOrderDetail);
                }

                pushPoOrder.setWarehouseCode(allPurchaseOrderDetailList.get(0).getWarehouseCode());
                pushPoOrder.setItems(pushPoOrderDetailList);
                resultPOrder.add(pushPoOrder);
            }
        }

        return resultPOrder;
    }


    /**
     * 调用封装的远程访问
     * */
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
                isSuccess = Objects.equals(jsonObject.get("status").toString(), "success");
            }
            if (wmsSystem.equals(wmsWJSystem)) {
                isSuccess =  Objects.equals(jsonObject.getJSONObject("result").getJSONObject("body").get("code"),100);
            }
            if (wmsSystem.equals(wmsHYSystem)) {

            }

            if (response.isSuccessful()) {
                orderExchangeInfo.setResponseContent(jsonObject.toString());
                boolean flag = isSuccess || (orderExchangeInfo.getPushProcessCount() > Integer.parseInt(processResultFailMaxCount) && !isSuccess);
                if (flag) {
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

    /**
     * 转换成唯捷需要的json格式
     * */
    private String convertToWJJson(PushPoOrder pushPoOrder, String wmswarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder bizContent = new StringBuilder();
        stringBuilder.append("{\"action\": \"weijie.wms.purchase.add\",\"appId\":\"").append(appId).append("\",");

        //bizContent.append("{\\\"purchaseOrderDate\\\":\\\"").append(fmt.format(pushPoOrder.getdMakeDate())).append("\\\",\\\"goodsSupplierCode\\\": \\\"").append(pushPoOrder.getPkSupplierCode()).append("\\\",\\\"goodsSupplierName\\\": \\\"").append(pushPoOrder.getPkSupplierName()).append("\\\",\\\"upstreamNumber\\\": \\\"").append("CD2018031900000157").append("\\\", \\\"transportNumber\\\": \\\"\\\", \\\"contractNumber\\\": \\\"\\\", \\\"areaCode\\\": \\\"310000\\\", \\\"provinceCode\\\": \\\"\\\", \\\"cityCode\\\": \\\"\\\", \\\"countyCode\\\": \\\"\\\", \\\"province\\\": \\\"\\\", \\\"city\\\": \\\"\\\", \\\"county\\\": \\\"\\\", \\\"wjWarehouseCode\\\":\\\"").append(wmswarehouseCode).append("\\\", \\\"wjCompanyCode\\\": \\\"").append(wjCompanyCode).append("\\\", \\\"remark\\\": \\\"\\\",");
        bizContent.append("{\\\"purchaseOrderDate\\\":\\\"").append(fmt.format(pushPoOrder.getdMakeDate())).append("\\\",\\\"goodsSupplierCode\\\": \\\"").append(pushPoOrder.getPkSupplierCode()).append("\\\",\\\"goodsSupplierName\\\": \\\"").append(pushPoOrder.getPkSupplierName()).append("\\\",\\\"upstreamNumber\\\": \\\"").append(pushPoOrder.getBillCode()).append("\\\", \\\"transportNumber\\\": \\\"\\\", \\\"contractNumber\\\": \\\"\\\", \\\"areaCode\\\": \\\"310000\\\", \\\"provinceCode\\\": \\\"\\\", \\\"cityCode\\\": \\\"\\\", \\\"countyCode\\\": \\\"\\\", \\\"province\\\": \\\"\\\", \\\"city\\\": \\\"\\\", \\\"county\\\": \\\"\\\", \\\"wjWarehouseCode\\\":\\\"").append(wmswarehouseCode).append("\\\", \\\"wjCompanyCode\\\": \\\"").append(wjCompanyCode).append("\\\", \\\"remark\\\": \\\"\\\",");
        bizContent.append("\\\"goods\\\": [");
        for (PurchaseOrderDetail purchaseOrderDetail : pushPoOrder.getItems()) {
            bizContent.append("{\\\"goodsNo\\\": \\\"").append(purchaseOrderDetail.getMaterialCode()).append("\\\",\\\"batchNo\\\": \\\"\\\",\\\"goodsCount\\\": \\\"").append(purchaseOrderDetail.getQuantity()).append("\\\",\\\"goodsUnit\\\": \\\"").append(purchaseOrderDetail.getMeasureUnits()).append("\\\",\\\"goodsPrice\\\": \\\"0\\\",\\\"goodsTotalPrice\\\": \\\"\\\",\\\"goodsWeight\\\": \\\"\\\",\\\"goodsSize\\\": \\\"\\\",\\\"goodsPromoteType\\\": \\\"\\\",\\\"extendInfo\\\":{\\\"cfirstbillhid\\\":\\\"").append(purchaseOrderDetail.getPkOrderHeader()).append("\\\",\\\"cfirstbillbid\\\": \\\"").append(purchaseOrderDetail.getPkOrderBody()).append("\\\", \\\"vsourcebillcode\\\": \\\"").append(pushPoOrder.getBillCode()).append("\\\"}, \\\"remark\\\": \\\"\\\"},");
            //bizContent.append("{\\\"goodsNo\\\": \\\"").append(purchaseOrderDetail.getMaterialCode()).append("\\\",\\\"batchNo\\\": \\\"\\\",\\\"goodsCount\\\": \\\"").append(purchaseOrderDetail.getQuantity()).append("\\\",\\\"goodsUnit\\\": \\\"").append(purchaseOrderDetail.getMeasureUnits()).append("\\\",\\\"goodsPrice\\\": \\\"0\\\",\\\"goodsTotalPrice\\\": \\\"\\\",\\\"goodsWeight\\\": \\\"\\\",\\\"goodsSize\\\": \\\"\\\",\\\"goodsPromoteType\\\": \\\"\\\",\\\"extendInfo\\\":{\\\"cfirstbillhid\\\":\\\"").append(purchaseOrderDetail.getPkOrderHeader()).append("\\\",\\\"cfirstbillbid\\\": \\\"").append(purchaseOrderDetail.getPkOrderBody()).append("\\\", \\\"vsourcebillcode\\\": \\\"").append("CD2018031900000157").append("\\\"}, \\\"remark\\\": \\\"\\\"},");
        }
        bizContent = bizContent.delete(bizContent.length() - 1, bizContent.length());
        bizContent.append("]");
        bizContent.append("}\"");

        StringBuilder signStr = new StringBuilder();
        signStr.append(bizContent.delete(bizContent.length() - 1, bizContent.length()).toString().replaceAll("\\\\",""));
        String sign = MD5EncoderUtil.MD5Encoder(signStr.toString() + secretKey);

        stringBuilder.append("\"sign\": \"").append(sign).append("\",");
        stringBuilder.append("\"charset\": \"utf-8\",\"version\": \"1.0\",\"timestamp\":\"").append(fmt.format(new Date())).append("\",");

        stringBuilder.append(bizContent.insert(0,"\"bizContent\":\""));
        stringBuilder.append("\"}");
        return stringBuilder.toString();

    }

    /**
     * 转换成码上配需要的json格式
     * */
    private String convertToMSPJson(PushPoOrder pushPoOrder, String wmsWarehouseCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(wmsWarehouseCode).append("\",\"customer_number\": \"").append(pushPoOrder.getBillCode()).append("\",\"delivery_address\": \"\",\"sign_up\": \"\",\"sign_up_tel\": \"\",\"remarks\": \"\"},");
        stringBuilder.append("\"goods\": [");
        for (PurchaseOrderDetail purchaseOrderDetail : pushPoOrder.getItems()) {
            stringBuilder.append("{\"goods_name\": \"").append(purchaseOrderDetail.getMaterialName()).append("\",\"goods_number\": \"").append(purchaseOrderDetail.getMaterialCode()).append("\",\"gifts\": \"").append(purchaseOrderDetail.getIsGift()).append("\",\"specification\": \"\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"\",\"large_unit_quantity\": \"\",\"small_unit_quantity\": \"").append(purchaseOrderDetail.getQuantity()).append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"21\",\"cfirstbillhid\": \"").append(purchaseOrderDetail.getPkOrderHeader()).append("\",\"cfirstbillbid\": \"").append(purchaseOrderDetail.getPkOrderBody()).append("\",\"vsourcebillcode\": \"").append(pushPoOrder.getBillCode()).append("\",\"remark\": \"\"},");
        }
        stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        stringBuilder.append("],");

        stringBuilder.append("\"supply\": {\"name\": \"").append(pushPoOrder.getPkSupplierName()).append("\",\"code\": \"").append(pushPoOrder.getPkSupplierCode()).append("\"}");
        stringBuilder.append("}");
        return stringBuilder.toString();

    }

    /**
     * 过滤仓库编码不一致的采购订单
     * */
    private boolean isSync(List<PurchaseOrderDetail> purchaseOrderDetailList, String pkOrderId) {
        boolean flag = false;
        Set<String> warehouseCodeSet = new HashSet<>();
        for (PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetailList) {
            warehouseCodeSet.add(purchaseOrderDetail.getWarehouseCode());
        }
        if (warehouseCodeSet.size() == 1) {
            flag = true;
        } else {
            logger.info("采购订单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }
}
