package com.jinshaobo.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        //处理post请求乱码
        req.setCharacterEncoding("UTF-8");
        //处理响应流的乱码问题
        resp.setContentType("text/html;charset=utf-8");
        //将请求放行
        chain.doFilter(req,resp);

    }
}
