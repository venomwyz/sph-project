package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售mapper映射
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 查询后台属性
     * @param spuId
     * @return
     */

    List<SpuSaleAttr> getSpuBySaleById(@Param("spuId") Long spuId);

    /**
     * 为前端查询属性
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrBySpuIdAndSkuId(@Param("skuId") Long skuId,@Param("spuId")Long spuId);
}
