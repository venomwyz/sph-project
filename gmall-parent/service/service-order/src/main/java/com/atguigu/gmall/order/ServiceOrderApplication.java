package com.atguigu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall")
@EnableFeignClients({"com.atguigu.gmall.cart.feign","com.atguigu.gmall.product.client","com.atguigu.gmall.payment.client"})
@EnableDiscoveryClient
@ServletComponentScan("com.atguigu.gmall.order.filter")
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class,args);
    }
}
