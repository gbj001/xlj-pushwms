package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.POrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface POrderDetailMapper {
    List<POrderDetail> getPOrdersDetail(String pkOrder);
}
