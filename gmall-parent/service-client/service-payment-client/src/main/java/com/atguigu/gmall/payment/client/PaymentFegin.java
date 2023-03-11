package com.atguigu.gmall.payment.client;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "service-payment",path = "wx/pay",contextId = "paymentFegin")
public interface PaymentFegin {


    /**
     * 获取二维码连接
     * @param body
     * @param orderId
     * @param amount
     * @return
     */
    @GetMapping("getUrl")
    public Map<String, String> getUrl(@RequestParam String body,
                                      @RequestParam String orderId,
                                      @RequestParam String amount);
}
