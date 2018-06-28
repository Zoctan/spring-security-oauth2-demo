package com.zoctan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务配置
 *
 * @author Zoctan
 * @date 2018/06/27
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private UserDetailsService userDetailsService;

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                // 如果使用 JWT，允许通过 url：/oauth/token_key 暴露公钥
                .tokenKeyAccess("permitAll()")
                // 允许通过 url：/oauth/check_token 检查 token
                .checkTokenAccess("isAuthenticated()")
                // 允许客户端使用表单请求 token
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        //----授权码模式
        // 请求授权码
        // localhost:8000/oauth/authorize?client_id=resource&response_type=code&redirect_uri=http://baidu.com

        // 通过授权码{}获得 token
        // curl -i -d "code={}&grant_type=authorization_code&redirect_uri=http://baidu.com" -X POST -H "Content-Type: application/x-www-form-urlencoded" "http://resource:resource123@localhost:8000/oauth/token"

        // 访问资源
        // curl -i -H "Authorization: Bearer {}" -X GET http://localhost:8000/image
        // 或者
        // curl -i -X GET http://localhost:8000/image\?access_token={}

        //----密码模式
        // curl -i -X POST -d "username=user&password=123456&grant_type=password&client_id=resource&client_secret=resource123" http://localhost:8000/oauth/token

        // 客户端信息保存在内存
        clients.inMemory()
                .withClient("resource")
                .secret("resource123")
                .redirectUris("http://baidu.com")
                .resourceIds("image")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read")
                .authorities("client")
                // token 有效期1天
                .accessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1))
                // refreshToken 有效期3天
                .refreshTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(3));
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(this.authenticationManager)
                .userDetailsService(this.userDetailsService)
                .tokenStore(this.tokenStore());
    }
}