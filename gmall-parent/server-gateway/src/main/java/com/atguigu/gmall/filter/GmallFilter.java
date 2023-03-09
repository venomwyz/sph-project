package com.atguigu.gmall.filter;


import com.atguigu.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义的全局过滤器
 */
@Component
public class GmallFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 自定义逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //获取响应体
        ServerHttpResponse response = exchange.getResponse();
        //从url获取token
//        String token = request.getQueryParams().getFirst("token");
//        if(StringUtils.isEmpty(token)){
//            //从head获取token
//            token = request.getHeaders().getFirst("token");
//            if(StringUtils.isEmpty(token)){
//                //cookie获取token
//                HttpCookie httpCookie = request.getCookies().getFirst("token");
//                if(httpCookie != null){
//                    token = httpCookie.getValue();
//                }
//            }
//        }
        //若全部都没有,则拒绝(引导到登录页面)
//        if(StringUtils.isEmpty(token)){
//            response.setStatusCode(HttpStatus.VARIANT_ALSO_NEGOTIATES);
//            return response.setComplete();
//        }
        //携带了token,获取本次请求的ip地址
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        //通过本地获取到的ip地址去redis中去获取redis中的redisToken
        String redisToken =
                stringRedisTemplate.opsForValue().get(gatwayIpAddress);
        //拿redistoken和token对比,不一致拒绝
        if(StringUtils.isEmpty(redisToken)){
            response.setStatusCode(HttpStatus.VARIANT_ALSO_NEGOTIATES);
            return response.setComplete();
        }
        //在转发的请求头中放一个固定key和固定格式value
        request.mutate().header("Authorization", "bearer " + redisToken);
        //一致,放行
        return chain.filter(exchange);
    }

    /**
     * 顺序
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
