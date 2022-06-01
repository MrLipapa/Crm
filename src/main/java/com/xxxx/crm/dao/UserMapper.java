package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.vo.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User,Integer> {
    //根据用户名,查询用户信息
    public User queryByUserName(String userName);

    //多条件查询用户---权限管理模块
    public List<User> queryByUserParams(UserQuery userQuery);

    //批量删除用户 --- 权限管理模块
    public Integer deleteUsers(Integer[] ids);
}
