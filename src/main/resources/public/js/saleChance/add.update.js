layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 加载 下拉框选项(指派人)
     *      第一个参数:请求路径
     *      第二个参数:请求成功之后的回调函数
     */
    $.post(ctx+'/sale_chance/queryAllSales',function (data) {
        //获取下拉框
        var am = $('#assignMan');
        //获取当前指派人id
        var aid = $('#assignId').val();
        if (data!=null){
            for (var i = 0 ; i < data.length ; i++){
                //回显当前数据的指派人
                if(aid == data[i].id){
                    var opt ="<option selected value=" + data[i].id + ">" + data[i].uname + "</option>";
                }else {
                    var opt ="<option value=" + data[i].id + ">" + data[i].uname + "</option>";
            }
                //对下拉框元素 进行后追加
                am.append(opt);
            }
        }
        //重新渲染下拉框内容
        // <因为 请求是在页面渲染之后进行的 执行到此回调函数时页面也定形,所以此时需要刷新重新渲染>
        layui.form.render("select");
    })

    /**
     * 监听表单的提交
     *      on 监听
     *      submit 事件
     */
    form.on("submit(addOrUpdateSaleChance)", function(data){
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
        var url=ctx+'/sale_chance/save';
        //根据 当前页面的请求域中是否有id值,来判断是 修改(有id则是修改) 还是 添加
        if ($('#hidId').val()){
            url=ctx+'/sale_chance/update';
        }

        //发送请求
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

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        console.log("我想取消 但取消不了...");
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 再执行关闭
        parent.layer.close(index);
    });



});
