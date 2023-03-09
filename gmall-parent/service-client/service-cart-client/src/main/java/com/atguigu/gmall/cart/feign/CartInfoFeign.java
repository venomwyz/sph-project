package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "service-cart", path = "api/cart",contextId = "cartInfoFeign")
public interface CartInfoFeign  {

    /**
     * 获取购物车金额
     * @return
     */
    @GetMapping("getConfirmCart")
    public Map<String, Object> getMoney();

    /**
     * 清空购物车接口
     * @return
     */
    @GetMapping("deleteCart")
    public Boolean delete();
}
