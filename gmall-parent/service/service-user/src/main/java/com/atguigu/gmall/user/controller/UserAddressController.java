package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserAddressController {
    @Resource
    private UserAddrService userAddrService;
    /**
     * 查询用户收货地址
     * @return
     */
    @GetMapping("getAddress")
    public Result address(){
        List<UserAddress> userAddresses =
                userAddrService.selectAddress();
        return Result.ok(userAddresses);


    }
}
