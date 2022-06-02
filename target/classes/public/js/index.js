layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 监听表单的提交
     *      on 监听
     *      submit 事件
     */
    form.on("submit(login)", function(data){
        //console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        //console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        //数据校验 TODO
        //发送请求
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                userName:data.field.username,
                userPwd:data.field.password
            },
            dataType:'json',
            success:function(data){
                if(data.code==200){
                    //存储 cookie,cookie以键值对形式存储数据
                    $.cookie("userIdStr",data.result.userId);
                    $.cookie("userName",data.result.userName);
                    $.cookie("trueName",data.result.trueName);

                    //记住密码
                    if($("#rememberMe").is(":checked")){
                        $.cookie("userIdStr", data.result.userIdStr, { expires: 7 });
                        $.cookie("userName", data.result.userName, { expires: 7 });
                        $.cookie("trueName", data.result.trueName, { expires: 7 });
                    }


                    //跳转到主页面
                    window.location.href=ctx+"/main";
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            }
        })

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    })

});

/*function isTrue(str){
    if(str==null||str.trim()==""){
        return true;
    }
    return false;
}*/
