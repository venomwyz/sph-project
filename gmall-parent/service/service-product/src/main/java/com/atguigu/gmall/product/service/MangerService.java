package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 一级分类service
 */
public interface MangerService {
    /**
     * 查询分类1所有的数据
     * @return
     */
    List<BaseCategory1> findAll();

    /**
     * 查询对应的BaseCategory2；
     * @return
     */

    List<BaseCategory2> findAll2(Long id1);

    /**
     * 查询分类3中的所有数据
     * @param id2
     * @return
     */
    List<BaseCategory3> findAll3(Long id2);

    /**
     * 添加平台数据
     * @param baseAttrInfo
     */


    void insertValue(BaseAttrInfo baseAttrInfo);

    /**
     * 获取平台数据
     * @return
     */
    List<BaseAttrInfo> getAttrInfo(Long id);

    /**
     * 查询所有的销售属性
     * @return
     */

    List<BaseSaleAttr> findAllBySale();

    /**
     * spu的保存接口
     * @param spuInfo
     */
    void saveSpu(SpuInfo spuInfo);

    /**
     * spu分页查询
     * @param page
     * @param size
     * @param id3
     * @return
     */

    IPage<SpuInfo> selectSpuByPage(Integer page, Integer size, Long id3);

    /**
     * 销售属性展示接口
     * @param id
     * @return
     */
    List<SpuSaleAttr> selectSpuBySaleValue(Long id);

    /**
     * 图片展示
     * @param spuId
     * @return
     */
    List<SpuImage> selectByImage(Long spuId);

    /**
     * sku的属性保存接口
     * @param skuInfo
     */
    void saveSku(SkuInfo skuInfo);

    /**
     * 获取sku的分页
     * @param page
     * @param size
     * @return
     */

    IPage<SkuInfo> selectBySkuByPage(Integer page, Integer size);

    /**
     * 上线与下线
     * @param skuId
     */
    void updateSaleStatus(Long skuId,Short status);


}
