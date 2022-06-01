package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo login(String userName,String userPwd){
        //封装ResultInfo对象给前台
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel =userService.loginCheck(userName, userPwd);
        resultInfo.setResult(userModel);
/*        try {
            UserModel userModel =userService.loginCheck(userName, userPwd);
            resultInfo.setResult(userModel);
        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("登陆失败,请重试!");
        }*/
        return resultInfo;
    }

    /**
     * 用户修改密码
     * @param request
     * @param oldPassWord
     * @param newPassWord
     * @param confirmPassWord
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo pwdUpdate(HttpServletRequest request, String oldPassWord, String newPassWord, String confirmPassWord){
        //封装ResultInfo对象给前台
        ResultInfo resultInfo = new ResultInfo();
        int id = LoginUserUtil.releaseUserIdFromCookie(request);
        userService.pwdUpdate(id,oldPassWord,newPassWord,confirmPassWord);
        resultInfo.setResult(1);
        /*System.out.println("从cookie中拿到的id是"+id);
        try {
            userService.pwdUpdate(id,oldPassWord,newPassWord,confirmPassWord);
            resultInfo.setResult(1);
        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("修改密码失败,请重试!");
        }*/
        success();
        return resultInfo;
    }

    //跳转到修改密码页面
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    /**
     * 多条件分页查询 用户信息 --- 权限管理模块
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryUserByParams(UserQuery userQuery){
        return userService.queryUserByParams(userQuery);
    }

    //跳转到用户管理页面
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("save")
    @ResponseBody
    public ResultInfo save(User user){
        userService.saveUser(user);
        return success();
    }

    /**
     * 修改用户
     * @param user
     * @return
     */
    @PostMapping("updateUser")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success();
    }

    //跳转到用户管理页面
    @RequestMapping("toAddOrUpdate")
    public String toAddOrUpdate(Integer id,HttpServletRequest request){
        if(id!=null){
            User user = userService.selectByPrimaryKey(id);
            AssertUtil.isTrue(null==user, "用户数据异常,请重试!");
            request.setAttribute("user", user);
        }
        return "user/add_update";
    }

    //批量删除用户 --- 权限管理模块
    @RequestMapping("deleteBatch")
    @ResponseBody
    public ResultInfo deleteBatch(Integer[]  ids){
        userService.deleteUsers(ids);
        return success();
    }
}
