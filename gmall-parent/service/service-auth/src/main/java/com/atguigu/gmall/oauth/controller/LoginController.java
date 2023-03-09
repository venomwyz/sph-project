package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义用户登录的控制层
 */
@RestController
@RequestMapping(value = "/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    /**
     * 自定义登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping
    public Result login(String username, String password){
        //登录
        AuthToken login = loginService.login(username, password);
        //返回
        return Result.ok(login);
    }


}
