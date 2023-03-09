package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 远程调用的sku的控制层
 */
@RestController
@RequestMapping("api/product")
public class ItemController {
    @Autowired
    private ItemService itemService;
    /**
     * 根据id查询sku属性
     * @param
     * @return
     */
    @GmallCache(prefix = "selectBySkuId:")
    @GetMapping("selectBySkuId/{skuId}")
    public SkuInfo selectBySkuId(@PathVariable("skuId") Long skuId){
        return itemService.getPageInfo(skuId);


    }

    /**
     * 分类查询
     * @param category3Id
     * @return
     */
    @GmallCache(prefix = "getCategory:")
    @GetMapping("getCategory/{category3Id}")
    public BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 图片查询
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "getImage:")
    @GetMapping("getSkuImage/{skuId}")
    public List<SkuImage> getImage(@PathVariable("skuId") Long skuId){
        List<SkuImage> spuImage = itemService.getSpuImage(skuId);
        return spuImage;
    }
    @GmallCache(prefix = "getPrice:")
    @GetMapping("getPrice/{skuId}")
    public BigDecimal getPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price = itemService.getPrice(skuId);
        return price;

    }
    /**
     * 查询销售属性
     */
    @GmallCache(prefix = "getSpuSaleAttr:")
    @GetMapping("getSpuSaleAttr/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable("skuId") Long skuId,
                                            @PathVariable("spuId") Long spuId){
        return itemService.getSpuSaleAttr(skuId,spuId);

    }

    /**
     * 页面跳转
     * @param spuId
     * @return
     */
    @GmallCache(prefix = "getSkuIdAndValues:")
    @GetMapping("getSkuIdAndValues/{spuId}")
    public Map getSkuIdAndValues(@PathVariable("spuId") Long spuId){
        return itemService.getSkuIdAndValues(spuId);
    }

    /**
     * 品牌查询
     * @return
     */
    @GetMapping("getTradeMake/{id}")
    public BaseTrademark getTradeMake(@PathVariable("id")Long id){
        BaseTrademark mark = itemService.getMark(id);
        return mark;
    }

    /**
     * 属性查询
     * @param skuId
     * @return
     */
//    @GmallCache(prefix = "getAttrInfo:")
    @GetMapping("getAttrInfo/{skuId}")
    public List<BaseAttrInfo> getAttrInfo(@PathVariable("skuId") Long skuId){
        List<BaseAttrInfo> baseAttrInfoBySkuId = itemService.getBaseAttrInfoBySkuId(skuId);
        return baseAttrInfoBySkuId;
    }
    /**
     * 扣减库从
     */
    @GetMapping("decount")
    public Boolean decount(@RequestParam Map decountMap){
        try {
            return itemService.decountBase(decountMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @GetMapping("rollbackCart")
    public Boolean rollbackCart(@RequestParam Map rollbackCart){

        try {
            return itemService.rollbackStock(rollbackCart);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


}
