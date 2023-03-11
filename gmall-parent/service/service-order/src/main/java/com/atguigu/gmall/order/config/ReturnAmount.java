package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReturnAmount {
    /**
     * 交换机
     */
    @Bean("returnEx")
    public Exchange returnEx(){
        return ExchangeBuilder.directExchange("return_exchange").build();
    }

    /**
     * 队列
     */
    @Bean("returnQu")
    public Queue returnQu(){
        return QueueBuilder.durable("return_queue").build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding(@Qualifier("returnEx") Exchange returnEx,
                           @Qualifier("returnQu") Queue returnQu){
        return BindingBuilder.bind(returnQu).to(returnEx).with("returnWx").noargs();
    }
}
