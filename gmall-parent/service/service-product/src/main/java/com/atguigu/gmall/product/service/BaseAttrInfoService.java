package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 平台属性表的Service接口类
 */
public interface BaseAttrInfoService {
    /**
     * 查询所有
     * @return
     */
    List<BaseAttrInfo> findAll();

    /**
     * 根据id查询
     * @param id
     * @return
     */

    BaseAttrInfo findById(Long id);

    /**
     * 插入数据
     * @param baseAttrInfo
     */
    void insert(BaseAttrInfo baseAttrInfo);

    /**
     * 修改一条数据
     * @param baseAttrInfo
     */
    void update(BaseAttrInfo baseAttrInfo);

    /**
     * 删除一条数据
     * @param id
     */

    void delete(Long id);

    /**
     * 分页展示
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrInfo> pageBy(Integer page, Integer size);

    /**
     * 条件查询
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> select(BaseAttrInfo baseAttrInfo);

    /**
     * 条件分页查询
     * @param baseAttrInfo
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrInfo> selectByPage(BaseAttrInfo baseAttrInfo, Integer page, Integer size);



}
