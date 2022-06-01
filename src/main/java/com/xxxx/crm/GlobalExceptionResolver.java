package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
//@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    /**
     * 方法返回值类型:
     *      视图
     *      JSON
     * 如何判断方法的返回类型：
     *      如果方法级别配置了 @ResponseBody 注解，表示方法返回的是JSON；
     *      反之，返回的是视图页面
     * @param request
     * @param response
     * @param handler
     * @param e
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        ModelAndView mv = new ModelAndView();

        if(e instanceof NoLoginException){
            mv.setViewName("redirect:/index");
            return mv;
        }
        //设置默认异常处理
        mv.setViewName("error");
        mv.addObject("code",300);
        mv.addObject("msg","系统异常,请重试!");

        //判断异常是否属于HandlerMethod类型<HandlerMethod类型是请求匹配条件包装后相对应的controller方法>
        if (handler instanceof HandlerMethod) {
            //将 handler 转换成  controller方法对象<即 HandlerMethod 类型对象>
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取 controller方法所对应的注解
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);

            //判断目标方法返回的是视图还是 json 数据
            //判断 ResponseBody 注解是否存在 (如果不存在，表示返回的是视图; 如果存在，表示返回的是JSON)
            if(responseBody == null){
                //返回视图的相关操作
                //判断是否返回自定义异常
                if (e instanceof ParamsException){
                    ParamsException paramsException = (ParamsException) e;
                    mv.addObject("code",paramsException.getCode());
                    mv.addObject("msg",paramsException.getMsg());
                }
                return mv;
            }else {
                //返回json数据的相关操作
                //设置自定义异常处理信息
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常,请重试!");

                //判断是否返回自定义异常
                if (e instanceof ParamsException){
                    ParamsException paramsException = (ParamsException) e;
                    resultInfo.setCode(paramsException.getCode());
                    System.out.println(paramsException.getCode());
                    resultInfo.setMsg(paramsException.getMsg());
                    System.out.println(paramsException.getMsg());
                }
                //将 resultInfo 数据返回给前台的 ajax 回调函数<只能接收 json 数据>
                //设置数据传输的类型和编码格式
                response.setContentType("application/json;charset=utf-8");
                Writer writer=null;
                try {
                    //因为方法只能返回 ModelAndView 类型视图,而controller方法返回的是
                    writer=response.getWriter();
                    //将数据对象转换成json格式 转换出去
                    writer.write(JSON.toJSONString(resultInfo));
                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }finally {
                    if(writer!=null){
                        try {
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return mv;
    }
}
