package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    //判断当前角色之前是否存在权限模块资源
    Integer countPermissions(Integer roleId);

    //如果当前用户之前存在权限模块,则全部删除
    Integer deletePermissions(Integer roleId);

    //查询当前目标角色拥有的权限
    List<Integer> selectPermissionByRoleId(Integer roleId);

    //查询当前用户的权限码
    List<String> selectUserRoleAvlValue(Integer id);
}
