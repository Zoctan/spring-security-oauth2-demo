package com.zoctan.filter;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录过滤器
 * 将未登录的用户导向认证中心
 * 因为只有一个index控制器，所以只对其过滤
 * 注意还要在启动程序加上@ServletComponentScan
 *
 * @author Zoctan
 * @date 2018/07/04
 */
@WebFilter(urlPatterns = "/index")
public class LoginFilter implements Filter {
    @Value("${qqURL}")
    private String qqURL;
    @Value("${localhostURL}")
    private String localhostURL;
    @Value("${db.sub.id}")
    private String subId;
    @Value("${db.sub.secret}")
    private String subSecret;
    @Value("${token.cookie.name}")
    private String tokenCookieName;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final HttpSession session = request.getSession();
        // 用户登录状态
        final Object isLogin = session.getAttribute("isLogin");
        // 已登录，放行
        if (isLogin != null) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("用户未登录，重定向至认证服务器");
        // 未登录，跳转到认证服务器
        final String to = String.format("%s/login?" +
                "sub_id=%s" + "&" +
                "sub_secret=%s" + "&" +
                "service=%s", this.qqURL, this.subId, this.subSecret, this.localhostURL);
        response.sendRedirect(to);
    }

    @Override
    public void destroy() {

    }
}
