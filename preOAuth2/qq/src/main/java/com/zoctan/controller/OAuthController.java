package com.zoctan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalTime;
import java.util.UUID;

/**
 * QQ认证控制器
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
    /**
     * 模拟数据库里的数据
     */
    private final static String USERNAME = "qq";
    private final static String PASSWORD = "qq123";
    private final static String CLIENT_ID = "bank";
    private final static String CLIENT_SECRET = "bank123";
    /**
     * 所有可允许的权限
     */
    private final static String[] ALL_SCOPE = {"userAllInfo", "userPartInfo"};
    /**
     * 只允许的授权范围
     */
    private final static String SCOPE = ALL_SCOPE[1];

    /**
     * 验证客户端
     */
    private String validateClient(final String clientId,
                                  final String clientSecret) {
        // 是否在本认证服务器上注册
        if (!CLIENT_ID.equals(clientId)) {
            return "客户端未进行过授权注册";
        }
        // 验证客户端秘钥
        if (!CLIENT_ID.equals(clientId) && !CLIENT_SECRET.equals(clientSecret)) {
            return "客户端不正确";
        }
        return null;
    }

    /**
     * 登录页面
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam("response_type") final String responseType,
                            @RequestParam("client_id") final String clientId,
                            @RequestParam(value = "client_secret", required = false) final String clientSecret,
                            @RequestParam(value = "scope", required = false) final String scope,
                            @RequestParam("state") final String state,
                            @RequestParam("redirect_uri") final String redirectUri,
                            final ModelMap map) {
        if (!"code".equals(responseType)) {
            map.addAttribute("alertMsg", "授权码模式的获取 Authorization Code 阶段只能使用 code");
            return "login";
        }

        // 验证客户端
        final String validateClient = validateClient(clientId, clientSecret);
        if (validateClient != null) {
            map.addAttribute("alertMsg", validateClient);
            return "login";
        }

        // 开启session记录状态
        final HttpSession session = this.request.getSession();
        session.setAttribute("clientId", clientId);
        session.setAttribute("scope", scope);
        session.setAttribute("state", state);
        session.setAttribute("redirectUri", redirectUri);

        map.addAttribute("loginURL", this.qqURL + "/login");
        map.addAttribute("username", USERNAME);
        map.addAttribute("password", PASSWORD);
        return "login";
    }

    /**
     * （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（Redirection URI），同时附上一个授权码
     */
    @PostMapping("/login")
    public void login(@RequestParam("username") final String username,
                      @RequestParam("password") final String password,
                      final HttpServletResponse response) throws IOException {
        // 验证用户名密码是否正确
        if (!USERNAME.equals(username) && !PASSWORD.equals(password)) {
            return;
        }
        // 获取 session
        final HttpSession session = this.request.getSession();
        final String clientId = session.getAttribute("clientId").toString();
        final String state = session.getAttribute("state").toString();
        final String redirectUri = session.getAttribute("redirectUri").toString();
        String scope = session.getAttribute("scope").toString();
        // 按 scope 范围授权客户端
        // 如果某些权限不允许，返回实际的 scope 范围
        // 比如这里只允许客户端获得部分用户名信息 userPartInfo 的权限而不是完整信息 userAllInfo
        if (!scope.contains(SCOPE)) {
            scope = SCOPE;
        }
        // code 与客户端 ID 和重定向 URI 是一一对应关系
        // 注意过期时间为10分钟后
        final String expiration = LocalTime.now().plusMinutes(10).toString();
        final String code = Base64Utils.encodeToString((clientId + " " + redirectUri + " " + expiration).getBytes());
        final String to = String.format("%s?" +
                "code=%s" + "&" +
                "state=%s" + "&" +
                "scope=%s", redirectUri, code, state, scope);
        // 此时，用户和认证服务器之间的操作就结束了，可以移除 session
        session.invalidate();
        response.sendRedirect(to);
    }

    /**
     * （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（Access Token）和更新令牌（Refresh Token）
     */
    @PostMapping("/token")
    @ResponseBody
    public String token(@RequestParam("grant_type") final String grantType,
                        @RequestParam("code") final String code,
                        @RequestParam("redirect_uri") final String redirectUri,
                        final ModelMap map) {
        if (!"authorization_code".equals(grantType)) {
            map.addAttribute("alertMsg", "授权码模式的通过 Authorization Code 获取 Access Token 阶段只能使用 authorization_code");
            return "login";
        }
        // 检查 code 是否使用过
        // 这里需要先缓存 code 才能检查，为了方便略过该检查

        final String decode = new String(Base64Utils.decodeFromString(code));

        // 为了方便验证暂时不用
        //--------- 请求头
        // 验证客户端 client_id、client_secret 是否正确
        /*
        final String authorization = this.request.getHeader("Authorization");
        final String client = new String(Base64Utils.decodeFromString(authorization.replace("Basic ", "")));
        final String clientId = client.split(":")[0];
        final String clientSecret = client.split(":")[1];
        final String validateClient = validateClient(clientId, clientSecret);
        if (validateClient != null) {
            map.addAttribute("alertMsg", validateClient);
            return "login";
        }
        //--------- 请求参数
        // 验证客户端
        final String clientIdFromCode = decode.split(" ")[0];
        if (!clientId.equals(clientIdFromCode)) {
            map.addAttribute("alertMsg", "客户端不一致");
            return "login";
        }
        */

        // 重定向 URL 不一致
        final String redirectUriFromCode = decode.split(" ")[1];
        if (!redirectUri.equals(redirectUriFromCode)) {
            map.addAttribute("alertMsg", "重定向 URL 不一致");
            System.out.println("重定向 URL 不一致");
            return "login";
        }

        // 如果当前时间不在过期时间之前
        final String expirationFromCode = decode.split(" ")[2];
        final LocalTime expiration = LocalTime.parse(expirationFromCode);
        if (!LocalTime.now().isBefore(expiration)) {
            map.addAttribute("alertMsg", "token 已过期");
            System.out.println("token 已过期");
            return "login";
        }

        // access_token 可以使用 jwt，做到无状态验证用户角色
        // refresh_token 应该和
        // 这里只是演示，所以随便弄了个 token
        return String.format("{" +
                "access_token: %s," +
                "token_type: bearer," +
                "expires_in: %d," +
                "refresh_token: %s," +
                "scope: %s" +
                "}", UUID.randomUUID().toString(), 3600, UUID.randomUUID().toString(), SCOPE);
    }

    /**
     * （F）资源服务器确认令牌无误，同意向客户端开放资源
     */
    @GetMapping("/getUserInfoByToken")
    @ResponseBody
    public String getUserInfoByToken(@RequestParam("access_token") final String accessToken) {
        // 判断 token 是否正确
        // 这里暂不考虑
        return "{" +
                "username: zoctan," +
                "mobile: 12345678901," +
                "email: 123@qq.com" +
                "}";
    }

}
