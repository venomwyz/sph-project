package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 查询分类信息mapper映射
 */
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
