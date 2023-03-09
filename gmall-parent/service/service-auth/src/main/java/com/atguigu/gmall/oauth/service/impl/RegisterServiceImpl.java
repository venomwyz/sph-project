package com.atguigu.gmall.oauth.service.impl;

import com.atguigu.gmall.common.execption.Assert;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.RegexValidateUtils;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.oauth.mapper.UserInfoMapper;
import com.atguigu.gmall.oauth.service.RegisterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class RegisterServiceImpl implements RegisterService {

    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     *注册
     * @param userInfo
     */
    @Override
    public void addUser(UserInfo userInfo) {
        //校验验证码 TODO----验证码校验

        //校验数据
        if (userInfo==null || userInfo.getId()==null){
            throw new RuntimeException("参数为空");
        }
        //校验数据是否已经存在
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getLoginName,userInfo.getLoginName());
        UserInfo userInfo1 = userInfoMapper.selectOne(wrapper);

        //如果userInfo1不为空说明有数据，不能注册
        if (userInfo1 != null){
            throw new RuntimeException("用户已经注册，请登录");
        }
        //插入
        UserInfo userInfo2 = new UserInfo();
        userInfo2.setLoginName(userInfo.getLoginName());
        userInfo2.setNickName(userInfo.getNickName());
        //密码加密
        String encode = new BCryptPasswordEncoder().encode(userInfo.getPasswd());
        userInfo2.setPasswd(encode);
        //电话号码判断 Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);、

        Assert.isTrue(RegexValidateUtils.checkCellphone(userInfo.getPhoneNum()), ResultCodeEnum.LOGIN_AUTH);

        userInfo2.setEmail(userInfo.getEmail());
        userInfo2.setHeadImg(userInfo.getHeadImg());
        userInfo2.setUserLevel(userInfo.getUserLevel());
        int insert = userInfoMapper.insert(userInfo2);
        if (insert <=0){
            throw new RuntimeException("注册用户失败");
        }
    }
}
