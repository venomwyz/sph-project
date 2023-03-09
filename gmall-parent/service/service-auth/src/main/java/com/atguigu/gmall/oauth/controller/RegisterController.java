package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.oauth.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("user/register")
public class RegisterController {
    /**
     * 用户注册
     * @param userInfo
     * @return
     */
    @Resource
    private RegisterService registerService;
    @PostMapping
    public Result register(@RequestBody UserInfo userInfo){
        registerService.addUser(userInfo);
        return Result.ok();
    }
}
