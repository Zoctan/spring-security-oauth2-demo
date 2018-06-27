package com.zoctan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 安全配置
 *
 * @author Zoctan
 * @date 2018/06/27
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("user").password("123456").authorities("ROLE_USER").build());
        return manager;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .expressionHandler(new OAuth2WebSecurityExpressionHandler())
                // localhost:8000/oauth/authorize?client_id=resource1&response_type=code&redirect_uri=http://www.baidu.com
                .antMatchers("/oauth/**", "/error").permitAll()
                .antMatchers("/image").access("#oauth2.hasScope('read') and hasRole('ROLE_USER')")
                .and()
                .formLogin();
    }
}