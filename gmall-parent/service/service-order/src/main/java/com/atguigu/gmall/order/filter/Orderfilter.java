package com.atguigu.gmall.order.filter;

import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.order.util.TokenUtil;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@WebFilter(filterName = "orderFilter", urlPatterns = "/*")
@Order(1)
public class Orderfilter extends GenericFilter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //获取请求头
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization").replace("bearer ", "");
        //解析令牌
        Map<String, String> map = TokenUtil.dcodeToken(token);

        if(!map.isEmpty()){
            //获取用户名
            String username = map.get("username");
            //判断
            if(!StringUtils.isEmpty(username)){
               // 存储
                OrderThreadLocalUtil.set(username);
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);

    }
}
