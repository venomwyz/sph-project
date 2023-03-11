package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    /**
     * 交换机
     */
    @Bean("resultEx")
    public Exchange resultEx(){
        return ExchangeBuilder.directExchange("result_exchange").build();
    }

    /**
     * 队列
     */
    @Bean("resultQu")
    public Queue resultQu(){
        return QueueBuilder.durable("result_queue").build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding(@Qualifier("resultEx") Exchange resultEx,
                           @Qualifier("resultQu") Queue resultQu){
        return BindingBuilder.bind(resultQu).to(resultEx).with("resultWx").noargs();
    }


}
