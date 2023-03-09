package com.atguigu.gmall.order.litsener;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "order_normal_queue")
    public void m2(Channel channel, Message message){
        //获取信息
        long messageId = Long.parseLong(new String(message.getBody()));
        //获取属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取tag
        long deliveryTag = messageProperties.getDeliveryTag();

        try {
            //超时取消，处理发送的消息
            orderService.rollbackStock(messageId);

            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            try {
            if (messageProperties.getRedelivered()){
                channel.basicReject(deliveryTag,false);
            }    else {
                channel.basicReject(deliveryTag,true);
            }
             }catch (Exception exception){
                 log.error("在超时取消订单出错，");
             }

        }



    }
}
