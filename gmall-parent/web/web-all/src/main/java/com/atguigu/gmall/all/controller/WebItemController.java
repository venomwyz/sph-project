package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import com.atguigu.gmall.product.client.IndexFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/page")
public class WebItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;
    @Autowired
    private IndexFeignClient indexFeignClient;

    @GetMapping("item")
    public String item(Long skuId, Model model){
        Map map = itemFeignClient.selectBySkuId(skuId);
        model.addAllAttributes(map);
        return "item";
    }

    /**
     * 生成静态页面
     * @return
     */
    @Autowired
    private TemplateEngine templateEngine;
    @GetMapping("/itemTemplate")
    @ResponseBody
    public String itemTemplate(Long skuId){

        try {
            //数据
            Context context = new Context();
            Map map = itemFeignClient.selectBySkuId(skuId);
            context.setVariables(map);
            //文件输出的位置
            File file = new File("E:\\", skuId + ".html");
            PrintWriter printWriter = new PrintWriter(file, "UTF-8");
            //方法调用
            templateEngine.process("itemTemplate",context,printWriter);
            //流的刷新与关闭
            printWriter.flush();
            printWriter.close();
            return "生成成功";
        }catch (Exception e){
          e.printStackTrace();
        }
        return "生成失败";
    }





}
