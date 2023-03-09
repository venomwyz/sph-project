package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 平台属性名称表的Mapper映射
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> selectBaseAttrInfoByCategory3Id(Long id3);

    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(Long skuId);



}
