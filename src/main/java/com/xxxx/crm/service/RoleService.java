package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    //查询所有的角色名称和对应的id返回给前台使用
    public List<Map<String,Object>> queryAllRoles(Integer id){
        return roleMapper.queryAllRoles(id);
    }

    //多条件分页查询角色信息
    public Map<String,Object> userList(String roleName){
        return roleMapper.userList(roleName);
    }

    /**
     * 给指定角色 添加权限
     * @param roleId
     * @param mIds
     */
    public void addGrant(Integer roleId, Integer[] mIds) {
        //校验参数
        //判断角色是否存在
        AssertUtil.isTrue(roleId==null, "待添加模块的角色id异常!");
        AssertUtil.isTrue(roleMapper.selectByPrimaryKey(roleId)==null, "待添加模块的角色不存在!");
        System.out.println("mids===="+ Arrays.toString(mIds));
        //判断接收到的需要进行绑定的权限是否存在
        //AssertUtil.isTrue(mIds==null||mIds.length<1, "需要绑定的模块不存在");
        //判断当前角色之前是否存在权限模块资源
        Integer count = permissionMapper.countPermissions(roleId);
        //如果当前用户之前存在权限模块,则全部删除
        if(count > 0){
            AssertUtil.isTrue(permissionMapper.deletePermissions(roleId) != count, "该用户删除旧权限模块异常");
        }
        //给当前角色绑定新的全新模块
        List<Permission> permissions = new ArrayList<>();//准备容器 存放待添加的permission对象
        for (Integer mId:mIds) {
            Permission permission = new Permission();

            permission.setRoleId(roleId);
            permission.setModuleId(mId);
            permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());//设置权限码  需要去module表中查询得到
            //System.out.println(permission.getAclValue());
            permission.setCreateDate(new Date());
            permission.setUpdateDate(new Date());

            permissions.add(permission);
        }
        //执行批量添加操作，绑定多个权限
        AssertUtil.isTrue(permissionMapper.insertBatch(permissions)!=permissions.size(), "权限模块添加失败!");
    }
}
