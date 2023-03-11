package com.atguigu.gmall.payment.service;

import java.util.Map;

public interface WxPayService {

    /**
     * 调用微信接口
     * @param body
     * @param orderId
     * @param amount
     * @return
     */
    Map<String, String> toWxPayApi(String body, String orderId, String amount);

    /**
     * 获取返回支付结果
     * @param orderId
     * @return
     */
    Map<String, String> resultWxpay(String orderId);



}
