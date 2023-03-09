package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import net.bytebuddy.description.field.FieldDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制层
 */
@RestController
@RequestMapping("api/cart")
public class CartInfoController {
    @Resource
    private CartService cartService;

    /**
     * 添加数据
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/add")
    public Result add(Long skuId, Integer num){
        cartService.addCart(skuId,num);
        return Result.ok();

    }

    /**
     * 查询数据
     */

    @GetMapping("select")
    public Result select(){
        List<CartInfo> cartInfo = cartService.getCartInfo();
        return Result.ok(cartInfo);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @GetMapping("delete")
    public Result delete(Long id){
        cartService.delete(id);
        return Result.ok();
    }

    /**
     * 选中
     * @param id
     * @return
     */
    @GetMapping("check")
    public Result check(Long id){
        cartService.checkOrUncheck(id,(short)1);
        return Result.ok();
    }

    /**
     * 选中
     * @param id
     * @return
     */
    @GetMapping("uncheck")
    public Result uncheck(Long id){
        cartService.checkOrUncheck(id,(short)0);
        return Result.ok();
    }

    /**
     * 合并
     * @param cartInfoList
     * @return
     */

    @GetMapping("merge")
    public Result merge(List<CartInfo> cartInfoList){
        cartService.mergeCart(cartInfoList);
        return Result.ok();
    }

    /**
     * 获取购物车金额
     * @return
     */
    @GetMapping("getMoney")
    public Result getMoney(){
        Map<String, Object> map = cartService.countNumAndMoney();
        return Result.ok(map);
    }


}
