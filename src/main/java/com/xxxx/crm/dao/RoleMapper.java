package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    //查询所有的角色名称和对应的id返回给前台使用
    public List<Map<String,Object>> queryAllRoles(Integer id);

    //多条件分页查询角色信息
    public Map<String,Object> userList(String roleName);
}
