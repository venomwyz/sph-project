package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * spu的属性映射
 */
@Mapper

public interface SpuSaleAttrValueMapper extends BaseMapper<SpuSaleAttrValue> {

}
