package com.zoctan.controller;

import com.zoctan.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * QQ邮箱控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Resource
    private HttpServletRequest request;
    @Value("${token.cookie.name}")
    private String tokenCookieName;

    /**
     * 回调
     */
    @GetMapping("/")
    public String callback(@RequestParam(value = "token", required = false) final String token,
                           @RequestParam(value = "error", required = false) final String error,
                           final HttpServletResponse response,
                           final ModelMap modelMap) {
        if (!StringUtils.isEmpty(error)) {
            modelMap.addAttribute("error", error);
        }
        if (!StringUtils.isEmpty(token)
                && StringUtils.isEmpty(error)) {
            System.out.println("接受认证服务器的回调");
            final HttpSession session = this.request.getSession();
            // 设置用户已登录
            session.setAttribute("isLogin", true);
            // 在 Cookie 中设置 token
            CookieUtil.set(response, this.tokenCookieName, token);
            modelMap.addAttribute(this.tokenCookieName, token);
        }
        return "index";
    }

    /**
     * 主页面
     */
    @GetMapping("/index")
    public String index(final ModelMap modelMap) {
        final String token = CookieUtil.get(this.request, this.tokenCookieName);
        modelMap.addAttribute(this.tokenCookieName, token);
        return "index";
    }
}
