package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件查询 营销机会 信息
     * @param query
     * @return
     */
    public Map<String,Object> queryByParams(CusDevPlanQuery query){
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(query.getPage(), query.getLimit());
        //多条件查询
        List<CusDevPlan> cusDevPlans = cusDevPlanMapper.queryByParams(query);
        //按照分页条件,进行格式化数据
        PageInfo<CusDevPlan> cusDevPlanPageInfo = new PageInfo<>(cusDevPlans);
        //设置 map
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", cusDevPlanPageInfo.getTotal());
        map.put("data", cusDevPlanPageInfo.getList());
        return map;
    }

    /**
     * 添加用户开发机会数据
     *      1.数据校验
     *          saleChanceId    营销机会的id 非空/数据存在
     *          planItem    计划内容 非空
     *          planDate    计划时间 非空
     *      2.直射默认值
     *          is_valid    数据是否有效
     *          create_date
     *          update_date
     *      3.执行添加操作,判断添加是否正确
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        //1.数据校验
        checkParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
        //2.设置默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        //3.执行添加操作,判断添加是否正确
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1, "开发计划添加失败!");
    }

    /**
     * 修改用户开发机会数据
     *      1.数据校验
     *          计划项 id      当前计划的id
     *          saleChanceId    营销机会的id 非空/数据存在
     *          planItem    计划内容 非空
     *          planDate    计划时间 非空
     *      2.直射默认值
     *          update_date
     *      3.执行修改操作,判断修改是否正确
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //1.数据校验
        AssertUtil.isTrue(cusDevPlan.getId()==null||cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null, "待修改的开发项id异常,请重试!");
        checkParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
        //2.设置默认值
        cusDevPlan.setUpdateDate(new Date());
        //3.执行添加操作,判断添加是否正确
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "开发计划添加失败!");
    }

    /**
     * 校验 机会添加和修改 的参数
     * @param saleChanceId
     * @param planItem
     * @param planDate
     */
    private void checkParams(Integer saleChanceId, String planItem, Date planDate) {
        AssertUtil.isTrue(null == saleChanceId || null == saleChanceMapper.selectByPrimaryKey(saleChanceId),"营销机会不存在!");
        AssertUtil.isTrue(StringUtils.isBlank(planItem), "计划开发详情不能为空!");
        AssertUtil.isTrue(null==planDate, "计划开发时间不能为空!");

    }

    /**
     * 删除计划开发项
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Integer id) {
        AssertUtil.isTrue(id==null, "要删除的计划开发项id为空!");
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==cusDevPlan, "待删除的计划开发项不存在!");
        cusDevPlan.setIsValid(0);
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"删除开发计划失败!");
    }
}
