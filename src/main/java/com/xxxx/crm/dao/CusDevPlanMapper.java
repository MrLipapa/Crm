package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.vo.CusDevPlan;

import java.util.List;

public interface CusDevPlanMapper extends BaseMapper<CusDevPlan,Integer> {
    public List<CusDevPlan> queryByParams(CusDevPlanQuery query);
}
