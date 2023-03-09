package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestRedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TestRedisImpl implements TestRedisService {

    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public synchronized void setRedis() {
        //使用随机数做锁的value
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //加redis的setnx锁进行抢锁
        Boolean key = redisTemplate.opsForValue().setIfAbsent("key", uuid);
        //设置过期时间
        redisTemplate.expire("key",10, TimeUnit.SECONDS);
        //获取redis
        if (key){
            Integer vivi = (Integer) redisTemplate.opsForValue().get("vivi");

            //判断是否存在
            if (vivi !=null){
                vivi++;

                //如果存在加一
                redisTemplate.opsForValue().set("vivi",vivi);
                //释放锁的时候先判断锁的uuid值是否匹配
//                String redisUuid = (String) redisTemplate.opsForValue().get("key");
//                if (redisUuid.equals(uuid)){
//                    redisTemplate.delete("key");
//                }
                DefaultRedisScript<Object> script = new DefaultRedisScript<>();
                script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            }
        }else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setRedis();
        }




    }

    /**
     * redis的redisson的写测试
     */
    @Resource
    private RedissonClient redissonClient;
    @Override
    public void setRedisAndRedission() {

        //获取锁
        RLock lock = redissonClient.getLock("key");
        //加锁
        try {
            boolean tryLock = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (tryLock){
                try {
                    Integer vivi = (Integer) redisTemplate.opsForValue().get("vivi");

                    //判断是否存在
                    if (vivi !=null) {
                        vivi++;

                        //如果存在加一
                        redisTemplate.opsForValue().set("vivi", vivi);
                    }
                }catch (Exception e){
                    System.out.println("执行异常");
                }finally {
                    lock.unlock();
                }

            }
        } catch (InterruptedException e) {
            System.out.println("加锁失败");
        }


    }
}
