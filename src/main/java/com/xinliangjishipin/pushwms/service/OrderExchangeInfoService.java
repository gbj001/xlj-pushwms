package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.*;
import com.xinliangjishipin.pushwms.mapper.OrderExchangeInfoMapper;
import com.xinliangjishipin.pushwms.mapper.POrderDetailMapper;
import com.xinliangjishipin.pushwms.mapper.POrderMapper;
import com.xinliangjishipin.pushwms.mapper.WarehouseConfigMapper;
import com.xinliangjishipin.pushwms.utils.HttpClientUtils;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    @Value("${wms.msp.url}")
    private String mspUrl;


    //采购订单推送
    public void process() {
        //一.查询需要推送的NC采购单信息及明细列表
        List<PushPoOrder> resultPOrder = outputPushPoOrder();
        //二. 把订单列表推送到各个wms系统
        pushOrder(resultPOrder);
    }

    private void pushOrder(List<PushPoOrder> resultPOrder){
        for (PushPoOrder pushPoOrder : resultPOrder) {
            //查询第三方wms配置表
            WarehouseConfig warehouseConfig = warehouseConfigMapper.getByNcWarehouseCode(pushPoOrder.getWarehouseCode().toUpperCase());
            if (warehouseConfig == null) {
                OrderExchangeInfo orderExchangeInfo = orderExchangeInfoMapper.getByOutOrderId(pushPoOrder.getPkOrder());
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
                remoteClient(orderExchangeInfo, wjUrl, json);
                orderExchangeInfo.setPushChannel(wmsWJSystem);
            }
            if (warehouseConfig.getWmsShort().equals(wmsMSPSystem)) {
                //转换成wj需要的json格式数据
                String json = convertToMSPJson(pushPoOrder);
                remoteClient(orderExchangeInfo, mspUrl, json);
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

    private List<PushPoOrder> outputPushPoOrder(){
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
    private void remoteClient(OrderExchangeInfo orderExchangeInfo, String url, String json) {
        try {
            Response response = new HttpClientUtils().post(wjUrl, json);
            orderExchangeInfo.setRequestContent(json);
            orderExchangeInfo.setResponseTime(new Date());
            if (response.isSuccessful()) {
                orderExchangeInfo.setResponseContent(response.body().string());
                orderExchangeInfo.setPushStatus(pushOrderSuccess);
            } else {
                orderExchangeInfo.setResponseContent(response.body().string());
            }
        } catch (IOException e) {
            orderExchangeInfo.setResponseContent(e.getMessage());
        }
    }


    private String convertToWJJson(PushPoOrder pushPoOrder) {
        String json = "{'order':{'age':32},}";

        return json;

    }

    private String convertToMSPJson(PushPoOrder pushPoOrder) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"order\": {\"warehouse_number\":\"").append(pushPoOrder.getWarehouseCode()).append("\",\"customer_number\": \"").append(pushPoOrder.getBillCode()).append("\",\"delivery_address\": \"\",\"sign_up\": \"\",\"sign_up_tel\": \"\",\"remarks\": \"\"},");
        stringBuilder.append("\"goods\": [");
        for (POrderDetail pOrderDetail : pushPoOrder.getItems()) {
            stringBuilder.append("{\"goods_name\": \"").append(pOrderDetail.getMaterialName()).append("\",\"goods_number\": \"").append(pOrderDetail.getMaterialName()).append("\",\"gifts\": \"").append(pOrderDetail.getIsGift()).append("\",\"specification\": \"\",\"barcode\": \"\",\"color\": \"\",\"style\": \"\",\"weight\": \"\",\"volume\": \"\",\"large_unit_quantity\": \"\",\"small_unit_quantity\": \"").append(pOrderDetail.getQuantity()).append("\",\"cost\": \"\",\"temperature\": \"\",\"humidity\": \"\",\"csourcetype\": \"21\",\"cfirstbillhid\": \"").append(pOrderDetail.getPkOrderHeader()).append("\",\"cfirstbillbid\": \"").append(pOrderDetail.getPkOrderBody()).append("\",\"vsourcebillcode\": \"").append(pushPoOrder.getBillCode()).append("\",\"remark\": \"\"},");
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
            logger.error("采购订单明细中仓库编码大于1个：" + pkOrderId);
        }
        return flag;
    }
}
