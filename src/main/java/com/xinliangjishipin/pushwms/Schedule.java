package com.xinliangjishipin.pushwms;


import com.xinliangjishipin.pushwms.service.OrderExchangeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Schedule {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${thirdwms}")
    private String thirdwms;

    @Autowired
    private OrderExchangeInfoService orderExchangeInfoService;

    //采购订单推送计划任务，每5分钟执行一次
    @Scheduled(cron="0 0/1 * * * ?")
    public void poOrderPush(){
        logger.info("start PoOrderPushTask......");
        long startTime = System.currentTimeMillis();
        orderExchangeInfoService.process();
        long endTime = System.currentTimeMillis();
        logger.info("end PoOrderPushTask....., totalTime:" + (endTime - startTime)/1000 + " seconds");
    }

    //销售订单推送计划任务，每1分钟执行一次
    @Scheduled(fixedRate = 1000000)
    public void soSaleOrderPush() {
        logger.info("start SoSaleOrderPushTask......");
        long startTime = System.currentTimeMillis();
        //logger.info("person list = " + this.personService.personList() + ": " + thirdwms);
        long endTime = System.currentTimeMillis();
        logger.info("end SoSaleOrderPushTask......, totalTime:" + (endTime - startTime)/1000 + " seconds");
    }

}
