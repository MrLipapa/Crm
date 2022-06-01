package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {
    //查询所有模块信息<id,name,pId---包装在ModuleModule中>---授权模块使用
    public List<TreeModule> queryAllModules(Integer roleId);

    //查询所有模块信息  --- 资源管理模块使用
    public List<Module> queryModules();

    //一下方法在添加模块资源时使用
    Module queryModulByGradeName(@Param("grade") Integer grade,@Param("moduleName") String moduleName);

    Module queryModulByGradeUrl(@Param("grade") Integer grade, @Param("url") String url);

    Module queryModulById(Integer parentId);

    Module queryModulByOptValue(String optValue);
}
