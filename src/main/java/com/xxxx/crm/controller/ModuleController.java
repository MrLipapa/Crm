package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.service.ModuleService;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;

    //查询所有模块信息<id,name,pId---包装在ModuleModule中>
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModule> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    //查询所有模块信息  --- 资源管理模块使用
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryModules(){
        return moduleService.queryModules();
    }

    /**
     * 跳转到模块资源管理页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "module/module";
    }

    /**
     * 添加模块资源
     * @param module
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo moduleAdd(Module module){
        moduleService.moduleAdd(module);
        return success("资源模块添加成功");
    }

    @RequestMapping("toAdd")
    public String toAdd(Integer grade, Integer parentId, HttpServletRequest request){
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);

        return "module/add";
    }

    @RequestMapping("updateModulePage")
    public String updateModulePage(Integer id,HttpServletRequest request){
        Module module = moduleService.selectByPrimaryKey(id);
        request.setAttribute("module",module);
        return "module/update";
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo moduleUpdate(Module module){
        moduleService.moduleUpdate(module);
        return success("资源修改成功");
    }
}
