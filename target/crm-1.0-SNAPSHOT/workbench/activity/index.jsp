<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){

		//日历插件
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//为创建按钮绑定事件，打开添加操作的模态窗口
		$("#addBtn").click(function (){
			$.ajax({
				url :"workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function (data){
                    //data{userList}
					var html = "";
					$.each(data,function (i,n){
						html += "<option value='"+n.id+"'>"+n.name+"</option>";
					})
					$("#create-owner").html(html);

					//使得默认所有者为当前用户的名字，需要从session中取，在js中使用EL表达式必须在“”内写
					var id = "${user.id}";
					$("#create-owner").val(id);

					$("#createActivityModal").modal("show");//将该窗口变为模态窗口，show表示打开模态窗口，hide关闭模态窗口
				}
			})
		})

		//为保存按钮绑定事件，执行添加操作
		$("#saveBtn").click(function (){
			$.ajax({
				url: "workbench/activity/save.do",
				data: {
					"owner": $.trim($("#create-owner").val()),
					"name": $.trim($("#create-name").val()),
					"startDate": $.trim($("#create-startDate").val()),
					"endDate": $.trim($("#create-endDate").val()),
                    "cost": $.trim($("#create-cost").val()),
					"description": $.trim($("#create-description").val()),
				},
				type: "post",
				dataType: "json",
				success: function (data){
					if(data.success){
						//保存成功，刷新市场活动信息列表，局部刷新
						//pageList(1,5);
						//保存成功后维持在第一页
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//清空添加操作的模态窗口的数据
						//利用form表单的reset，jquery对象没有reset方法，只能转换成dom对象
						$("#activityAddForm")[0].reset();

						//关闭模态窗口
						$("#createActivityModal").modal("hide");

					}else {
						//失败
						alert("添加市场活动失败");
					}
				}
			})
		})
		//页面加载完毕后，触发一个分页方法
        pageList(1,5);//当前页码第一页，每页显示5条记录
		//为查询按钮绑定事件，触发pageList方法
		$("#searchBtn").click(function (){

			//点击查询按钮时，我们应该将搜索框中的信息保存起来，保存到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			//分页查询
			//pageList(1,5);
			//查询后停留在第一页
			pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

		})

		//为全选复选框绑定事件
		$("#qx").click(function (){
			$("input[name=xz]").prop("checked",this.checked)//当前单选框的选中状态与全选框的选中状态一致
		})

		//为单选框绑定事件，
		//下面这个是不行的，动态生成的标签不能用普通绑定事件来处理
		//$("input[name=xz]").click()
		//需要用$(需要绑定元素的有效外层元素).on(绑定的事件，绑定的对象，回调函数)
		$("#activityBody").on("click",$("input[name=xz]"),function (){
			$("#qx").prop("checked",$("input[name=xz]").length == $("input[name=xz]:checked").length)
		})

		//为删除按钮绑定事件
		$("#deleteBtn").click(function (){
			//找到所有选中的复选框的jquery对象,就是dom对象数组
			var $xz = $("input[name=xz]:checked");
			if($xz.length == 0){
				alert("请选择您要删除的市场活动！")
			}else {
				if(confirm("你确定要删除这条记录吗？")){
					//找到所有选中复选框的value值，拼接到url后面id=value
					var param = "";
					for (var i = 0; i < $xz.length; i++) {
						param += "id="+$($xz[i]).val();
						if(i < $xz.length - 1){
							param += "&";
						}
					}
					$.ajax({
						url: "workbench/activity/delete.do",
						data: param,
						type: "post",
						dataType: "json",
						success: function (data){
							//data{success:true/false}
							if(data.success){
								//刷新市场活动列表
								//pageList(1,5);
								//删除后维持在当前页
								pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
										,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}else {
								alert("删除市场活动失败")
							}
						}
					})
				}
			}
		})

		//为修改按钮绑定事件
		$("#editBtn").click(function (){

			//找到选中的复选框的jquery对象
			$xz = $("input[name=xz]:checked");
			if($xz.length == 0){
				alert("请选择您要修改的市场活动！")
			}else if($xz.length > 1){
				alert("每次只能修改一条市场活动信息")
			}else {
				var id = $xz.val();
				$.ajax({
					url : "workbench/activity/getUserListAndActivity.do",
					data : {
						"id" : id
					},
					type : "post",
					dataType : "json",
					success : function (data){
						//data{userList,activity}
                        //处理userList
						var html = "";
                        var defaultUser;
						$.each(data.userList,function (i,n){
							html += "<option value='"+n.id+"'>"+n.name+"</option>";
                            if(n.id == data.activity.owner){
                                 defaultUser = n;
                            }
						})
						$("#edit-owner").html(html);
                        $("#edit-owner").val(defaultUser.id);



						//处理activity
						$("#edit-id").val(data.activity.id);//id很重要，表示这是哪一条记录，放在隐藏域中
						$("#edit-name").val(data.activity.name);
						$("#edit-startDate").val(data.activity.startDate);
						$("#edit-endDate").val(data.activity.endDate); 
						$("#edit-cost").val(data.activity.cost);
						$("#edit-description").val(data.activity.description);

						//所有值修改之后，打开模态窗口
						$("#editActivityModal").modal("show");

					}
				})
			}
		})

        //为更新按钮绑定事件，执行市场活动的修改操作
        $("#updateBtn").click(function (){
            //里面直接copy保存按钮的操作即可，只做稍微修改
            $.ajax({
                url: "workbench/activity/update.do",
                data: {
                    "id": $.trim($("#edit-id").val()),
                    "owner": $.trim($("#edit-owner").val()),
                    "name": $.trim($("#edit-name").val()),
                    "startDate": $.trim($("#edit-startDate").val()),
                    "endDate": $.trim($("#edit-endDate").val()),
                    "cost": $.trim($("#edit-cost").val()),
                    "description": $.trim($("#edit-description").val()),
                },
                type: "post",
                dataType: "json",
                success: function (data){
                    if(data.success){
                        //修改成功，刷新市场活动信息列表，局部刷新
                        //pageList(1,5);
						//bootStrap提供的方法，更新完哪一页的数据,还停留在哪一页
						//第一个参数，维持在当前页；第二个参数，每页维持记录条数
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

						//关闭模态窗口
                        $("#editActivityModal").modal("hide");

                    }else {
                        //失败
                        alert("修改市场活动失败");
                    }
                }
            })
        })

		
	});
	//什么时候调用pageList方法
	//1.点击左侧菜单栏市场活动，需要局部刷新市场活动信息列表
	//2.点击创建、修改、删除，需要局部刷新市场活动信息列表
	//3.点击查询，需要需要局部刷新市场活动信息列表
	//4.点击分页组件，需要需要局部刷新市场活动信息列表
    function pageList(pageNo,pageSize){
		//每次刷新市场活动列表前，清除全选复选框的√
		$("#qx").prop("checked",false);

		//查询前将隐藏域中保存的信息取出来，重新赋予到搜索框中
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url: "workbench/activity/pageList.do",
			data: {
				//分页查询的数据
				"pageNo": pageNo,
				"pageSize": pageSize,
				//条件查询传输的数据
				"name": $.trim($("#search-name").val()),
				"owner": $.trim($("#search-owner").val()),
				"startDate": $.trim($("#search-startDate").val()),
				"endDate": $.trim($("#search-endDate").val())
			},
			type: "get",
			dataType: "json",
			success: function (data){
				//获得市场活动信息列表和总记录条数
				//data{"total":100,"activityList":[{activity1},{activity2}]}
				var html = "";
				$.each(data.dataList,function (i,n){
					//里面用双引号，外边就用单引号
					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';//全局刷新走传统请求
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';
				})

				$("#activityBody").html(html);

				//计算总页数
				var totalPages = data.total % pageSize == 0 ? data.total / pageSize : parseInt(data.total / pageSize) + 1;

				//数据处理完毕之后，结合分页查询，对前端展现分页信息
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});


			}
		})

    }
	
