package com.atguigu.gmall.list.service;

/**
 * es查询数据接口
 */
public interface GoodsService {
    /**
     * 数据查询
     */
    void addGoodsByEs(Long skuId);

    /**
     * 删除es查询
     * @param goodsId
     */
    void removeGoodsByEs(Long goodsId);

    /**
     * 添加热度值
     * @param id
     */
    void addHotScore(Long id);

}
