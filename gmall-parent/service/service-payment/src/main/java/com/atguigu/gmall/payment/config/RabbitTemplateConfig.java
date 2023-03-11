package com.atguigu.gmall.payment.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 队列没有收到信息的处理
 */
@Component
@Log4j2
public class RabbitTemplateConfig {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                log.error("信息内容为："+new String(message.getBody()));
                log.error("信息内容为："+i);
                log.error("信息内容为："+s);
                log.error("信息内容为："+s1);
                log.error("信息内容为："+s2);
            }
        });
    }
}
