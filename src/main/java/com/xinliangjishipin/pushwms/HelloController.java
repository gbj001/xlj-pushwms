package com.xinliangjishipin.pushwms;

import com.xinliangjishipin.pushwms.service.OrderExchangeInfoService;
import com.xinliangjishipin.pushwms.service.POrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private POrderService pOrderService;

    @Autowired
    private OrderExchangeInfoService orderExchangeInfoService;

    @GetMapping(value = "/test")
    public String getOrfffders(){
        orderExchangeInfoService.process();
        return "sss";
    }
}
