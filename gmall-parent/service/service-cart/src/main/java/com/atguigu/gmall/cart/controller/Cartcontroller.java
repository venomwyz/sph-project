package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 为feign接口调用准备
 */
@RestController
@RequestMapping("api/cart")
public class Cartcontroller {

@Autowired
private CartService cartService;
    /**
     * 获取购物车金额
     * @return
     */
    @GetMapping("getConfirmCart")
    public Map<String, Object> getMoney(){
        Map<String, Object> map = cartService.countNumAndMoney();
        return map;
    }

    @GetMapping("deleteCart")
    public Boolean delete(){
        try {
            return cartService.deleteCart();
        } catch (Exception e) {
            e.printStackTrace();
        }
         return false;
    }


}
