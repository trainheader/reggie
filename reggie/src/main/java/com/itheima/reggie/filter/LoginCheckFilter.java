package com.itheima.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * @author laijunhan
 */
@WebFilter(urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    public static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/**/*.html",
                "/**/*.css",
                "/**/*.js",
                "/**/*.png",
                "/**/*.jpg",
                "/**/*.ico"
        };


        if(checkPath(urls, uri)){
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否登录 登录了放行 未登录拦截
        if (request.getSession().getAttribute("employeeId") != null){
            // 将操作人的id 放入线程变量中
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employeeId"));
            filterChain.doFilter(request, response);
            return;
        }

        // 判断移动端用户是否登录
        if (request.getSession().getAttribute("user") != null){
            Long userId = (Long)request.getSession().getAttribute("user");
            // 将操作人的id 放入线程变量中
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 判断请求路径是否在urls中
     * @param urls 符合条件的放行路径
     * @param requestUri 请求路径
     * @return true：放行；false：拦截
     */
    public boolean checkPath(String[] urls, String requestUri){
        for (String url : urls) {
            boolean ans = antPathMatcher.match(url, requestUri);
            if(ans) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
