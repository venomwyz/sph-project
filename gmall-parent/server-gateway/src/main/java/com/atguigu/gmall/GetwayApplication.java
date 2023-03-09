package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 配置主启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GetwayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GetwayApplication.class,args);

    }
}
