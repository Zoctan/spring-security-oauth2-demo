package com.zoctan.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 * 解决认证服务器重定向时的跨域问题
 *
 * @author Zoctan
 * @date 2018/06/24
 */
@Component
public class CorsFilter implements Filter {
    @Value("${qqURL}")
    private String qqURL;

    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", this.qqURL);
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

        filterChain.doFilter(request, response);
    }

    public void destroy() {
        
    }
}