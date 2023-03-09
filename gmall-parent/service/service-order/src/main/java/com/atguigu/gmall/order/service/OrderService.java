package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

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


}
