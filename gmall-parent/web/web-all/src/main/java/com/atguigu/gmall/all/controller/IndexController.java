package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.product.client.IndexFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexFeignClient indexFeignClient;

    /**
     * index页面展示
     * @param model
     * @return
     */
    @GetMapping("category")
    public String category(Model model){
        List<JSONObject> jsonObjects = indexFeignClient.indexCategory();
        model.addAttribute("categoryList",jsonObjects);
        return "index";
    }
}
