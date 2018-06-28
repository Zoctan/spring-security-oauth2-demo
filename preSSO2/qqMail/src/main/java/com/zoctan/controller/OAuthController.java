package com.zoctan.controller;

import com.alibaba.fastjson.JSON;
import com.zoctan.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;

/**
 * QQ邮箱登录控制器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Controller
public class OAuthController {
    @Value("${qqURL}")
    private String qqURL;
    @Value("${oauth.username}")
    private String username;
    @Value("${oauth.password}")
    private String password;
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/login")
    public String login(final ModelMap map) {
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
            final String getAccessTokenURL = String.format("%s/oauth/token?" +
                    "grant_type=password" + "&" +
                    "username=%s" + "&" +
                    "password=%s" +
                    "&client_id=%s" + "&" +
                    "client_secret=%s", this.qqURL, username, password, this.clientId, this.clientSecret);
            final String json = this.restTemplate.postForObject(getAccessTokenURL, null, String.class);
            final Result result = JSON.parseObject(json, Result.class);

            modelAndView.setViewName("index");
            map.addAttribute("token", result.getAccessToken());
            map.addAttribute("getPrincipalURL", this.qqURL + "/principal?access_token=" + result.getAccessToken());
        } catch (final HttpClientErrorException e) {
            modelAndView.setViewName("redirect:/login");
            redirectAttributes.addFlashAttribute("alertMsg", "登录失败：" + e.getMessage());
        }
        return modelAndView;
    }
}
