package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 商品价格service接口
 */
public interface BaseAttrValueService {

    /**
     * 查询全部数据
     * @return
     */
    List<BaseAttrValue> findAll();

    /**
     * 删除数据
     * @param id
     */
    void delete(Long id);

    /**
     * 新增数据
     */

    void insert(BaseAttrValue baseAttrValue);


    /**
     * 修改数据
     */

    void update(BaseAttrValue baseAttrValue);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrValue> page(Integer page,Integer size);

    /**
     * 条件查询
     * @param baseAttrValue
     * @return
     */
    List<BaseAttrValue> select(BaseAttrValue baseAttrValue);

    /**
     * 分页条件查询
     * @param baseAttrValue
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrValue> selectByPage(BaseAttrValue baseAttrValue,Integer page, Integer size);


}
