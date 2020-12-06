package com.roc.crm.web.filter;

import com.roc.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            // 不应该被拦截的资源，自动放行请求
            filterChain.doFilter(servletRequest,servletResponse);
        } else { // 其他资源，必须验证有没有登陆过
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if(user != null){
                filterChain.doFilter(servletRequest,servletResponse);
            } else { // 没有登陆过，重定向到登录页
                /*
                    重定向路径怎么写？
                        在实际项目开发中，对于路径的使用，不论操作的是前端还是后端，应该一律使用绝对路径
                        关于转发和重定向的路径的写法如下：
                        转发：
                            使用的是一种特殊的绝对路径的使用方式，这种绝对路径前面不加/项目名，这种路径也称之为内部路径
                            /login.jsp
                        重定向：
                            使用的是传统绝对路径的写法，前面必须以/项目名开头，后面跟具体的资源路径
                            /crm/login.jsp
                    为什么使用重定向，使用转发不行吗？
                        转发之后，路径会停留在老路径上，而不是跳转之后最新资源的路径
                        我们应该在为用户跳转到登录页的同时，将浏览器的地址栏自动设置为当前的登录页的路径
                    ${pageContext.request.contextPath}  /项目名
                 */
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }

    @Override
    public void destroy() {
    }
}
