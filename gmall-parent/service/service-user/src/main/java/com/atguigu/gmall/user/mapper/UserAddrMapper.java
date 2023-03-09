package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址查询映射
 */
@Mapper
public interface UserAddrMapper extends BaseMapper<UserAddress> {
}
