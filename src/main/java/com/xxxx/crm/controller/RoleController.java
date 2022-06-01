package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.utils.AssertUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;


    //查询所有的角色名称和对应的id返回给前台使用
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer id){
        return roleService.queryAllRoles(id);
    }

    @RequestMapping("index")
    public String toRoleIndex(){
        return "role/role";
    }

    /**
     * 多条件分页查询角色数据
     * @param roleQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> userList(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 跳转到用户角色授权页面
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer rId, HttpServletRequest request){
        AssertUtil.isTrue(rId==null, "角色id异常不存在");
        request.setAttribute("roleId", rId);
        return "role/grant";
    }

    @RequestMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mIds){
        roleService.addGrant(roleId,mIds);
        return success();
    }
}
