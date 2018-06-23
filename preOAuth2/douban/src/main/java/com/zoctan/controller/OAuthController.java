package com.zoctan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * 豆瓣认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${server.port}")
    private Integer douBanPort;
    @Value("${qq.port}")
    private Integer qqPort;
    @Resource
    private RestTemplate restTemplate;

    private static String getLocalHost() throws UnknownHostException {
        //return InetAddress.getLocalHost().getHostAddress();
        return "10.100.19.211";
    }

    /**
     * （A）用户访问豆瓣客户端，豆瓣将用户导向QQ认证服务器，即跳转到QQ登录页面
     *
     * @param response 响应
     * @throws IOException IO异常
     */
    @GetMapping("toQQAuthorize")
    public void toQQAuthorize(HttpServletResponse response) throws IOException {
        String to = String.format("http://localhost:%d/authorize?" +
                "response_type=code" + "&" +
                "client_id=abc123" + "&" +
                "scope=userinfo" + "&" +
                "state=test" + "&" +
                "redirect_uri=http://localhost:%d/index", qqPort, douBanPort);
        response.sendRedirect(to);
    }

    @RequestMapping("index")
    public String index(String code, HttpServletRequest request) throws Exception {
        /**
         * （D）客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。
         */
        String accessToken = restTemplate.getForObject("http://" + getLocalHost() + ":7000/token?" +
                "grant_type=authorization_code&" +
                "code=xxx&" +
                "redirect_uri=http://" + getLocalHost() + ":7001/index", String.class);

        /**
         * 发起通过token换用户信息的请求
         */
        String username = restTemplate.getForObject("http://" + getLocalHost() + ":7000/getUserinfoByToken?" +
                "access_token=yyy", String.class);

        request.getSession().setAttribute("username", username);

        return "index";
    }


    @GetMapping("getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpServletRequest request) throws Exception {
        Object username = request.getSession().getAttribute("username");
        return "Tom 18811311416";
    }

}
