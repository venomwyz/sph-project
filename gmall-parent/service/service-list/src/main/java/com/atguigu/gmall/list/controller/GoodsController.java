package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/list")
public class GoodsController {
    /**
     * es的查询
     * @param skuId
     */
    @Autowired
    private GoodsService goodsService;
    @GetMapping("addEs/{skuId}")
    public Result addEs(@PathVariable("skuId") Long skuId){
        goodsService.addGoodsByEs(skuId);
        return Result.ok();
    }

    /**
     * 删除
     * @param id
     * @return
     */

    @GetMapping("remove/{id}")
    public Result remove(@PathVariable("id") Long id){
        goodsService.removeGoodsByEs(id);
        return Result.ok();
    }

    /**
     * 获取热度
     * @param id
     * @return
     */
    @GetMapping("addHotScore/{id}")
    public Result addHotScore(@PathVariable("id") Long id){
        goodsService.addHotScore(id);
        return Result.ok();
    }

}
