package com.xxxx.crm.aspect;

import com.xxxx.crm.annotation.RequirePermission;
import com.xxxx.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Autowired
    private HttpSession session;

    /**
     * 环绕通知(需要放行才能到达目标方法上) --- 拦截有这个注解的所有方法
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(com.xxxx.crm.annotation.RequirePermission)")
    public  Object around(ProceedingJoinPoint pjp) throws Throwable {
        //获取当前登录用户的所有权限
        List<Integer> permissions = (List<Integer>) session.getAttribute("permissions");
        //如果没有任何权限,则直接跳转到登陆页面.(有时候会有不通过前台页面按钮访问过来,而直接输入当前方法访问地址的恶意访问)
        if(null == permissions || permissions.size()==0){
            throw  new NoLoginException();
        }
        Object result =null;
        //获取目标方法对象
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //获取目标方法注解的权限值
        RequirePermission requirePermission = methodSignature.getMethod().getDeclaredAnnotation(RequirePermission.class);
        if(!(permissions.contains(requirePermission.code()))){
            throw  new NoLoginException();
        }
        //放行
        result= pjp.proceed();
        return result;
    }
}
