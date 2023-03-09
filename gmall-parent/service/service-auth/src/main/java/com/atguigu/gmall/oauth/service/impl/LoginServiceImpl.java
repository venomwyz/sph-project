package com.atguigu.gmall.oauth.service.impl;

import com.alibaba.nacos.client.utils.IPUtil;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

/**
 * 登录接口类
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private HttpServletRequest httpServletRequest;
    /**
     * 用户登录
     *  @param username
     * @param password
     * @return
     */
    @Override
    public AuthToken login(String username, String password) {
        //参数校验: 用户名密码都不能为空
        if(StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(password)){
            throw new RuntimeException("用户名或密码不能为空!");
        }
        //请求头初始化
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.set("Authorization", getHeadValue());
        //请求体初始化: 3个
        MultiValueMap<String, String> body = new HttpHeaders();
        body.set("username", username);
        body.set("password", password);
        body.set("grant_type", "password");
        //拼接参数: 5个
        HttpEntity httpEntity = new HttpEntity(body, headers);
        //往固定的url发送post请求
        ServiceInstance choose = loadBalancerClient.choose("service-oauth");
        //http://ip:port
        String url = choose.getUri().toString() + "/oauth/token";
        //发起请求
        /**
         * 1.地址
         * 2.请求方法类型
         * 3.请求参数
         * 4.返回结果街垒
         */
        ResponseEntity<Map> exchange =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        //获取返回结果
        Map<String, String> result = exchange.getBody();
        //解析结果
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(result.get("access_token"));
        authToken.setRefreshToken(result.get("refresh_token"));
        authToken.setJti(result.get("jti"));
        //防止token盗用 ，将IP与token存储在redis中
        String ipAddress = IpUtil.getIpAddress(httpServletRequest);
        redisTemplate.opsForValue().set(ipAddress,authToken.getAccessToken());
        //返回
        return authToken;
    }

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;
    /**
     * 拼接请求头中的参数: 客户端id+:+客户端秘钥的明文
     * @return
     */
    private String getHeadValue() {
        //初始化
        String str = clientId + ":" + clientSecret;
        //使用base64加密
        byte[] encode = Base64.getEncoder().encode(str.getBytes());
        //拼接返回
        return "Basic " + new String(encode);
    }

    public static void main(String[] args) {
        byte[] decode = Base64.getDecoder().decode("YWFhYTpiYmI=");
        System.out.println(new String(decode));
    }

}
