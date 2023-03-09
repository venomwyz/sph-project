package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * 用户地址service层
 */
public interface UserAddrService {
    /**
     * 用户地址查询
     * @return
     */
    List<UserAddress> selectAddress();
}
