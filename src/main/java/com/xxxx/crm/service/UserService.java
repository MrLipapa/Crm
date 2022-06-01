package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 校验用户输入的登录信息
     *     1.校验参数是否为空
     *         如果为空，抛异常
     *     2.通过用户名查询数据库数据
     *         如果未查到，抛异常(用户不存在)
     *     3.校验前台传来的密码和数据库中的密码是否一致 (前台密码加密后再校验)
     *         如果不一致，抛异常(密码错误)
     *     4.封装 ResultInfo 对象给前台（根据前台需求：usermodel对象封装后传到前台使用）
     * @param userName
     * @param userPwd
     */
    public UserModel loginCheck(String userName,String userPwd){
        //校验用户输入参数是否为空
        checkLoginData(userName,userPwd);
        //调用dao层通过用户名查询数据库中用户的信息
        User user = userMapper.queryByUserName(userName);
        //根据dao层查询结果,校验数据库中是否存在该用户
        checkLoginUser(user);
        //校验用户密码是否匹配
        checkLoginPwd(user.getUserPwd(),userPwd);
        //根据前台需求：usermodel对象封装后传到前台使用
        return backUserModel(user);
    }

    /**
     * 封装ResultInfo对象给前台（根据前台需求：usermodel对象封装后传到前台使用）
     * 准备前台cookie需要的数据,即usermodel
     * @param user
     * @return
     */
    private UserModel backUserModel(User user) {
        UserModel userModel = new UserModel();
        String id = UserIDBase64.encoderUserID(user.getId());
        System.out.println("我要给question一个"+id);
        userModel.setUserId(id);
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 校验用户密码是否匹配
     * @param dbPwd
     * @param userPwd
     */
    private void checkLoginPwd(String dbPwd,String userPwd) {
        //数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作比较
        String encode = Md5Util.encode(userPwd);
        //用加密后的输入密码与数据库中存储的密码进行比对
        AssertUtil.isTrue(!encode.equals(dbPwd), "用户密码错误,请检查!");
    }

    /**
     * 根据dao层查询结果,校验数据库中是否存在该用户
     * @param user
     */
    private void checkLoginUser(User user) {
        AssertUtil.isTrue(null == user, "用户名输入有误,该用户不存在!");
    }

    /**
     * 校验用户输入参数是否为空
     * @param userName
     * @param userPwd
     */
    private void checkLoginData(String userName,String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "用户密码不能为空!");
    }

    /**
     * 修改用户密码
     *      1.确保用户是否是登录状态获取cookie中的id 非空 查询数据库
     *      2.校验老密码 非空  老密码必须要跟数据库中密码一致
     *      3.新密码    非空  新密码不能和原密码一致
     *      4.确认密码  非空  确认必须和新密码一致
     *      5.执行修改操作，返回ResultInfo
     * @param userId
     * @param oldPassWord
     * @param newPassWord
     * @param confirmPassWord
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void pwdUpdate(Integer userId,String oldPassWord,String newPassWord,String confirmPassWord){
        //1.确保用户是否是登录状态获取cookie中的id<controller层实现> 非空 查询数据库
        AssertUtil.isTrue(userId==null, "用户未登录!");
        User user = userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user==null, "用户状态异常!");
        //校验原始密码\新密码\确认密码
        checkPassWord(oldPassWord,newPassWord,confirmPassWord,user.getUserPwd());
        //5.执行修改操作，返回 UserModel
        user.setUserPwd(Md5Util.encode(newPassWord));
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1, "修改失败!");
    }

    /**
     * 校验密码:
     *      2.校验老密码 非空  老密码必须要跟数据库中密码一致
     *      3.新密码    非空  新密码不能和原密码一致
     *      4.确认密码  非空  确认必须和新密码一致
     * @param oldPassWord
     * @param newPassWord
     * @param confirmPassWord
     * @param dbPassWord
     */
    private void checkPassWord(String oldPassWord, String newPassWord, String confirmPassWord,String dbPassWord) {
        //校验老密码 非空  老密码必须要跟数据库中密码一致
        AssertUtil.isTrue(StringUtils.isBlank(oldPassWord), "原始密码不能为空!");
        AssertUtil.isTrue(!dbPassWord.equals(Md5Util.encode(oldPassWord)), "原始密码错误!");
        //新密码    非空  新密码不能和原密码一致
        AssertUtil.isTrue(StringUtils.isBlank(newPassWord), "新密码不能为空!");
        AssertUtil.isTrue(newPassWord.equals(oldPassWord), "新密码不能与原始密码相同!");
        //确认密码  非空  确认必须和新密码一致
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassWord),"确认密码不能为空!");
        AssertUtil.isTrue(!confirmPassWord.equals(newPassWord), "确认密码与新密码不同!");
    }

    /**
     * 多条件分页查询 用户信息 --- 权限管理模块
     * @param userQuery
     * @return
     */
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        Map<String,Object> map=new HashMap<>();
        //开启分页
        PageHelper.startPage(userQuery.getPage(), userQuery.getLimit());
        //多条件查询
        List<User> users = userMapper.queryByUserParams(userQuery);
        //按照分页条件,进行格式化数据
        PageInfo<User> userPageInfo =new PageInfo<>(users);
        //设置 map
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", userPageInfo.getTotal());
        map.put("data", userPageInfo.getList());
        return map;
    }

    /**
     * 添加用户
     *   1.校验参数
     *       用户名  非空 | 唯一
     *       邮箱    非空
     *       手机号  非空 | 格式正确
     *   2.设置默认值
     *      is_valid
     *      update_date
     *      create_date
     *      user_password  设置用户默认密码 123456(加密MD5)
     *  3.执行添加操作
     * @param user
     */
    public void saveUser(User user){
        //1.校验参数
        //用户名不能为空
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名不能为空!");
        //用户名唯一
        AssertUtil.isTrue(null != userMapper.queryByUserName(user.getUserName()),"用户名已存在!");
        checkParams(user.getEmail(),user.getPhone());
        //2.设置默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //设置用户默认密码 123456(加密MD5)
        user.setUserPwd(Md5Util.encode("123456"));
        //3.执行添加操作,校验操作是否成功
        //AssertUtil.isTrue(userMapper.insertSelective(user)!=1, "用户添加失败!");
        // 在mybatis中通过插入语句,返回对应用户的主键id
        // 执行添加操作，设置对应sql属性，主键返回到user对象中
        AssertUtil.isTrue(userMapper.insertHasKey(user)!=1, "用户添加失败!");
        //绑定角色给用户对象
        relationRole(user.getId(),user.getRoleIds());
    }

    //校验参数
    private void checkParams(String email, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号不能为空!");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号格式错误!");
    }

    /**
     * 修改用户
     *   1.
     *       id     非空 | 存在
     *       用户名  非空 | 唯一
     *       邮箱    非空
     *       手机号  非空 | 格式正确
     *   2.设置默认值
     *      update_date
     *  3.执行修改操作操作
     * @param user
     */
    public void updateUser(User user){
        //1.校验参数
        //id 非空且存在
        AssertUtil.isTrue(null == user.getId() && null == userMapper.selectByPrimaryKey(user.getId()),"用户id异常,请重试!");
        //校验用户名
        //用户名非空
        AssertUtil.isTrue(StringUtils.isBlank(user.getUserName()), "用户名不能为空!");
        //根据用户名查询用户信息,若查询结果为空(表示修改过用户名,则修改后用户名必须不存在在db中)
        // 并且用户id 和 查询结果id相同,则可以进行修改 --- 仔细理解这个思路  不太好懂
        User dbUser = userMapper.queryByUserName(user.getUserName());
        AssertUtil.isTrue(dbUser != null && dbUser.getId() != user.getId(),"用户名已存在!");
        //2.设置默认值
        user.setUpdateDate(new Date());
        //3.执行修改操作,并校验操作是否成功
        AssertUtil.isTrue(updateByPrimaryKeySelective(user) != 1, "用户信息修改失败,请重试!");
        //修改角色给用户对象
        relationRole(user.getId(),user.getRoleIds());
    }

    //批量删除用户 --- 权限管理模块
    public void deleteUsers(Integer[]  ids){
        AssertUtil.isTrue(ids==null||ids.length<1, "未选中任何用户!");
        AssertUtil.isTrue(userMapper.deleteUsers(ids)!=ids.length, "用户删除失败!");
    }

    /**
     * 给用户绑定角色
     * @param id
     * @param roleIds
     */
    private void relationRole(Integer id, String roleIds) {
        //修改角色操作：查询是否原来就有角色，如果有那么直接删除再绑定新角色
        Integer count = userRoleMapper.countUserRole(id);
        if(count > 0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoles(id) != count, "删除该用户原有角色信息失败!");
        }
        AssertUtil.isTrue(roleIds==null||roleIds.length()<1, "请选择用户角色!");
        //切割获取到每个id
        String[] split = roleIds.split(",");
        //准备一个容器接收遍历出来的新对象/新数据
        List<UserRole> urs = new ArrayList<>();
        for (String idStr : split) {
            UserRole userRole = new UserRole();
            userRole.setUserId(id);
            userRole.setRoleId(Integer.parseInt(idStr));
            userRole.setCreateDate(new Date());
            userRole.setUpdateDate(new Date());

            //将数据添加到集合中
            urs.add(userRole);
        }
        //执行批量添加操作
        AssertUtil.isTrue(userRoleMapper.insertBatch(urs) != split.length, "批量添加用户失败!");
    }
}
