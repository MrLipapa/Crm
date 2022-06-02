package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 跳转到用户计划详情页
     * @return
     */
    @RequestMapping("index")
    public String toIndex(){
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 打开客户计划详情页
     * @param sId
     * @param request
     * @return
     */
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer sId, HttpServletRequest request){
        AssertUtil.isTrue(sId==null, "数据异常,请重试!");
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(sId);
        AssertUtil.isTrue(saleChance==null, "数据异常,请重试!");
        //将数据存储在作用域中
        request.setAttribute("saleChance",saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     *多条件查询用户机会数据
     * @param query
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryByParams(CusDevPlanQuery query){
        return cusDevPlanService.queryByParams(query);
    }

    /**
     * 添加用户开发机会数据
     * @param cusDevPlan
     */
    @RequestMapping("save")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success();
    }

    /**
     * 修改用户开发机会数据
     * @param cusDevPlan
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success();
    }

    /**
     * 跳转到计划项添加/修改页面
     * @return
     */
    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Integer id,Integer sId,HttpServletRequest request){
        if(id != null){
            CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
            AssertUtil.isTrue(null==cusDevPlan, "计划项数据异常,请重试!");
            request.setAttribute("cusDevPlan", cusDevPlan);
        }
        request.setAttribute("sId", sId);
        return "cusDevPlan/add_update";
    }

    /**
     * 删除计划开发项
     * @param id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo delCusDevPlan(Integer id){
        cusDevPlanService.delete(id);
        return success();
    }
}
