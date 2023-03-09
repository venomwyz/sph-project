package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    /**
     * 正常交换机
     */
    @Bean("normalE")
    public Exchange normalE(){
        return ExchangeBuilder.directExchange("order_normal_exchange").build();
    }

    /**
     * 死信队列
     */
    @Bean("deadQ")
    public Queue deadQ(){
        return QueueBuilder.durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange","order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","order_dead_bb")
                .build();
    }

    /**
     * 绑定正常交换机与死信队列
     */
    @Bean
    public Binding bindDeadQ(@Qualifier("normalE") Exchange normalE,
                             @Qualifier("deadQ") Queue deadQ){
        return BindingBuilder.bind(deadQ).to(normalE).with("order_normal_aa").noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("deadE")
    public Exchange deadE(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 正常队列
     */
    @Bean("normalQ")
    public Queue normalQ(){
        return QueueBuilder.durable("order_normal_queue").build();
    }

    @Bean
    public Binding bindNormalQ(@Qualifier("deadE") Exchange deadE,
                               @Qualifier("normalQ") Queue normalQ){
        return BindingBuilder.bind(normalQ).to(deadE).with("order_dead_bb").noargs();
    }
}
