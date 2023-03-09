package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * sku销售值映射
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    @Select("SELECT sku_id,GROUP_CONCAT(DISTINCT sale_attr_value_id order by sale_attr_value_id SEPARATOR ',') " +
            "as values_id FROM sku_sale_attr_value " +
            "WHERE spu_id = #{spuId} GROUP BY sku_id")
    public List<Map> selectSkuIdAndValues(@Param("spuId") Long spuId);

}
