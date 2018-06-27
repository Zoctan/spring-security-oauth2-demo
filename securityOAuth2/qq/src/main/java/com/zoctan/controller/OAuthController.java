package com.zoctan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * QQ认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${oauth.username}")
    private String username;
    @Value("${oauth.password}")
    private String password;

    @RequestMapping("/login")
    public String login(@RequestParam(value = "error", required = false) final String error,
                        final Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        model.addAttribute("loginURL", "/login");
        model.addAttribute("username", this.username);
        model.addAttribute("password", this.password);
        return "login";
    }
}
