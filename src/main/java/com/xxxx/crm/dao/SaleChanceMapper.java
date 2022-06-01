package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.vo.SaleChance;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {
    //多条件查找用户
    public List<SaleChance> queryByParams(SaleChanceQuery saleChanceQuery);

    //查询所有销售人员
    public List<Map<String,Object>> queryAllSales();

    //更新开发状态
    public Integer updateDevResult(@Param("id") Integer id, @Param("devResult") Integer devResult);
}
