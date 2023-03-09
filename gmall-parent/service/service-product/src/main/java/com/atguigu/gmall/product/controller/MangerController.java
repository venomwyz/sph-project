package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.ProductConstant;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.MangerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 平台属性分类的控制层
 */
@RestController
@RequestMapping("admin/product")
public class MangerController {
    @Autowired
    private MangerService mangerService;



    /**
     * 查询所有的一级分类
     * @return
     */
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> all = mangerService.findAll();
        return Result.ok(all);


    }

    /**
     * 查询所有的二级分类
     * @param category1Id
     * @return
     */

    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> all2 = mangerService.findAll2(category1Id);
        return Result.ok(all2);
    }

    /**
     * 查询所有的三级分类
     * @param category2Id
     * @return
     */


    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> all3 = mangerService.findAll3(category2Id);
    return Result.ok(all3);
    }

    /**
     * 添加数据
     * @param baseAttrInfo
     * @return
     */

    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        mangerService.insertValue(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 查询所有分类三对应的属性数据
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */

    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){

        List<BaseAttrInfo> attrInfo = mangerService.getAttrInfo(category3Id);
        return Result.ok(attrInfo);

    }

    /**
     * 销售列表展示
     * @return
     */

    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> allBySale = mangerService.findAllBySale();
        return Result.ok(allBySale);
    }

    /**
     * 保存spu数据
     * @param spuInfo
     * @return
     */

    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        mangerService.saveSpu(spuInfo);
        return Result.ok();

    }

    /**
     * 分页展示spu数据
     * @param page
     * @param limit
     * @param category3Id
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result spuByPage(@PathVariable Integer page,
                            @PathVariable Integer limit,
                            @RequestParam Long category3Id){
        IPage<SpuInfo> spuInfoIPage = mangerService.selectSpuByPage(page, limit, category3Id);
        return Result.ok(spuInfoIPage);
    }

    /**
     * 查询销售列表值与名
     * @param spuId
     * @return
     */

    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
        List<SpuSaleAttr> list = mangerService.selectSpuBySaleValue(spuId);
        return Result.ok(list);

    }

    /**
     * sku图片展示
     * @param spuId
     * @return
     */

    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImages = mangerService.selectByImage(spuId);
        return Result.ok(spuImages);
    }

    /**
     * sku的保存
     * @param skuInfo
     * @return
     */

    @PostMapping("saveSkuInfo")
    public Result saveBySku(@RequestBody SkuInfo skuInfo){
        mangerService.saveSku(skuInfo);
        return Result.ok();

    }

    /**
     * sku的分页查询
     * @param page
     * @param limit
     * @return
     */

    @GetMapping("list/{page}/{limit}")
    public Result listPage(@PathVariable Integer page,
                           @PathVariable Integer limit){
        IPage<SkuInfo> skuInfoIPage = mangerService.selectBySkuByPage(page, limit);
        return Result.ok(skuInfoIPage);
    }

    /**
     * 上架
     * @param skuId
     * @return
     */

    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        mangerService.updateSaleStatus(skuId, ProductConstant.STATUS1);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @return
     */

    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        mangerService.updateSaleStatus(skuId, ProductConstant.STATUS2);
        return Result.ok();
    }








}
