package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.POrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface POrderMapper {
    List<POrder> getPOrders();
}
