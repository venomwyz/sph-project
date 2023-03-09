package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 购物车映射
 */
@Mapper
public interface CartMapper extends BaseMapper<CartInfo> {

    //单独选中
    @Update("update cart_info set is_checked=#{status} where id=#{id} and user_id=#{username}")
    void one(@Param("id") Long id , @Param("status") Short status,@Param("username") String username);

    //全部选中
    @Update("update cart_info set is_checked=#{status} where user_id=#{username}")
    void all(@Param("status") Short status,@Param("username") String username);


}
