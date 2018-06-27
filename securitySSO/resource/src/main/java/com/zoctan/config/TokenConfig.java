package com.zoctan.config;

import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.annotation.Resource;

@Configuration
public class TokenConfig {
    @Resource
    private OAuth2ClientProperties oAuth2ClientProperties;
    @Resource
    private AuthorizationServerProperties authorizationServerProperties;

    @Bean
    public ResourceServerTokenServices tokenServices() {
        final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl(this.authorizationServerProperties.getCheckTokenAccess());
        remoteTokenServices.setClientId(this.oAuth2ClientProperties.getClientId());
        remoteTokenServices.setClientSecret(this.oAuth2ClientProperties.getClientSecret());
        remoteTokenServices.setAccessTokenConverter(this.accessTokenConverter());
        return remoteTokenServices;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new DefaultAccessTokenConverter();
    }
}
