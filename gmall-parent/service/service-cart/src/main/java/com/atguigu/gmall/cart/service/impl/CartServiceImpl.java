package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.util.CartThreadLocalUtil;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    /**
     * 添加购物车
     * @param cartInfo
     */
    @Resource
    private CartMapper cartMapper;
    @Resource
    private ProductFeignClient productFeignClient;
    @Override
    public void addCart(Long skuId,Integer num) {
        //数据校验
        if (skuId==null && num==null){
            return;
        }
        //添加数据

        SkuInfo skuInfo = productFeignClient.selectBySkuId(skuId);
        //判断skuId
        if (skuInfo==null){
            return;
        }
        String username = CartThreadLocalUtil.get();
        //在添加数据之前判断是否数据库存在
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CartInfo::getUserId,username).eq(CartInfo::getSkuId,skuId);
        CartInfo cartInfo = cartMapper.selectOne(queryWrapper);
        //如果为空则新增
        if (cartInfo == null || cartInfo.getId()==null){
            if (num <=0){
                return;
            }
            //添加数据
            cartInfo = new CartInfo();
            cartInfo.setUserId(username);
            cartInfo.setSkuId(skuId);
            //价格
            BigDecimal price = productFeignClient.getPrice(skuId);
            cartInfo.setCartPrice(price);
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());

            int insert = cartMapper.insert(cartInfo);
            if (insert<=0){
                throw new RuntimeException("数据添加失败");
            }

        }else {
            //合并数量
            num = cartInfo.getSkuNum() + num;
            if (num <=0 ){
               cartMapper.deleteById(cartInfo.getId());
            }

            cartInfo.setSkuNum(num);
            int update = cartMapper.updateById(cartInfo);
            if (update <0){
                return;
            }
        }
    }

    /**
     * 查看购物车数据
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        String username = CartThreadLocalUtil.get();
        List<CartInfo> cartInfos = cartMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username));
        return cartInfos;
    }

    /**
     * 删除数据库
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        String username = CartThreadLocalUtil.get();
        cartMapper.delete(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId,username)
                .eq(CartInfo::getId,id));
    }

    /**
     * 选中与取消
     *
     * @param id
     * @param status
     */
    @Override
    public void checkOrUncheck(Long id, Short status) {
        //首先要区分是全部选中还是单独选中


        String username = CartThreadLocalUtil.get();

        if (id == null){
            cartMapper.all(status,username);
        }else {
            cartMapper.one(id,status,username);
        }

    }

    /**
     * 合并购物车
     *
     * @param cartInfoList
     */
    @Override
    public void mergeCart(List<CartInfo> cartInfoList) {
        //合并
        cartInfoList.stream().forEach(cartInfo -> {
            //合并或者新增
            this.addCart(cartInfo.getSkuId(),cartInfo.getSkuNum());
        });

    }

    /**
     * 计算总金额
     * @return
     */
    @Override
    public Map<String, Object> countNumAndMoney() {
        //将所有的数据添加到map中
        Map<String, Object> map = new HashMap<>();
        //查询此用户的所有购物车
        String username = CartThreadLocalUtil.get();

        LambdaQueryWrapper<CartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CartInfo::getUserId,username).eq(CartInfo::getIsChecked,(short)1);
        List<CartInfo> cartInfos = cartMapper.selectList(wrapper);
        if (cartInfos==null){
            return null;
        }
        //给金额与数量初始值
        AtomicInteger num=new AtomicInteger(0);
        AtomicDouble money=new AtomicDouble(0);
        List<CartInfo> cartInfoList = cartInfos.stream().map(cartInfo -> {
            //获取数量
            Integer skuNum = cartInfo.getSkuNum();
            //相当于i++
            num.getAndAdd(skuNum);
            //获取金额，计算总价格
            BigDecimal price = productFeignClient.getPrice(cartInfo.getSkuId());
            cartInfo.setSkuPrice(price);
            //计算出总价格
            money.getAndAdd(price.multiply(new BigDecimal(cartInfo.getSkuNum())).doubleValue());
            return cartInfo;
        }).collect(Collectors.toList());
        //将数据保存
        map.put("cartInfoList",cartInfoList);
        map.put("num",num);
        map.put("money",money);
        return map;
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @Override
    public Boolean deleteCart() {
        LambdaQueryWrapper<CartInfo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(CartInfo::getUserId,CartThreadLocalUtil.get()).eq(CartInfo::getIsChecked,(short)1);
        int delete = cartMapper.delete(queryWrapper);
        //只有大于0才删除
        return delete>0;
    }
}
