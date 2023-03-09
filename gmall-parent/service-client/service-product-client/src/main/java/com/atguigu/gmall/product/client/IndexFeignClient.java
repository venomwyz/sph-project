package com.atguigu.gmall.product.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "service-product",path = "/index/product", contextId ="indexFeign")
public interface IndexFeignClient {
    @GetMapping("category")
    public List<JSONObject> indexCategory();
}
