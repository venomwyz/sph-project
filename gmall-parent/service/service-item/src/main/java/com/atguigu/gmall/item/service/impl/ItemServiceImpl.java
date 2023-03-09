package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.config.ItemThreadPoolConfig;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 获取页面信息业务层
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    /**
     * 获取页面信息
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getItemPageInfo(Long skuId) {
        //参数校验
        if (skuId == null) {
            return null;
        }
        //测试Map
        Map<String, Object> map = new ConcurrentHashMap<>();
        //商品信息
        CompletableFuture<SkuInfo> future = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.selectBySkuId(skuId);

            //商品不存在
            if (skuInfo == null || skuInfo.getId() == null) {
                return null;
            }
            map.put("skuInfo", skuInfo);
            return skuInfo;

        },threadPoolExecutor);
        CompletableFuture<Void> future1 = future.thenAcceptAsync(skuInfo -> {
            //商品存在，查询分类信息
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            Long category3Id = skuInfo.getCategory3Id();
            BaseCategoryView category = productFeignClient.getCategory(category3Id);
            map.put("category", category);
        },threadPoolExecutor);

        CompletableFuture<Void> future2 = future.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            //商品存在，查询商品图片列表
            List<SkuImage> image = productFeignClient.getImage(skuId);
            map.put("skuImageList", image);
        },threadPoolExecutor);

        CompletableFuture<Void> future3 = future.thenAcceptAsync(skuInfo -> {
            //商品存在，查询商品价格
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            BigDecimal price = productFeignClient.getPrice(skuInfo.getId());
            map.put("price", price);
        },threadPoolExecutor);

        CompletableFuture<Void> future4 = future.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            //商品存在，查询商品的销售属性
            List<SpuSaleAttr> spuSaleAttr = productFeignClient.getSpuSaleAttr(skuInfo.getId(), skuInfo.getSpuId());
            map.put("spuSaleAttrList", spuSaleAttr);
        },threadPoolExecutor);

        CompletableFuture<Void> future5 = future.thenAcceptAsync(skuInfo -> {
            if (skuInfo == null || skuInfo.getId() == null) {
                return;
            }
            //商品存在，查询商品的跳转的用的spu下的所有sku的值和id的集合(键值对)(将三个结果变为一个结果变为key
            // ，将sku的id变为建)
            Map skuIdAndValues = productFeignClient.getSkuIdAndValues(skuInfo.getSpuId());
            map.put("skuIdAndValues", skuIdAndValues);
        },threadPoolExecutor);
        //用CompletableFuture优化任务结束
        CompletableFuture.allOf(future1,future2,future3,future4,future5).join();
        //返回
        return map;

    }


}
