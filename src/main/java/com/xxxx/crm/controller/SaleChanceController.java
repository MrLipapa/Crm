package com.xxxx.crm.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 多条件分页查询 营销机会 信息
     * @param saleChanceQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryByParams(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest request){
        //根据flag区分 营销机会管理 和 客户开发计划(flag非空)
        if(flag != null && flag == 1){
            //根据 request 获取当前 登录用户的id
            int id = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(id);
        }
        return saleChanceService.queryByParams(saleChanceQuery);
    }

    /**
     * 跳转到多条件分页查询页面
     */
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     * 跳转到多条件分页查询页面
     */
    @RequestMapping("toAddUpdatePage")
    public String toAddUpdatePage(Integer id,HttpServletRequest request){
        //如果是修改操作 需要将待修改的数据映射到前台
        if(id!=null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            AssertUtil.isTrue(saleChance==null, "数据异常请重试!");
            request.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 跳转到多条件分页查询页面
     */
    @RequestMapping("save")
    @ResponseBody
    @RequirePermission(code = "101002")
    public ResultInfo save(HttpServletRequest request, SaleChance saleChance){
        //设置创建人
        String userName = CookieUtil.getCookieValue(request, "userName");
        saleChance.setCreateMan(userName);
        saleChanceService.addSaleChance(saleChance);
        return success();
    }

    /**
     * 修改营销机会数据
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success();
    }

    /**
     * 查询所有销售人员
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return saleChanceService.queryAllSales();
    }

    /**
     * 更新开发状态
     *      1.检验参数
     *          id 非空
     *          devResult 非空
     *      2.执行更新操作,校验操作是否成功
     * @param id
     * @param devResult
     */
    @RequestMapping("updateDevResult")
    @ResponseBody
    public ResultInfo updateDevResult(Integer id,Integer devResult){
        saleChanceService.updateDevResult(id, devResult);
        return success();
    }
}
