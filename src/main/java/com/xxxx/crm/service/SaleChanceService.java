package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
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
public class SaleChanceService extends BaseService<SaleChance,Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询 营销机会 信息
     * @param saleChanceQuery
     * @return
     */
    public Map<String,Object> queryByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        //多条件查询
        List<SaleChance> saleChances = saleChanceMapper.queryByParams(saleChanceQuery);
        //按照分页条件,进行格式化数据
        PageInfo<SaleChance> saleChancePageInfo =new PageInfo<>(saleChances);
        //设置 map
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", saleChancePageInfo.getTotal());
        map.put("data", saleChancePageInfo.getList());
        return map;
    }

    /**
     * 添加一条营销机会数据
     *      1.校验参数
     *          customerName    客户名称 非空
     *          linkName        联系人 非空
     *          linkPhone       手机号码 非空 手机号11位正则校验
     *      2.设置默认值
     *          is_valid    数据有效 0无效 1有效
     *          create_date 数据创建时间 new Date()
     *          update_date 数据修改事件
     *          判断用户是否设置了分配人
     *              如果分配了
     *                  assign_man  分配人
     *                  assign_time 分配时间 new Date()
     *                  state       已分配 分配状态 0-未分配 1-已分配
     *                  dev_result  开发中 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
     *                  create_man  数据的创建人 当前登录的用户(交给controller层 获取cookie),直接设置到 salechance对象中
     *              如果未分配
     *                  state       未分配 分配状态 0-未分配 1-已分配
     *                  dev_result  未开发 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
     *       3.执行添加操作,判断是否添加成功
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //1.校验参数
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //2.设置默认值
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //判断是否设置分配人
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            //没有设置分配人
            saleChance.setState(0);
            saleChance.setDevResult(0);
        }else {
            //设置了分配人
            saleChance.setState(1);
            saleChance.setDevResult(1);
            saleChance.setAssignTime(new Date());
        }
        //3.执行添加操作,判断是否添加成功
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)!=1, "营销机会数据添加失败");
    }

    /**
     * 1.校验参数
     *        customerName    客户名称 非空
     *        linkName        联系人 非空
     *        linkPhone       手机号码 非空 手机号11位正则校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名称不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"手机号码不能为空");
        //判断手机号码是否符合规则
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号码不符合规则");
    }

    /**
     * 营销机会数据更新
     *      1.参数校验
     *          id:记录必须存在
     *          customerName:非空
     *          linkMan:非空
     *          linkPhone:非空，11位手机号
     *      2.设置默认值
     *          update_time     修改时间 new Date()
     *          判断是否指派了工作人员
     *              修改前没有指派
     *                  修改后没有指派
     *                      不做任何操作
     *                  修改后指派了
     *                      assign_time 分配时间 new Date()
     *                      state       已分配 分配状态 0-未分配 1-已分配
     *                      dev_result  开发中 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
     *              修改前指派了
     *                  修改后没有指派
     *                      assign_time 修改为 null
     *                      state       未分配 分配状态 0-未分配 1-已分配
     *                      dev_result  未发中 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
     *                  修改后指派了
     *                      判断更改后的人员和更改前的人员有没有变化
     *                          没有变动,不做操作
     *                          有变动,update_time 最新时间 new Date()
     *      3.执行修改操作,判断是否修改成功
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        //判断id是否存在
        AssertUtil.isTrue(saleChance.getId()==null, "数据异常,用户id不能为空!");
        //1.参数校验
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        //2.设置默认值
        saleChance.setUpdateDate(new Date());
        //通过现有的id查询修改前的数据
        SaleChance dbSaleChance = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(dbSaleChance==null, "数据异常,用户不存在!");
        //判断修改前是否有分配人
        if(StringUtils.isBlank(dbSaleChance.getAssignMan())){
            //进入当前行 说明修改前没有指定分配人

            //判断修改后是否有分配人
            if(!StringUtils.isBlank(saleChance.getAssignMan())){
                //进入当前行说明修改后指定了分配人
                    //assign_time 分配时间 new Date()
                    //state       已分配 分配状态 0-未分配 1-已分配
                    //dev_result  开发中 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
                saleChance.setAssignTime(new Date());
                saleChance.setState(1);
                saleChance.setDevResult(1);
            }
            //进入当前行 说明修改后没有指定分配人,什么操作都不做
        }else{
            //进入当前行 说明修改前有分配人

            //判断修改后是否有分配人
            if(StringUtils.isBlank(saleChance.getAssignMan())){
                //进入当前行说明修改后没有指定分配人
                    //assign_time 修改为 null
                    //state       未分配 分配状态 0-未分配 1-已分配
                    //dev_result  未发中 开发状态 0-未开发 1-开发中 2-开发成功 3-开发失败
                /* 此时 SaleChanceMapper.xml 中的setAssignTime和getAssignMan在做判断时,
                判定位空,会跳过对应数据修改阶段,所以应该删掉xml文件中的修改条件 */
                saleChance.setAssignTime(null);
                saleChance.setState(0);
                saleChance.setDevResult(0);
            }else{
                //进入当前行 说明修改后指定了分配人

                //判断更改后的人员和更改前的人员有没有变化
                if(!dbSaleChance.getAssignMan().equals(saleChance.getAssignMan())){
                    //进入当前行 说明修改前后分配人不一样 修改时间 new Date()
                    saleChance.setUpdateDate(new Date());
                }else{
                    //进入当前行 说明修改前后分配人一样,不做任何操作

                    //但前后台都没有对分配时间进行设置,结合172行在xml中对sal条件的修改,此时必会对数据中的updateDate设置为null,
                    // 所以在这里我们要手动为 updateDate 设置时间
                    saleChance.setUpdateDate(dbSaleChance.getUpdateDate());
                }
            }
        }
        //3.执行修改操作,判断是否修改成功
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1, "营销机会修改失败!");
    }

    /**
     * 查询所有销售人员
     */
    public List<Map<String,Object>> queryAllSales(){
        return saleChanceMapper.queryAllSales();
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
    public void updateDevResult(Integer id,Integer devResult){
        //1.检验参数
        AssertUtil.isTrue(null == id, "待更新的id为空!");
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == saleChance, "更新状态为空!");
        //2.执行更新操作,校验操作是否成功
        AssertUtil.isTrue(saleChanceMapper.updateDevResult(id,devResult) != 1, "更新状态失败!");
    }

}
