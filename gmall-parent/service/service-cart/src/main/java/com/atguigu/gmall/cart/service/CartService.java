package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * 购物车service层
 */
public interface CartService {
    /**
     * 添加购物车
     * @param
     */
    void addCart(Long skuId,Integer num);

    /**
     * 查看购物车数据
     * @return
     */
    List<CartInfo> getCartInfo();

    /**
     * 删除数据库
     * @param id
     */
    void delete(Long id);

    /**
     * 选中与取消
     * @param id
     * @param status
     */
    void checkOrUncheck(Long id , Short status);

    /**
     * 合并购物车
     * @param cartInfoList
     */
    void mergeCart(List<CartInfo> cartInfoList);


    /**
     * 计算总金额
     * @return
     */
    Map<String, Object> countNumAndMoney();

    /**
     * 清空购物车
     * @return
     */
    Boolean deleteCart();


}
