package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartInfoFeign;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderMapper;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.payment.client.PaymentFegin;
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
import java.util.*;
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
    @Autowired
    private PaymentFegin paymentFegin;
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
                                    messageProperties.setExpiration(2000000+"");
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
        RLock lock = redissonClient.getLock("pay_cancel_orderId_" + orderId);
        try {
            if (lock.tryLock()){
                try {
                    redisTemplate.expire("pay_cancel_orderId_" + orderId,10,TimeUnit.SECONDS);
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

    /**
     * 获取微信的支付信息:安照收到的支付结果，无条件的修改为支付
     *
     * @param resultPay
     */
    @Override
    public void updatePayStatus(String resultPay) {
        //校验
        if (StringUtils.isEmpty(resultPay)){
            return;
        }

        //将信息转换为对象
        Map<String,String> map = JSONObject.parseObject(resultPay, Map.class);
        //获取订单号
        String orderId = map.get("out_trade_no");
        //防止并发
        RLock lock = redissonClient.getLock("pay_cancel_orderId_" + orderId);
        try {
            lock.lock();
                try {
                    //查询数据库，修改状态
                    OrderInfo orderInfo = orderMapper.selectById(orderId);
                    //修改状态--修改为已经支付
                    if (orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())){

                        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
                        orderInfo.setProcessStatus(OrderStatus.PAID.getComment());
                        //添加其他两个字段
                        orderInfo.setOutTradeNo(map.get("transaction_id"));
                        orderInfo.setTradeBody(resultPay);
                        int update = orderMapper.updateById(orderInfo);
                        if (update<0){
                            throw new RuntimeException("修改失败");
                        }
                        //用户点击取消或者是超时取消，但是这时用户又将钱付了，就必须将取消改为为已经支付,然后进行退款
                    }else if (orderInfo.getOrderStatus().equals(OrderStatus.ACT_CANCLE.getComment()) ||
                            orderInfo.getOrderStatus().equals(OrderStatus.TIMEOUT_CANCLE.getComment())) {
                        //调用方法
                        Map<String, String> hashMap = getStringStringMap(orderId, orderInfo,map.get("paywey"));

                        rabbitTemplate
                                .convertAndSend("return_exchange", "returnWx", JSONObject.toJSONString(hashMap));
                        //若订单为已支付状态,判断是否重复支付!!(多渠道都去支付了), 判断本次支付的渠道和数据库保存的渠道是否一致
                    }else if (orderInfo.getOrderStatus().equals(OrderStatus.PAID.getComment())){
                        //获取信息
                        PaymentInfo paymentInfo =
                                paymentInfoMapper.selectOne(
                                        new LambdaQueryWrapper<PaymentInfo>()
                                                .eq(PaymentInfo::getOrderId, orderId)
                                                .eq(PaymentInfo::getIsDelete, 1));
                        if (!paymentInfo.getPaymentType().equals(map.get("payway"))) {
                            Map<String, String> reMap = getStringStringMap(orderId, orderInfo, map.get("paywey"));

                        }
                    }
                }catch (Exception exception){
                    log.error("加锁成功，业务执行失败");
                }finally {
                    lock.unlock();
                }

        }catch (Exception e){
            log.error("加锁失败");
        }

    }

    private Map<String, String> getStringStringMap(String orderId, OrderInfo orderInfo,String payway) {
        //生成退款需要的字段
        String replace = UUID.randomUUID().toString().replace("-", "");
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("refundNo", replace);
        hashMap.put("orderId", orderId);
        hashMap.put("acount", orderInfo.getTotalAmount()
                .multiply(new BigDecimal(100)).intValue() + "");
        return hashMap;
    }

    @Resource
private PaymentInfoMapper paymentInfoMapper;

    /**
     * 判断用户支付的渠道，目的是为了知道用户多渠道支付，造成的重复支付后果
     * ，这也是用户支付的第一步
     *  @param orderId
     * @param paywey
     * @return
     */
    @Override
    public PaymentInfo getPayment(String orderId, String paywey) {
        if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(paywey)){
            return null;
        }

        //获取旧的,有的话返回,没有去找渠道申请
        PaymentInfo paymentInfo =
                (PaymentInfo) redisTemplate.opsForValue().get("Order_Pay_Url_" + orderId);
        if(paymentInfo != null && paymentInfo.getId() != null){
            return paymentInfo;
        }
        //加锁,保证同一个订单在同一个时间只能选择一个支付渠道--->5分钟才能换渠道!
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("Pay_return_Url_" + orderId, "123", 300, TimeUnit.SECONDS);
            //查询订单信息，目的是用户将订单信息传过去
            if (aBoolean){
                LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OrderInfo::getUserId,OrderThreadLocalUtil.get())
                        .eq(OrderInfo::getId,orderId);
                OrderInfo orderInfo = orderMapper.selectOne(queryWrapper);

                //使用feign调用
                Map<String, String> uiui = paymentFegin.getUrl("uiui", orderId, orderInfo.getTotalAmount().multiply(new BigDecimal(100)).intValue() + "");
                //判断是否成功获取二维码及相关信息
                if(uiui.get("return_code").equals("SUCCESS") &&
                        uiui.get("result_code").equals("SUCCESS")){
                    String codeUrl = uiui.get("code_url");

                    paymentInfo = new PaymentInfo();
                    paymentInfo.setPayUrl(codeUrl);
                    paymentInfo.setOrderId(orderId);
                    paymentInfo.setPaymentType(paywey);
                    paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
                    paymentInfo.setSubject("尚硅谷商城");
                    paymentInfo.setPaymentStatus(OrderStatus.UNPAID.getComment());
                    paymentInfo.setCreateTime(new Date());
                    //保存: 先逻辑删除数据的,在新增
                    if(paymentInfoMapper.deletePaymentInfo(orderId) > 0 &&
                            paymentInfoMapper.insert(paymentInfo) > 0){
                        redisTemplate.opsForValue().set("Pay_return_Url_" + orderId,paymentInfo);

                    }else {
                        //把key删除,防止用户不能申请渠道
                        redisTemplate.delete("Order_Pay_Url_" + orderId);
                    }
                    //成功获取才能返回支付二维码地址
                    return paymentInfo;

                }
            }
            return null;
    }
}
