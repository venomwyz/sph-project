package com.atguigu.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableDiscoveryClient  //注册到注册中心
@ComponentScan("com.atguigu.gmall")

/**
 * springboot就是的简化的整合
 */
public class ProductApplication {
    /**
     * 创建ioc容器，将可能使用的bean全部准备好
     * @param args:JVM的参数
     *   @SpringBootApplication的三个作用
     *   ComponentScan：扫描启动类所在包下类的全部注解以及子包中所有类的注解
     *   SpringBootConfiguration：启动类作为配置类
     *   EnableAutoConfiguration（最难）：
     *   a.最终会根据@Import(AutoConfigurationImportSelector.class)获取工厂的127个类，
     *   而这127个类不是一次性加载的，他们会按照条件装配规则（@Conditional），
     *   b.会按pom需配置。然后会在根据反射的全量名进行实例化，从而创建这个类
     *   c.最终会将加载好的类以bean的形式注入到ioc中
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }


}
