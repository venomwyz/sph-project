package com.atguigu.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建交换机，队列，并且绑定
 */
@Configuration
public class DemoConfig {


    @Bean("exchange1")
    public Exchange exchange1(){
        return ExchangeBuilder.directExchange("exchange_1").build();
    }
    @Bean("queue1")
    public Queue queue1(){
        return QueueBuilder.durable("queue_1").build();
    }

    /**
     * 绑定队列与交换机
     */

    @Bean
    public Binding binding1(@Qualifier("queue1") Queue queue1,
                            @Qualifier("exchange1") Exchange exchange1){
        return BindingBuilder.bind(queue1).to(exchange1).with("user.add").noargs();
    }



}
