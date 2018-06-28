package com.zoctan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @RequestMapping("/login_page")
    public String loginPage(final Model model) {
        model.addAttribute("username", this.username);
        model.addAttribute("password", this.password);
        return "login_page";
    }
}
