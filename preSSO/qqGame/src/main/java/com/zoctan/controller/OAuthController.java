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
import java.io.IOException;

/**
 * QQ游戏控制器
 *
 * @author Zoctan
 * @date 2018/07/03
 */
@Controller
public class OAuthController {
    @Value("${qqURL}")
    private String qqURL;
    @Value("${qqMailURL}")
    private String qqMailURL;
    @Value("${db.sub.id}")
    private String subId;
    @Value("${db.sub.secret}")
    private String subSecret;
    @Resource
    private HttpServletRequest request;

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
            // 在 Cookie 中设置 token
            CookieUtil.set(response, "token", token);
            modelMap.addAttribute("token", token);
        }
        return "index";
    }

    /**
     * 主页面
     */
    @GetMapping("/index")
    public String index(final HttpServletResponse response,
                        final ModelMap modelMap) throws IOException {
        // 从 Cookie 中获取 token
        final String token = CookieUtil.get(this.request, "token");
        System.out.println("token => " + token);
        // 没有 token，表示还没登录过
        if (!StringUtils.isEmpty(token)) {
            modelMap.addAttribute("token", token);
            return "index";
        }
        // 跳转到认证服务器
        final String to = String.format("%s/login?" +
                "sub_id=%s" + "&" +
                "sub_secret=%s" + "&" +
                "service=%s", this.qqURL, this.subId, this.subSecret, this.qqMailURL);
        response.sendRedirect(to);
        return null;
    }
}
