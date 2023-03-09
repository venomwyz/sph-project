package com.atguigu.mq.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;

/**
 * 消费
 */
@Component
public class MqListener {

//    @RabbitListener(queues = "queue_1")
//    public void m1(String msg){
//        try {
//            System.out.println(msg);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

    @RabbitListener(queues = "normal_queue")
    public void m1(Channel channel, Message message){
        System.out.println("消息的接收时间为: " + System.currentTimeMillis());
        //获取信息的内容
        System.out.println(new String(message.getBody()));
        //获取属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取标签
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                if (messageProperties.getRedelivered()){
                    System.out.println("解决第二次");
                    channel.basicReject(deliveryTag,false);
                }else {
                    System.out.println("拒绝第一次");
                    channel.basicReject(deliveryTag,true);
                }



            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
