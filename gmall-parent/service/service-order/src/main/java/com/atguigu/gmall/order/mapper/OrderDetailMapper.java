package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单详情映射
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
