package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.mapper.BaseCategoryViewMapper;
import com.atguigu.gmall.product.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/index/product")
public class IndexController {
    /**
     * 前端页面调用的Controller
     */
    @Resource
    private IndexService indexService;
    @GetMapping("category")
    public List<JSONObject> indexCategory(){
         List<JSONObject> categoryAll = indexService.getCategoryAll();
         return categoryAll;
    }
}
