package com.zoctan.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域过滤器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Component
public class CorsFilter implements Filter {
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
