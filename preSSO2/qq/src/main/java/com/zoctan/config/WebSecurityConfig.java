package com.zoctan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 安全配置
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${db.user.username}")
    private String username;
    @Value("${db.user.password}")
    private String password;
    @Value("${db.user.role}")
    private String role;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // 用户信息保存在内存
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(this.username).password(this.password).roles(this.role).build());
        return manager;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .antMatchers("/js/**", "/css/**", "/login", "/doLogin").permitAll()
                .anyRequest().authenticated();
    }

    /**
     * 支持 password 模式
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}