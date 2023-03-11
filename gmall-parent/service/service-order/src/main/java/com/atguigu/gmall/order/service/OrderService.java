package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;

/**
 * 订单service
 */
public interface OrderService {
    /**
     * 添加订单与详情
     * @param orderInfo
     */
    void addOrderAndDetail(OrderInfo orderInfo);

    /**
     * 取消订单，回滚库从
     * @param orderId
     */
    void rollbackStock(Long orderId);

    /**
     * 获取微信的支付信息
     * @param resultPay
     */
    void updatePayStatus(String resultPay);

    /**
     * 判断用户支付的渠道，目的是为了知道用户多渠道支付，造成的重复支付后果
     * ，这也是用户支付的第一步
     * @return
     */

    PaymentInfo getPayment(String orderId, String paywey);


}
