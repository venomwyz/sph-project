package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * feign控制层调用
 */
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Resource
    private ItemService itemService;


    /**
     * 查询sku数据
     * @param skuId
     * @return
     */
    @GetMapping("/selectBySkuId/{skuId}")
    public Map selectBySkuId(@PathVariable("skuId") Long skuId){
        return itemService.getItemPageInfo(skuId);
    }


}
