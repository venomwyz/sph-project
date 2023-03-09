package com.atguigu.gmall.all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients({"com.atguigu.gmall.item.client","com.atguigu.gmall.product.client","com.atguigu.gmall.list.client"})
@EnableDiscoveryClient
public class WebAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllApplication.class,args);
    }
}
