package com.xinliangjishipin.pushwms.service;

import com.xinliangjishipin.pushwms.entity.POrder;
import com.xinliangjishipin.pushwms.mapper.POrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class POrderService {

    @Autowired
    private POrderMapper pOrderMapper;

    public List<POrder> getOrders() {
        return pOrderMapper.getPOrders();
    }
}