</script>
</head>
<body>

	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"><%--很重要，表示修改的哪一条记录--%>
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">
<%--								  <option>zhangsan</option>--%>
<%--								  <option>lisi</option>--%>
<%--								  <option>wangwu</option>--%>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label" >名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx" /></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">--%>
<%--							<td><input type="checkbox" /></td>--%>
<%--							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--							<td>2020-10-10</td>--%>
<%--							<td>2020-10-20</td>--%>
<%--						</tr>--%>
<%--                        <tr class="active">--%>
<%--                            <td><input type="checkbox" /></td>--%>
<%--                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>--%>
<%--                            <td>zhangsan</td>--%>
<%--                            <td>2020-10-10</td>--%>
<%--                            <td>2020-10-20</td>--%>
<%--                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage">

				</div>

<%--				<div>--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>--%>
<%--				</div>--%>
<%--				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>--%>
<%--					<div class="btn-group">--%>
<%--						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">--%>
<%--							10--%>
<%--							<span class="caret"></span>--%>
<%--						</button>--%>
<%--						<ul class="dropdown-menu" role="menu">--%>
<%--							<li><a href="#">20</a></li>--%>
<%--							<li><a href="#">30</a></li>--%>
<%--						</ul>--%>
<%--					</div>--%>
<%--					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>--%>
<%--				</div>--%>
<%--				<div style="position: relative;top: -88px; left: 285px;">--%>
<%--					<nav>--%>
<%--						<ul class="pagination">--%>
<%--							<li class="disabled"><a href="#">首页</a></li>--%>
<%--							<li class="disabled"><a href="#">上一页</a></li>--%>
<%--							<li class="active"><a href="#">1</a></li>--%>
<%--							<li><a href="#">2</a></li>--%>
<%--							<li><a href="#">3</a></li>--%>
<%--							<li><a href="#">4</a></li>--%>
<%--							<li><a href="#">5</a></li>--%>
<%--							<li><a href="#">下一页</a></li>--%>
<%--							<li class="disabled"><a href="#">末页</a></li>--%>
<%--						</ul>--%>
<%--					</nav>--%>
<%--				</div>--%>
			</div>
			
		</div>
		
	</div>
</body>
</html>