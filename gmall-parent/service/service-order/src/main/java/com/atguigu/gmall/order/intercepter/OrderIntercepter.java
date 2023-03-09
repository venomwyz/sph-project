package com.atguigu.gmall.order.intercepter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 订单微服务的feign调用的拦截器:
 *  feign调用发生前,做个增强
 * 拦截器: 出去的请求
 * 过滤器: 进入的请求
 */
@Component
public class OrderIntercepter implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 在Spring Framework中，RequestContextHolder是一个用于获取当前请求的HttpServletRequest和HttpServletResponse对象的静态类。
         *
         * getRequestAttributes()是RequestContextHolder类的一个静态方法，返回一个ServletRequestAttributes对象，其中包含了当前请求的相关信息，例如请求参数、请求头、请求路径等。如果当前线程不是Web请求线程，则返回null。
         *
         * 可以通过以下代码获取当前请求的HttpServletRequest对象：
         */

        //获取原requset的对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        //非空判断
        if (requestAttributes != null){
            HttpServletRequest request = requestAttributes.getRequest();

            //获取request中的全部的请求头

            Enumeration<String> headerNames = request.getHeaderNames();
            //迭代请求头
            while (headerNames.hasMoreElements()){
                String name = headerNames.nextElement();
                String value = request.getHeader(name);

                //存储
                requestTemplate.header(name,value);
            }

        }
    }
}
