package com.atguigu.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitTemplateConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void m1(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if (b){
                    System.out.println("b="+b);
                }else {
                    System.out.println("b="+b);
                }
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("内容为"+new String(message.getBody()));
                System.out.println("内容为"+i);
                System.out.println("内容为"+s);
                System.out.println("内容为"+s1);
                System.out.println("内容为"+s2);
            }
        });


    }
}
