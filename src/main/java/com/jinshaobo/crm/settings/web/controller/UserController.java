package com.jinshaobo.crm.settings.web.controller;

import com.jinshaobo.crm.settings.domain.User;
import com.jinshaobo.crm.settings.service.UserService;
import com.jinshaobo.crm.settings.service.impl.UserServiceImpl;
import com.jinshaobo.crm.utils.MD5Util;
import com.jinshaobo.crm.utils.PrintJson;
import com.jinshaobo.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String path = request.getServletPath();

        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if("/settings/user/xxx.do".equals(path)){
            //xxx();
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        //获取用户提交的账号密码
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        loginPwd = MD5Util.getMD5(loginPwd);

        //获取用户的ip地址
        String ip = request.getRemoteAddr();


        //调用service处理数据
        UserService userService = (UserService)ServiceFactory.getService(new UserServiceImpl());

        //若失败，执行catch代码，若成功，将数据以json形式传回浏览器
        try {
            User user = userService.login(loginAct,loginPwd,ip);
            //没有异常表示成功
            //将数据存储到session中
            request.getSession().setAttribute("user",user);
            //返回登陆成功true
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            String msg = e.getMessage();
            System.out.println(msg);
            //若失败，返回登录失败及错误信息，用map封装
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("success",false);
            map.put("msg",msg);
            PrintJson.printJsonObj(response,map);

        }

    }
}
