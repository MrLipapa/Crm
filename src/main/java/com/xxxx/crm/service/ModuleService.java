package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    //查询所有模块信息<id,name,pId---包装在ModuleModule中>
    public List<TreeModule> queryAllModules(Integer roleId){
        //校验参数目标角色是否存在
        AssertUtil.isTrue(roleId==null||roleMapper.selectByPrimaryKey(roleId)==null,"目标角色不存在!");
        //查询当前目标角色拥有的权限
        List<Integer> mIds = permissionMapper.selectPermissionByRoleId(roleId);
        //查询所有的模块
        List<TreeModule> treeModules = moduleMapper.queryAllModules(roleId);
        //遍历所有模块,并标记当前角色拥有的模块权限资源
        for (TreeModule treeModule:treeModules) {
            //获取当前遍历对象的模块id
            Integer id = treeModule.getId();
            //判断当前角色拥有的模块权限mIds是否包含了当前遍历对象的模块id
            if (mIds.contains(id)) {    //当前方法判断某个数据是否存在于集合当中
                treeModule.setChecked(true);
                treeModule.setOpen(true);
            }
        }
        return treeModules;
    }

    //查询所有模块信息  --- 资源管理模块使用
    public Map<String, Object> queryModules(){
        Map<String, Object> map = new HashMap<>();
        List<Module> modules = moduleMapper.queryModules();
        map.put("code", 0);
        map.put("msg", "");
        map.put("data", modules);
        map.put("count", modules.size());
        return map;
    }

    /**
     * 模块添加
     *   1.数据校验
     模块名称
     非空，同级唯一
     地址 URL
     二级菜单：非空，同级唯一
     父级菜单 parentId
     一级：null | -1
     二级|三级：非空 | 必须存在
     层级 grade
     非空  值必须为 0|1|2
     权限码
     非空  唯一
     2.默认值
     is_valid
     updateDate
     createDate
     3.执行添加操作  判断受影响行数
     *
     * @param module
     */
    @Transactional
    public void moduleAdd(Module module) {
        //层级 grade  非空  值必须为 0|1|2
        AssertUtil.isTrue(module.getGrade() == null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade() == 0 || module.getGrade() == 1 || module.getGrade() == 2),"层级有误");

        //模块名称 非空  同级唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        Module dbModule = moduleMapper.queryModulByGradeName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule != null,"模块名称已存在");

        // 二级菜单URL：非空，同级唯一
        if(module.getGrade() == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"模块地址不能为空");
            dbModule = moduleMapper.queryModulByGradeUrl(module.getGrade(),module.getUrl());
            AssertUtil.isTrue(dbModule != null,"地址已存在，请重新输入");
        }

        //父级菜单  二级|三级：非空 | 必须存在
        if(module.getGrade() == 1 || module.getGrade() == 2){
            AssertUtil.isTrue(module.getParentId() == null,"父ID不能为空");
            dbModule = moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule == null,"父ID不存在");
        }

        //权限码  非空  唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        dbModule = moduleMapper.queryModulByOptValue(module.getOptValue());
        AssertUtil.isTrue(dbModule != null,"权限码已存在");

        //默认值
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        //执行添加操作  判断受影响行数
        AssertUtil.isTrue(moduleMapper.insertSelective(module) < 1,"模块添加失败");
    }

    /**
     * 修改模块
     1.数据校验
     id
     非空，并且资源存在
     模块名称
     非空，同级唯一
     地址 URL
     二级菜单：非空，同级唯一
     父级菜单 parentId
     一级：null | -1
     二级|三级：非空 | 必须存在
     层级 grade
     非空  值必须为 0|1|2
     权限码
     非空  唯一
     2.默认值
     is_valid
     updateDate
     createDate
     3.执行修改操作  判断受影响行数
     * @param module
     */
    @Transactional
    public void moduleUpdate(Module module) {
        // id  非空，并且资源存在
        AssertUtil.isTrue(module.getId() == null,"待删除的资源不存在");
        Module dbModule = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(dbModule == null,"系统异常");

        //层级 grade  非空  值必须为 0|1|2
        AssertUtil.isTrue(module.getGrade() == null,"层级不能为空");
        AssertUtil.isTrue(!(module.getGrade() == 0 || module.getGrade() == 1 || module.getGrade() == 2),"层级有误");

        //模块名称 非空  同级唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        dbModule = moduleMapper.queryModulByGradeName(module.getGrade(),module.getModuleName());
        AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"模块名称已存在");

        // 二级菜单URL：非空，同级唯一
        if(module.getGrade() == 1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"模块地址不能为空");
            dbModule = moduleMapper.queryModulByGradeUrl(module.getGrade(),module.getUrl());
            AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"地址已存在，请重新输入");
        }

        //父级菜单  二级|三级：非空 | 必须存在
        if(module.getGrade() == 1 || module.getGrade() == 2){
            AssertUtil.isTrue(module.getParentId() == null,"父ID不能为空");
            dbModule = moduleMapper.queryModulById(module.getParentId());
            AssertUtil.isTrue(dbModule == null && !(module.getId().equals(dbModule.getId())),"父ID不存在");
        }

        //权限码  非空  唯一
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        dbModule = moduleMapper.queryModulByOptValue(module.getOptValue());
        AssertUtil.isTrue(dbModule != null && !(module.getId().equals(dbModule.getId())),"权限码已存在");

        //默认值
        module.setUpdateDate(new Date());

        //执行修改操作
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) < 1,"资源修改失败");
    }
}
