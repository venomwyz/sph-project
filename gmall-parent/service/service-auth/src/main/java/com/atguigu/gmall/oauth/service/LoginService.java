package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * 登录接口类
 */
public interface LoginService {

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    public AuthToken login(String username, String password);
}
