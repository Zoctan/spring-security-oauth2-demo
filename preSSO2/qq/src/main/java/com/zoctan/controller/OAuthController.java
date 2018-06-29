package com.zoctan.controller;

import com.zoctan.utils.JWTUtil;
import com.zoctan.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.io.IOException;

/**
 * 登录认证控制器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Controller
public class OAuthController {
    @Value("${db.user.username}")
    private String username;
    @Value("${db.user.password}")
    private String password;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private HttpServletRequest request;

    //  @Resource
    // private ClientDetailsService clientDetailsService;

    @GetMapping("/login")
    public String login(@PathParam("service") final String service,
                        final ModelMap map,
                        final HttpServletResponse response) throws IOException {
        // 127.0.0.1:8000/login?service=http://baidu.com
        final HttpSession session = this.request.getSession();
        // 如果之前登录过
        final Object isLogin = session.getAttribute("isLogin");
        if (isLogin != null) {
            // 从 redis 中获取 token
            final OAuth2AccessToken token = (OAuth2AccessToken) this.redisUtil.get(session.getId());
            // 带上 token 重定向回去
            response.sendRedirect(service + "?token=" + token);
            return null;
        }
        // 未登录，按正常登录进行
        // 记录下请求的子系统地址
        session.setAttribute("service", service);
        // 页面变量
        map.addAttribute("username", this.username);
        map.addAttribute("password", this.password);
        return "login";
    }

    @PostMapping("/doLogin")
    public ModelAndView doLogin(@PathParam("username") final String username,
                                @PathParam("password") final String password,
                                final HttpServletResponse response) throws IOException {
        final ModelAndView modelAndView = new ModelAndView();
        if (!this.username.equals(username) || !this.password.equals(password)) {
            modelAndView.setViewName("/login");
            modelAndView.addObject("alertMsg", "用户名或密码错误");
            return modelAndView;
        }
        final HttpSession session = this.request.getSession();
        // 设置用户已登录
        session.setAttribute("isLogin", true);
        final String service = session.getAttribute("service").toString();

        // 生成 JWT
        final OAuth2AccessToken token = this.jwtUtil.generateToken(username, password);
        // 缓存到 redis
        this.redisUtil.set(session.getId(), token);
        response.sendRedirect(service + "?token=" + token);
        return null;
    }
}
