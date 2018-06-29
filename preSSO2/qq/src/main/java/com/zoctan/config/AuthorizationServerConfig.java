package com.zoctan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务配置
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;
    @Value("${client.resource.id}")
    private String clientResourceId;
    @Value("${client.scope}")
    private String clientScope;

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisConnectionFactory redisConnectionFactory;
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Resource
    private TokenEnhancer jwtTokenEnhancer;

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(this.redisConnectionFactory);
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
        //----密码模式
        // curl -i -X POST -d "username=user&password=123456&grant_type=password&client_id=mail&client_secret=mail123" http://localhost:8000/oauth/token

        // 客户端信息保存在内存
        clients.inMemory()
                .withClient(this.clientId)
                .secret(this.clientSecret)
                .resourceIds(this.clientResourceId)
                .authorizedGrantTypes("password", "refresh_token")
                .scopes(this.clientScope);
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(this.authenticationManager)
                .tokenStore(this.tokenStore());

        // 扩展token返回结果
        if (this.jwtAccessTokenConverter != null && this.jwtTokenEnhancer != null) {
            final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            final List<TokenEnhancer> enhancerList = new ArrayList();
            enhancerList.add(this.jwtTokenEnhancer);
            enhancerList.add(this.jwtAccessTokenConverter);
            tokenEnhancerChain.setTokenEnhancers(enhancerList);
            // jwt
            endpoints.tokenEnhancer(tokenEnhancerChain)
                    .accessTokenConverter(this.jwtAccessTokenConverter);
        }
    }
}