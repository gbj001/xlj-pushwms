package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.OrderExchangeInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderExchangeInfoMapper {

    OrderExchangeInfo getByOutOrderId(String outOrderId);

    void updateOrderExchangeInfo(OrderExchangeInfo orderExchangeInfo);

    void insertOrderExchangeInfo(OrderExchangeInfo orderExchangeInfo);
}
