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
import java.io.IOException;
import java.util.UUID;

/**
 * 豆瓣认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${qqURL}")
    private String qqURL;
    @Value("${doubanURL}")
    private String doubanURL;
    @Resource
    private HttpServletRequest request;
    @Resource
    private RestTemplate restTemplate;
    /**
     * 模拟数据库里的数据
     */
    private final static String CLIENT_ID = "douban";
    private final static String CLIENT_SECRET = "douban123";
    /**
     * 防止跨站点请求伪造
     * 该值必须保证不可猜测，并且浏览器保存时要同源保护
     */
    private final static String STATE = UUID.randomUUID().toString();
    /**
     * 请求的授权范围
     */
    private final static String SCOPE = "userAllInfo";

    @GetMapping("/login")
    public String login(final ModelMap map) {
        map.addAttribute("toAuthorizeURL", this.doubanURL + "/toAuthorize");
        return "login";
    }

    /**
     * （A）用户访问豆瓣客户端，豆瓣将用户导向QQ认证服务器，即跳转到QQ登录页面
     */
    @GetMapping("/toAuthorize")
    public void toAuthorize(final HttpServletResponse response) throws IOException {
        final String to = String.format("%s/authorize?" +
                "response_type=code" + "&" +
                "client_id=%s" + "&" +
                "client_secret=%s" + "&" +
                "scope=%s" + "&" +
                "state=%s" + "&" +
                "redirect_uri=%s/index", this.qqURL, CLIENT_ID, CLIENT_SECRET, SCOPE, STATE, this.doubanURL);
        response.sendRedirect(to);
    }

    /**
     * 首页
     */
    @GetMapping("/index")
    public String index(@RequestParam("code") final String code,
                        @RequestParam("state") final String state,
                        @RequestParam("scope") final String scope,
                        final ModelMap map) {
        if (!STATE.equals(state)) {
            map.addAttribute("message", "遭受CSRF攻击");
            return "index";
        }

        if (!SCOPE.equals(scope)) {
            map.addAttribute("message", "授权范围被限制为" + scope);
        }

        // （D）客户端收到授权码，附上"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见
        final String getAccessTokenURL = String.format("%s/token?" +
                "grant_type=authorization_code" + "&" +
                "code=%s" + "&" +
                "redirect_uri=%s/index", this.qqURL, code, this.doubanURL);
        final String accessToken = this.restTemplate.getForObject(getAccessTokenURL, String.class);

        // 通过token请求用户信息
        final String getUserInfoURL = String.format("%s/getUserInfoByToken?" +
                "access_token=%s", this.qqURL, accessToken);
        final String userInfo = this.restTemplate.getForObject(getUserInfoURL, String.class);

        this.request.getSession().setAttribute("userInfo", userInfo);

        map.addAttribute("getUserInfoURL", this.doubanURL + "/getUserInfo");
        return "index";
    }

    @GetMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo() {
        return this.request.getSession().getAttribute("userInfo").toString();
    }
}
