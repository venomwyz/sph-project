package com.atguigu.gmall.list.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "service-list",path = "/api/list",contextId ="listFeign")
public interface ListFeign {

    /**
     * 搜索查询
     * @param
     * @return
     */
    @GetMapping("/getSearch")
    public Map<String, Object> getSearch(@RequestParam Map<String,String> searchDate);
}
