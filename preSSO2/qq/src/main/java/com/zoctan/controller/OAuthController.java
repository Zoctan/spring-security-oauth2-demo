package com.zoctan.controller;

import com.zoctan.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;

/**
 * 登录认证控制器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Controller
public class OAuthController {
    @Value("${oauth.username}")
    private String username;
    @Value("${oauth.password}")
    private String password;
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;
    @Resource
    private HttpServletRequest request;
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @GetMapping("/login")
    public String login(@PathParam("service") final String service,
                        final ModelMap map) {
        final HttpSession session = this.request.getSession();
        // 记录下请求的子系统地址
        session.setAttribute("service", service);
        // 页面变量
        map.addAttribute("doLoginURL", "/doLogin");
        map.addAttribute("username", this.username);
        map.addAttribute("password", this.password);
        return "login";
    }

    @PostMapping("/doLogin")
    public ModelAndView doLogin(@PathParam("username") final String username,
                                @PathParam("password") final String password,
                                final ModelMap map,
                                final RedirectAttributes redirectAttributes) {
        final ModelAndView modelAndView = new ModelAndView();
        try {
            // todo
            // 生成jwt
            final Result result = new Result();

            modelAndView.setViewName("index");
            map.addAttribute("token", result.getAccessToken());
            map.addAttribute("getPrincipalURL", "/principal?access_token=" + result.getAccessToken());
        } catch (final HttpClientErrorException e) {
            modelAndView.setViewName("redirect:/login");
            redirectAttributes.addFlashAttribute("alertMsg", "登录失败：" + e.getMessage());
        }
        return modelAndView;
    }
}
