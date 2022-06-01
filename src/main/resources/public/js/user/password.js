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
    form.on("submit(saveBtn)", function(data){
        //console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        //console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        //获取数据
        var oldPassWord = data.field.old_password;
        var newPassWord = data.field.new_password;
        var confirmPassWord  =data.field.again_password;

        //校验数据
        if(oldPassWord == newPassWord){
            layer.msg("新密码不能与原始密码相同!");
            return false;
        }
        if(newPassWord != confirmPassWord){
            layer.msg("确认密码与新密码不同!");
            return false;
        }

        //发送请求
        $.ajax({
            type:"post",
            url:ctx+"/user/update",
            data:{
                oldPassWord:oldPassWord,
                newPassWord:newPassWord,
                confirmPassWord:confirmPassWord
            },
            dataType:'json',
            success:function(data){
                if(data.code==200){
                    //清楚登录状态,删除cookie
                    $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                    $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                    $.removeCookie("trueName",{domain:"localhost",path:"/crm"});

                    //跳转到登录页面
                    window.parent.location.href=ctx+"/index";
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            }
        })

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    })

});
