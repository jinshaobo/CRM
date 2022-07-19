<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
//http://localhost:8080/crm
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script>
		$(function (){
			if(window.top != window){
				window.top.location = window.location;
			}
			//页面加载完毕后将文本框中的内容清空
			$("#loginAct").val("jinshaobo");
			$("#loginPwd").val("123");
			//页面加载完毕后，用户登录框获得焦点
			$("#loginAct").focus()
			//为登录按钮绑定点击事件
			$("#submitBtn").click(function (){
				//alert("执行登录操作"),
				login();
			})
			//为当前窗口绑定敲键盘事件
			//event是键盘中的键
			$(window).keydown(function (event){
				//alert(event.keyCode);
				if(event.keyCode == 13){
					//alert("执行登录操作");操作重复，封装成方法
					login();
				}
			})
		})
		//自定义的方法写在$(function(){})的外面
		function login(){
			//alert("执行登录操作");
			//验证账号密码不能为空，需要先获得账号密码
			var loginAct = $.trim($("#loginAct").val());//去掉前后空格
			var loginPwd = $.trim($("#loginPwd").val());//去掉前后空格
			if(loginAct == "" || loginPwd == ""){
				$("#msg").html("账号密码不能为空！");
				//如果账号密码为空，终止该方法，不再往下执行；
				return false;
			}
			//如果不为空，验证是否正确，错误显示异常信息，正确跳转到workbench，所以需要ajax
			$.ajax({
				url : "settings/user/login.do",
				data : {
					"loginAct" : loginAct,
					"loginPwd" : loginPwd
				},
				type : "post",
				dataType : "json",
				success : function (data){
					//data{"success" : true/false,"msg" : "异常信息"}
					if(data.success){
						document.location.href = "workbench/index.jsp";
					}else {
						$("#msg").html(data.msg);
					}
				}

			})

		}

	</script>
</head>

<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2022.7.1&nbsp;淘气波波</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg" style="color: red"></span>
						
					</div>
					<%--在form表单中的按钮，默认是submit按钮，我们想点击按钮按照我们的代码操作，将submit改为button--%>
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>