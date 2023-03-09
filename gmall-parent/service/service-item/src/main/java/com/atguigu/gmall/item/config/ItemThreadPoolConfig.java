package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ItemThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolConfig(){

       return new ThreadPoolExecutor(12,
                24,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1000));


    }
}
