package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddrMapper;
import com.atguigu.gmall.user.service.UserAddrService;
import com.atguigu.gmall.user.util.UserThreadLocalUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class UserAddrServiceImpl implements UserAddrService {
    @Resource
    private UserAddrMapper userAddrMapper;
    /**
     * 用户地址查询
     *
     * @return
     */
    @Override
    public List<UserAddress> selectAddress() {
        String username=UserThreadLocalUtil.get();
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getUserId,username);
        List<UserAddress> userAddresses = userAddrMapper.selectList(queryWrapper);
        return userAddresses;

    }
}
