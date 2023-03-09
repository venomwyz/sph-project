package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/product")
public class TestRedisController {
    /**
     * redis测试
     * @return
     */
    @Resource
    private TestRedisService testRedisService;
    @GetMapping("redis")
    public Result redis(){
        testRedisService.setRedisAndRedission();
        return Result.ok();
    }
}
