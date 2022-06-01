package com.xxxx.crm.interceptors;

import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 *      拦截未登录的用户到登录页面
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;
    /**
     * 判断用户是否是登录状态
     *      获取Cookie对象，解析用户ID的值
     *      如果用户ID不为空，且在数据库中存在对应的用户记录，表示请求合法
     *      否则，请求不合法，进行拦截，重定向到登录页面
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取Cookie对象，解析用户ID的值
        int id = LoginUserUtil.releaseUserIdFromCookie(request);
        //判断用户ID是否不为空，且数据库中存在对应的用户记录
        if(id==0||userMapper.selectByPrimaryKey(id)==null){
            //用户未登陆,抛出异常
            throw new NoLoginException();
        }
        return true;
    }
}
