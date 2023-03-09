package com.atguigu.gmall.oauth.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录映射接口
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
