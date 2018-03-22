package com.xinliangjishipin.pushwms.mapper;

import com.xinliangjishipin.pushwms.entity.WarehouseConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WarehouseConfigMapper {
    WarehouseConfig getByNcWarehouseCode(String warehouseCode);
}
