package com.atguigu.gmall.payment.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class WxReturnConsumer {


    /**
     * 获取支付结果消息,修改订单
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "return_queue")
    public void orderPayMessage(Channel channel, Message message){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息内容: json字符串
        String payResultString = new String(message.getBody());
        try {
            //修改订单的状态
            System.out.println(payResultString);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //取消发生异常,再来一次
                if(messageProperties.getRedelivered()){
                    //两次都失败
                    log.error("连续两次退款结果都失败,支付报文为:" + payResultString);
                    //拒绝消息,并丢弃
                    channel.basicReject(deliveryTag, false);
                }else{
                    //再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("拒绝消息发生异常,退款结果可能失败了,需要确认,支付报文为:" + payResultString);
            }
        }
    }


    }
