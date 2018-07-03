package com.zoctan.controller;

import com.zoctan.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * QQ认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${db.user.username}")
    private String username;
    @Value("${db.user.password}")
    private String password;
    @Value("${db.sub.ids}")
    private String subIds;
    @Value("${db.sub.secrets}")
    private String subSecrets;
    @Resource
    private HttpServletRequest request;

    /**
     * 验证子系统
     */
    private String validateClient(final String subId,
                                  final String subSecret) {
        final String[] subIdList = this.subIds.split(",");
        final String[] subSecretList = this.subSecrets.split(",");
        for (int i = 0; i < subIdList.length; i++) {
            // 子系统
            if (subIdList[i].equals(subId)) {
                // 验证秘钥
                if (subSecretList[i].equals(subSecret)) {
                    return null;
                }
                return "子系统信息不正确";
            }
        }
        return "非QQ子系统";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login(@RequestParam("sub_id") final String subId,
                        @RequestParam("sub_secret") final String subSecret,
                        @RequestParam("service") final String service,
                        final ModelMap map,
                        final HttpServletResponse response) throws IOException {
        // 验证子系统
        final String validate = this.validateClient(subId, subSecret);
        if (validate != null) {
            response.sendRedirect(service + "?error=" + validate);
            return null;
        }
        System.out.println("子系统请求 => " + subId);

        // 127.0.0.1:8000/login?service=http://127.0.0.1:7000/index
        final HttpSession session = this.request.getSession();
        // 如果之前登录过
        final Object isLogin = session.getAttribute("isLogin");
        if (isLogin != null) {
            // 把 service 记录到 redis（为了之后单点注销）
            // 这里为了演示暂时不记录

            // 从 Cookie 中获取 token
            final String token = CookieUtil.get(this.request, "token");
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

    /**
     * 登录
     */
    @PostMapping("/doLogin")
    public ModelAndView doLogin(@RequestParam("username") final String username,
                                @RequestParam("password") final String password,
                                final HttpServletResponse response) throws IOException {
        final ModelAndView modelAndView = new ModelAndView();
        if (!this.username.equals(username) || !this.password.equals(password)) {
            modelAndView.setViewName("/login");
            modelAndView.addObject("alertMsg", "用户名或密码错误");
            return modelAndView;
        }

        // 生成 token
        final String token = UUID.randomUUID().toString();
        // 在 Cookie 中设置 token
        CookieUtil.set(response, "token", token);
        // token 缓存到 redis（方便验证过期）
        // 同样这里暂不缓存

        final HttpSession session = this.request.getSession();
        // 设置用户已登录
        session.setAttribute("isLogin", true);
        final String service = session.getAttribute("service").toString();
        response.sendRedirect(service + "?token=" + token);
        return null;
    }
}
