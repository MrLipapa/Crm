layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    // 引入 formSelects 模块
    formSelects = layui.formSelects;

    /**
     * 加载下拉框数据
     */
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx + "/role/queryAllRoles?id="+$('[name="id"]').val(),
        //自定义返回数据中name的key, 默认 name --- 需要和sql.xml文件中返回map的值名称保持一致
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);

    /**
     * 监听表单的提交
     *      on 监听
     *      submit 事件
     */
    form.on("submit(addOrUpdateUser)", function(data){
        //console.log(data.elem) //被执行事件的元素DOM对象，一般为button对象
        //console.log(data.form) //被执行提交的form对象，一般在存在form标签时才会返回
        console.log(data.field) //当前容器的全部表单字段，名值对形式：{name: value}

        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });

        //设置请求发送路径<默认是添加数据>
        var url=ctx+'/user/save';
        //根据 当前页面的请求域中是否有id值,来判断是 修改(有id则是修改) 还是 添加
        //但这里的url并不需要传递 id参数 因为前台使用的是form表单,表单中的所有元素会 随着请求全部提交到后台
        if ($('[name="id"]').val()){
            url=ctx+'/user/updateUser';
        }

        //发送请求
        //第一个参数 是请求地址,第二个参数是 表单内容,第三个参数是请求成功的回调函数
        $.post(url,data.field,function (data) {
            if(data.code==200){
                //关闭提交加载层弹出框
                layer.close(index);
                //关闭iframe
                layer.closeAll("iframe");
                //刷新父页面,将添加的新数据加载出来
                parent.location.reload();
            }else {
                layer.msg(data.msg,{icon:5});
            }
        })

        return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });
    
});
