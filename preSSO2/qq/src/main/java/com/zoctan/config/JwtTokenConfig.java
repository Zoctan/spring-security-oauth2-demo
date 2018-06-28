package com.zoctan.config;

/**
 * ${todo}
 *
 * @author Zoctan
 * @date 6/28/18 21:56
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JwtTokenConfig {

    /**
     * 使用jwtTokenStore存储token
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(this.jwtAccessTokenConverter());
    }

    /**
     * 生成jwt
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        // 秘钥
        accessTokenConverter.setSigningKey("qq");
        return accessTokenConverter;
    }

    /**
     * 扩展JWT
     */
    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return new MyJwtTokenEnhancer();
    }

}

class MyJwtTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(final OAuth2AccessToken accessToken, final OAuth2Authentication authentication) {
        final Map<String, Object> info = new HashMap<>(1);
        //扩展返回的token
        info.put("author", "zoctan");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}