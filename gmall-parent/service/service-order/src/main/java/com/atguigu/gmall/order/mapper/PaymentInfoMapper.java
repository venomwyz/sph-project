package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 支付渠道信息表的mapper映射
 */
@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {

    /**
     * 将订单的旧支付信息进行逻辑删除
     * @param orderId
     * @return
     */
    @Update("update payment_info set is_delete = 0 where order_id = #{orderId}")
    public int deletePaymentInfo(String orderId);
}