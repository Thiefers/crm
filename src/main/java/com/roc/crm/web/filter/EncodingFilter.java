package com.roc.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        // 过滤POST请求中文参数乱码
        req.setCharacterEncoding("UTF-8");
        // 过滤响应流响应中文乱码
        resp.setContentType("text/html; charset=UTF-8");
        // 将请求放行
        chain.doFilter(req,resp);
    }

}
