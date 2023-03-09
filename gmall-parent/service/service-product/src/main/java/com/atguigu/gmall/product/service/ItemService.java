package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 远程调用的查询商品的接口接口
 */

public interface ItemService {

    /**
     * 远程调用查询接口
     * @param skuId
     */

    SkuInfo getPageInfo(Long skuId);

    /**
     * 从redis中获取数据
     * @param skuId
     * @return
     */
    SkuInfo getRedisPageInfo(Long skuId);




    /**
     * 查询分页信息
     * @param category3Id
     * @return
     */

    BaseCategoryView getCategory(Long category3Id);

    /**
     * 查询图片
     * @param skuId
     * @return
     */

    List<SkuImage> getSpuImage(Long skuId);

    /**
     * 价格查询
     * @param skuId
     * @return
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 查询属性
     * @param skuId
     * @param spuId
     * @return
     */

    List<SpuSaleAttr> getSpuSaleAttr(Long skuId,Long spuId);

    /**
     * 跳转页面
     * @param spuId
     * @return
     */
    Map getSkuIdAndValues(Long spuId);

    /**
     * 品牌查询
     * @return
     */
    BaseTrademark getMark(Long id);

    /**
     * 属性查询
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoBySkuId(Long skuId);

    /**
     * 扣减库从
     * @return
     */
    boolean decountBase(Map<Object,Object> decountSkuMap);

    /**
     * 回退库从
      * @param orderId
     * @return
     */
    boolean rollbackStock(Map<Object,Object> decountSkuMap);

}
