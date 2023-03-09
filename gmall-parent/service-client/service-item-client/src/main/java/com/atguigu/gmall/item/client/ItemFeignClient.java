package com.atguigu.gmall.item.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "service-item" , path = "/api/item",contextId = "itemFeign")
public interface ItemFeignClient {


    /**
     * 查询sku数据
     * @param skuId
     * @return
     */
    @GetMapping("/selectBySkuId/{skuId}")
    public Map selectBySkuId(@PathVariable("skuId") Long skuId);


}
