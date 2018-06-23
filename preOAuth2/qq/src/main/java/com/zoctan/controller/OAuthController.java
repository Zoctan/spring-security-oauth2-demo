package com.zoctan.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

/**
 * QQ认证控制器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@Controller
public class OAuthController {
    @Value("${server.port}")
    private Integer qqPort;
    @Value("${douban.port}")
    private Integer douBanPort;

    private final static String CLIENT_ID = "douban";
    private final static String CLIENT_SECRET = "douban123";

    private static String getLocalHost() throws UnknownHostException {
        //return InetAddress.getLocalHost().getHostAddress();
        return "10.100.19.211";
    }

    private static String getRedirect_uri() throws UnknownHostException {
        return "http://" + getLocalHost() + ":7001/index";
    }

    /**
     * 验证客户端
     */
    private String validateClient(String clientId,
                                  String clientSecret) {

        // 是否在本第三方授权服务器上注册
        if (!CLIENT_ID.equals(clientId)) {
            return "客户端未进行过授权注册";
        }
        // 验证客户端秘钥
        // 此处当成空不作处理
        /*
        if (!CLIENT_ID.equals(clientId) && !CLIENT_SECRET.equals(clientSecret)) {
            return "客户端不正确";
        }
         */
        return null;
    }

    /**
     * 登录页面
     */
    @GetMapping("authorize")
    public String authorize(@RequestParam("response_type") String responseType,
                            @RequestParam("client_id") String clientId,
                            @RequestParam(value = "client_secret", required = false) String clientSecret,
                            @RequestParam(value = "scope", required = false) String scope,
                            @RequestParam("state") String state,
                            @RequestParam("redirect_uri") String redirectUri,
                            HttpServletRequest request,
                            ModelMap map) {
        if (!"code".equals(responseType)) {
            map.addAttribute("message", "授权码模式获取 Authorization Code 阶段只能使用code");
            return "tip";
        }

        // 验证客户端
        String validateClient = validateClient(clientId, clientSecret);
        if (validateClient != null) {
            map.addAttribute("message", validateClient);
            return "tip";
        }

        // 开启session记录状态
        HttpSession session = request.getSession();
        session.setAttribute("clientId", clientId);
        session.setAttribute("scope", scope);
        session.setAttribute("state", state);
        session.setAttribute("redirectUri", redirectUri);
        return "login";
    }

    /**
     * （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码
     */
    @PostMapping("login")
    public void login(@RequestBody Map<String, Object> map,
                      HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String username = map.get("username").toString();
        String password = map.get("password").toString();
        // 验证用户名密码是否正确
        if (!"qq".equals(username) && !"qq".equals(password)) {
            return;
        }
        HttpSession session = request.getSession();
        String clientId = session.getAttribute("clientId").toString();
        String scope = session.getAttribute("scope").toString();
        String state = session.getAttribute("state").toString();
        String redirectUri = session.getAttribute("redirectUri").toString();
        // 按scope范围授权客户端
        // 如果某些权限不允许，返回实际的scope范围
        // 比如这里只允许客户端获得用户名的权限而不是完整的userinfo
        if (!scope.contains("username")) {
            scope = "username";
        }
        // code 与客户端 ID 和重定向 URI 是一一对应关系
        String code = Base64Utils.encodeToString((clientId + " " + redirectUri).getBytes());
        String to = String.format("%s?code=%s&state=%s&scope=%s", redirectUri, code, state, scope);
        // 此时，用户和认证服务器的操作就结束了，可以移除session
        session.invalidate();
        response.sendRedirect(to);
    }

    /**
     * （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（Access Token）和更新令牌（Refresh Token）
     */
    @RequestMapping("token")
    @ResponseBody
    public String token(@RequestParam("grant_type") String grantType,
                        @RequestParam("code") String code,
                        @RequestParam("redirect_uri") String redirectUri,
                        HttpServletRequest request,
                        ModelMap map) throws IOException {
        String authorization = request.getHeader("Authorization");
        String client = new String(Base64Utils.decodeFromString(authorization.replace("Basic ", "")));
        // 判断client_id、client_secret是否正确
        String clientId = client.split(" ")[0];
        String clientSecret = client.split(" ")[1];

        // 验证客户端
        String validateClient = validateClient(clientId, clientSecret);
        if (validateClient != null) {
            map.addAttribute("message", validateClient);
            return "tip";
        }

        // 返回token
        return "{access_token:yyy," +
                "token_type:bearer," +
                "expires_in:600," +
                "refresh_token:zzz}";
    }

    /**
     * （F）资源服务器确认令牌无误，同意向客户端开放资源。。
     */
    @RequestMapping("getUserinfoByToken")
    @ResponseBody
    public String getUserinfoByToken(String token) throws IOException {
        // 判断token是否正确
        return "{userGuid:j3jlk2jj32li43i," +
                "username:Tom," +
                "mobile:18811412324}";
    }

}
