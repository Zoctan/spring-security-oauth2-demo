package com.zoctan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

/**
 * 银行认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${qqURL}")
    private String qqURL;
    @Value("${bankURL}")
    private String bankURL;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RestTemplate restTemplate;
    /**
     * 模拟数据库里的数据
     */
    private final static String CLIENT_ID = "bank";
    private final static String CLIENT_SECRET = "bank123";
    /**
     * 请求的授权范围
     */
    private final static String SCOPE = "userAllInfo";

    @GetMapping("/login")
    public String login(final ModelMap map) {
        map.addAttribute("toAuthorizeURL", this.bankURL + "/toAuthorize");
        return "login";
    }

    /**
     * （A）用户访问银行客户端中的第三方登录，银行将用户导向QQ认证服务器，即跳转到QQ登录页面
     */
    @GetMapping("/toAuthorize")
    public void toAuthorize(final HttpServletResponse response) throws IOException {
        // 获取 session
        final HttpSession session = this.request.getSession();
        // state 防止银行客户端被 CSRF 攻击
        // 这里必须满足几个特性：不可预测性、关联性、唯一性、时效性
        final String state = UUID.randomUUID().toString();
        // state 保存下来，以便稍后授权成功重定向回来时验证
        session.setAttribute("state", state);
        final String to = String.format("%s/authorize?" +
                "response_type=code" + "&" +
                "client_id=%s" + "&" +
                "client_secret=%s" + "&" +
                "scope=%s" + "&" +
                "state=%s" + "&" +
                "redirect_uri=%s/index", this.qqURL, CLIENT_ID, CLIENT_SECRET, SCOPE, state, this.bankURL);
        response.sendRedirect(to);
    }

    /**
     * 成功获取授权码的重定向页面
     * CSRF 攻击也是在这控制器发生
     */
    @GetMapping("/index")
    public String index(@RequestParam("code") final String code,
                        @RequestParam("state") final String state,
                        @RequestParam("scope") final String scope,
                        final ModelMap map) {
        final HttpSession session = this.request.getSession();
        if (session == null) {
            map.addAttribute("alertMsg", "请先登录");
            return "index";
        }
        // 验证 state，防止受到 CSRF 攻击
        // 如果这一步被攻击，那么 session 会话里不存在 state，因为用户没有发出过授权请求
        final Object stateFromSession = session.getAttribute("state");
        if (stateFromSession == null || !stateFromSession.toString().equals(state)) {
            map.addAttribute("alertMsg", "受到 CSRF 攻击");
            return "index";
        }
        // 一旦被使用则立即失效
        session.setAttribute("state", null);
        map.addAttribute("message", "成功获取授权码");

        // （D）客户端收到授权码，附上"重定向URI"，向认证服务器申请令牌
        // 这一步是在客户端的后端服务器上完成的，对用户不可见
        final String getAccessTokenURL = String.format("%s/token?" +
                "grant_type=authorization_code" + "&" +
                "code=%s" + "&" +
                "redirect_uri=%s/index", this.qqURL, code, this.bankURL);
        final String accessToken = this.restTemplate.getForObject(getAccessTokenURL, String.class);

        // 通过 token 请求用户信息
        final String getUserInfoURL = String.format("%s/getUserInfoByToken?" +
                "access_token=%s", this.qqURL, accessToken);
        final String userInfo = this.restTemplate.getForObject(getUserInfoURL, String.class);

        this.request.getSession().setAttribute("userInfo", userInfo);

        map.addAttribute("getUserInfoURL", this.bankURL + "/getUserInfo");
        return "index";
    }

    @GetMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo() {
        return this.request.getSession().getAttribute("userInfo").toString();
    }
}
