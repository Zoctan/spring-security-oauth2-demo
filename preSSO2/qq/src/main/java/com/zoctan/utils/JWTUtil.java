package com.zoctan.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JWT工具
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Component
public class JWTUtil {
    @Value("${db.user.role}")
    private String role;
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret}")
    private String clientSecret;
    @Value("${client.scope}")
    private String clientScope;

    @Resource
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    public OAuth2AccessToken generateToken(final String username, final String password) {
        final Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(this.role));

        final Map<String, String> requestParameters = new HashMap<>();
        final Set<String> scope = new HashSet<>();
        scope.add(this.clientScope);
        final Set<String> resourceIds = new HashSet<>();
        final Set<String> responseTypes = new HashSet<>();
        responseTypes.add("token");
        final Map<String, Serializable> extensionProperties = new HashMap<>();

        final OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, this.clientId,
                authorities, true, scope,
                resourceIds, null, responseTypes, extensionProperties);

        final User userPrincipal = new User(username, password, true, true, true, true, authorities);

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
        final OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
        final OAuth2AccessToken token = this.authorizationServerTokenServices.createAccessToken(auth);
        System.out.println(token.getValue());
        return token;
    }
}
