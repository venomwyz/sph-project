package com.atguigu.gmall.oauth.util;

import jodd.crypt.BCrypt;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.Base64;

/**
 * 令牌实体类
 */
@Data
public class AuthToken implements Serializable{
    //令牌信息
    String accessToken;
    //刷新token(refresh_token)
    String refreshToken;
    //jwt短令牌
    String jti;

}