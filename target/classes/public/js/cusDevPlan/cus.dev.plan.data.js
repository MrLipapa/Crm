layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?sId='+$("[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });

    //触发事件
    //监听头部工具栏---添加计划项
    table.on('toolbar(cusDevPlans)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        console.log(obj);
        switch(obj.event){
            case 'add':
                openAddOrUpdateDialog();//打开添加或修改页面
                break;
            case 'success':
                updateSaleChanceDevResult(2);
                break;
            case 'failed':
                updateSaleChanceDevResult(3);
                break;
        };
    });

    function updateSaleChanceDevResult(devResult) {
        var id=$('[name="id"]').val();
        // 弹出提示框询问用户
        layer.confirm("确认执行当前操作？", {icon:3, title:"计划项维护"}, function
            (index) {
            $.post(ctx + "/sale_chance/updateDevResult", {id:id, devResult:devResult}, function (data) {
                if (data.code == 200) {
                    layer.msg("操作成功！");    // 关闭弹出层
                    layer.closeAll("iframe");   // 刷新父页面
                    parent.location.reload();
                } else {
                    layer.msg(data.msg, {icon:5});
                }
            });
        });
    }

    //触发事件
    //监听行工具栏---修改计划项
    table.on('tool(cusDevPlans)', function(obj){
        console.log(obj);
        switch(obj.event){
            case 'edit':
                openAddOrUpdateDialog(obj.data.id);//打开添加或修改页面
                break;
            case 'del':
                //询问用户是否确认删除 第一个参数 提示信息,第二个参数 题目,第三个参数 当点击确认时的调用函数
                layer.confirm("确定删除当前数据？", {icon:3, title:"开发计划管理"}, function(index) {   // 发送ajax请求
                    //第一个参数 请求路径,第二个参数 发送的参数,第三个参数 请求成功的回调函数
                    $.post(ctx + "/cus_dev_plan/delete", {id:obj.data.id}, function(data) {
                        if (data.code == 200) {
                            layer.msg("操作成功！");     // 重新加载表格
                            tableIns.reload();
                        } else {
                            layer.msg(data.msg, {icon:5});
                        }
                    });
                });
                break;
        };
    });

    /**
     * 打开添加/修改 计划项的对话框
     */
    function openAddOrUpdateDialog(id){
        var title='<h2>计划项管理-添加计划项</h2>';
        var url= ctx +'/cus_dev_plan/toAddOrUpdate?sId='+$('[name="id"]').val(); //jquery获取saleChanceId
        //通过id判断是修改还是添加
        if(id){
            title='<h2>计划项管理-修改计划项</h2>';
            url += '&id='+id;
        }

        //打开layUI iframe
        layui.layer.open({
            type:2,     //打开的页面位iframe框
            title:title,        //页面题目
            content:url,        //页面内容
            area:["500px","300px"],     //页面大小
            maxmin:true     //是否可以伸缩页面大小
        });
    }

});
