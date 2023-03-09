package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartInfoFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartInfoFeign cartInfoFeign;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 添加订单与详情
     *
     * @param orderInfo
     */
    @Override
    public void addOrderAndDetail(OrderInfo orderInfo) {
        //校验
        if (orderInfo == null){
            return;
        }
        //添加分布式锁
        RLock lock = redissonClient.getLock("Order_Add_Lock_" + OrderThreadLocalUtil.get());

        try {
            if (lock.tryLock()){
                try {
                    //死锁
                    redisTemplate.expire("Order_Add_Lock_" + OrderThreadLocalUtil.get(),10, TimeUnit.SECONDS);
                    //查询购物车信息 需要用feign远程调用查询
                    Map<String, Object> cartInfoFeignMoney = cartInfoFeign.getMoney();
                    if (cartInfoFeignMoney != null) {
                        //构建订单对象补全数据
                        orderInfo.setTotalAmount(new BigDecimal(cartInfoFeignMoney.get("money").toString()));
                        orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
                        String username = OrderThreadLocalUtil.get();
                        orderInfo.setUserId(username);
                        orderInfo.setPaymentWay("1");
                        orderInfo.setCreateTime(new Date());
                        orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1800000));
                        orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
                        //保存订单信息
                        int insert = orderMapper.insert(orderInfo);
                        if (insert <= 0) {
                            throw new RuntimeException("插入失败");
                        }
                        //保存成功后获取订单号id
                        Long id = orderInfo.getId();

                        //基于订单号id，保存订单详情
                        List cartInfoList = (List) cartInfoFeignMoney.get("cartInfoList");
                        Map map = saveOrderDetail(id, cartInfoList);
                        //清空购物车
//                        if (!cartInfoFeign.delete()){
//                            throw new RuntimeException("清空购物车失败,下单失败!");
//                        }
                        //扣减库存
                        if (!productFeignClient.decount(map)){
                            throw new RuntimeException("扣减库从失败!");
                        }
                        //发送延时信息
                        rabbitTemplate.convertAndSend("order_normal_exchange","order_normal_aa",
                                id+"",
                                (message -> {
                                    MessageProperties messageProperties = message.getMessageProperties();
                                    messageProperties.setExpiration(20000+"");
                                    return message;

                                }));

                    }
                }catch (Exception e){
                   throw new RuntimeException("加锁正常，执行过程发生异常");
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        } catch (RuntimeException e) {
            log.error("下单加锁发生异常,异常的内容为:" + e.getMessage());
        }



    }

    /**
     * 创建私有方法，保存订单详情
     * @param id
     * @param cartInfoList
     */
    private Map saveOrderDetail(Long id, List cartInfoList) {
        //记录扣减库存的商品数量
        Map map = new ConcurrentHashMap();
        cartInfoList.stream().forEach(o->{
            //序列化
            String s = JSONObject.toJSONString(o);
            //反序列化
            CartInfo cartInfo = JSONObject.parseObject(s, CartInfo.class);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            //前面已经将实时价格保存
            orderDetail.setOrderPrice(cartInfo.getSkuPrice());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            int insert = orderDetailMapper.insert(orderDetail);

            if (insert<= 0){
                throw new RuntimeException("保存订单详情失败");
            }
            map.put(cartInfo.getSkuId().toString(),cartInfo.getSkuNum());
        });

        return map;


    }

    /**
     * 取消订单，回滚库从
     *
     * @param orderId
     */
    @Override
    public void rollbackStock(Long orderId) {
        //校验
        if (orderId==null){
            return;
        }
        RLock lock = redissonClient.getLock("Cancle_Order_" + orderId);
        try {
            if (lock.tryLock()){
                try {
                    redisTemplate.expire("Cancle_Order_" + orderId,10,TimeUnit.SECONDS);
                    //判断是主动取消还是被动取消
                    LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper
                            .eq(OrderInfo::getId,orderId)
                            .eq(OrderInfo::getOrderStatus,OrderStatus.UNPAID.getComment());
                    //获取username
                    String username = OrderThreadLocalUtil.get();
                    if (StringUtils.isEmpty(username)){
                        //超时取消
                        OrderInfo orderInfo = orderMapper.selectOne(queryWrapper);
                        orderInfo.setOrderStatus(OrderStatus.TIMEOUT_CANCLE.getComment());
                        orderInfo.setProcessStatus(OrderStatus.TIMEOUT_CANCLE.getComment());
                        int update = orderMapper.updateById(orderInfo);
                        if (update <0){
                            throw new RuntimeException("超时取消修改失败");
                        }
                    }else {
                        //主动取消
                        queryWrapper
                                .eq(OrderInfo::getUserId,username);
                        OrderInfo orderInfo = orderMapper.selectOne(queryWrapper);
                        orderInfo.setOrderStatus(OrderStatus.ACT_CANCLE.getComment());
                        orderInfo.setProcessStatus(OrderStatus.ACT_CANCLE.getComment());
                        int update = orderMapper.updateById(orderInfo);
                        if (update <0){
                            throw new RuntimeException("修改主动失败");
                        }
                    }
                    //回退库存
                    rollbackReturn(orderId);

                }catch (Exception e){
                    throw new RuntimeException("加锁正常，执行过程发生异常");
                }finally {
                    lock.unlock();
                }
            }
        }catch (Exception e){
            throw new RuntimeException("订单取消加锁失败");
        }

    }

    /**
     * 回退库存
     * @param orderId
     */
    private void rollbackReturn(Long orderId) {
        LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(wrapper);
        Map map = new ConcurrentHashMap<>();
       orderDetails.stream().forEach(orderDetail -> {
           map.put(orderDetail.getSkuId().toString(), orderDetail.getSkuNum());


       });
       if (!productFeignClient.rollbackCart(map)){
           throw new RuntimeException("在订单业务中数据库回滚失败");
       }
    }
}
