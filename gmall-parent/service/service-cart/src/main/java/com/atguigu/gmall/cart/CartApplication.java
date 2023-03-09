package com.atguigu.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.atguigu.gmall")
@ServletComponentScan(basePackages = "com.atguigu.gmall.cart.filter")
@EnableFeignClients(basePackages = "com.atguigu.gmall.product.client")
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class,args);
    }
}
