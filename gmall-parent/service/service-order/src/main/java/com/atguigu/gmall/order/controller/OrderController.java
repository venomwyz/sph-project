package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 订单
 */
@RestController
@RequestMapping("api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 订单的插入
     * @param orderInfo
     * @return
     */
    @PostMapping("addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderService.addOrderAndDetail(orderInfo);
        return Result.ok();
    }

    /**
     * 订单取消
     * @param orderId
     * @return
     */
    @GetMapping("deleteCart")
    public Result deleteCart(@RequestParam("orderId") Long orderId){
        orderService.rollbackStock(orderId);
        return Result.ok();
    }

    /**
     * 获取渠道信息
     *
     */
    @GetMapping("getOrderPayInfo")
    public Result getUrlMsg(String orderId,String paywey){
        String payment = orderService.getPayment(orderId, paywey);
        return Result.ok(payment);

    }

}
