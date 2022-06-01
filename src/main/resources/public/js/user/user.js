layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 用户列表展示
     */
    var  tableIns = table.render({
        elem: '#userList',
        url : ctx + '/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '用户名', minWidth:50, align:"center"},
            {field: 'email', title: '用户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '用户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });

    //数据表格重载
    //监听搜索按钮 --- 完成数据表格重载
    $('#btnSearch').click(function () {
        //这里以搜索为例
        tableIns.reload({
            where: { //设定异步数据接口的额外参数，任意设
                userName:$('[name="userName"]').val(),
                email:$('[name="email"]').val(),
                phone:$('[name="phone"]').val()
            }
            ,page: {
                curr: 1 //重新从第 1 页开始
            }
        });
    });

    //触发事件
    //监听头部工具栏 --- 添加用户
    table.on('toolbar(users)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        console.log(obj);
        switch(obj.event){
            case 'add':
                openAddOrUpdateDialog();//打开添加或修改页面
                break;
            case 'del':
                detelebatch(checkStatus.data);//打开批量删除用户页面
                break;
        };
    });

    //触发事件
    //监听行工具栏 --- 修改用户
    table.on('tool(users)', function(obj){
        console.log(obj);
        switch(obj.event){
            case 'edit':
                openAddOrUpdateDialog(obj.data.id);//打开添加或修改页面
                break;
            case 'del':
                //询问是否要删除这条数据
                layer.confirm("确定要删除这条记录吗？",{icon: 3, title:"营销机会数据管理"},function (index) {
                    //关闭弹出框
                    layer.close(index);
                    //发送ajax请求 删除用户
                    $.ajax({
                        type:'post',
                        url:ctx+'/user/deleteBatch',
                        data:{
                            ids:obj.data.id
                        },dataType:'json',
                        success:function (data) {
                            if (data.code == 200) {
                                // 加载表格
                                tableIns.reload();
                            } else {
                                layer.msg(result.msg, {icon: 5});
                            }
                        }
                    });
                })
                break;
        };
    });

    //打开批量删除用户的页面
    function detelebatch(data) {
        if (data.length==0){
            layer.msg("请选择要删除的用户!");
            return;
        }
        //向用户确认删除行为
        layer.confirm("您确定要删除选中的记录吗？",{
            btn:["确认","取消"],
        },function (index) {
            //关闭弹出框
            layer.close(index);
            //拼接后台需要的数组 ids=1&ids=2&...
            var str = 'ids='
            for (var i=0;i<data.length;i++){
                if(i < data.length - 1){
                    str += data[i].id + '&ids=';
                }else {
                    str += data[i].id;
                }
            }
            console.log(str);

            //向后台发送请求 --- Ajax
            $.ajax({
                type:'post',
                url:ctx+'/user/deleteBatch',
                data:str,
                dataType:'json',
                success:function (data) {
                    if(data.code == 200){
                        //刷新数据表格
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg,{icon:5})
                    }
                }
            });
        });
    }

    //打开添加/修改用户的页面
    function openAddOrUpdateDialog(id) {
        var title="<h2>用户管理-用户添加</h2>";
        var url=ctx+'/user/toAddOrUpdate';
        //若id存在值,则if判断为true,进入条件体内 --- 表示修改操作
        if(id){
            //这里不需要传参 因为此处 前端使用的是form表单 会自动将所有 元素提交到后台<包括隐藏域中的id属性>
            title="<h2>用户管理-用户修改</h2>";
            url+="?id="+id;
        }
        //打开修改添加页面
        layer.open({
            title:title,//页面标题
            type: 2,//iframe弹出框
            content:url,//页面内容
            area:["650px","400px"], //设置宽高
            maxmin:true //可以伸缩页面大小
        });
    }

});
