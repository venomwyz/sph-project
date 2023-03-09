package com.atguigu.mq.controller;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class MqController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public String test(){

        System.out.println("消息的发送时间为: " + System.currentTimeMillis());

        rabbitTemplate.convertAndSend("normal_exchange","normal_aa",
                "信息测试",
                (message -> {
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setExpiration(10000+"");
                    return message;

        }));

        return "success";

    }
}
