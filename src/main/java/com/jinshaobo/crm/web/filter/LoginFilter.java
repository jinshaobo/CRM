package com.jinshaobo.crm.web.filter;

import com.jinshaobo.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        //登录页和登录请求不需要拦截
        String path = request.getServletPath();
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)) {
            chain.doFilter(req,resp);
        }else {
            //其他路径需要拦截
            //查看session中是否有user对象，以验证用户是否登录过
            User user = (User) request.getSession().getAttribute("user");
            if(user != null){
                //用户登录过，释放请求
                chain.doFilter(req,resp);
            }else {
                //重定向到登录页面
                //response.sendRedirect("/crm/login.jsp");
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }

    }
}
