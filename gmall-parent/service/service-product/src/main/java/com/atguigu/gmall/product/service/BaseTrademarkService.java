package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 品牌表service层接口
 */
public interface BaseTrademarkService {
    /**
     * 查询所有数据
     * @return
     */
    List<BaseTrademark> findAll();

    /**
     * 分页查询所有的品牌数据
     * @param page
     * @param size
     * @return
     */

    IPage<BaseTrademark> findAllByPage(Integer page, Integer size);

    /**
     * 添加品牌
     * @param baseTrademark
     */


    void saveTrademark(BaseTrademark baseTrademark);
}
