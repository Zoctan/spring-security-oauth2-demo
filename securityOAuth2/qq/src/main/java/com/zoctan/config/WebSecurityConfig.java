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
 * @date 2018/06/27
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${oauth.username}")
    private String username;
    @Value("${oauth.password}")
    private String password;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        // 用户信息保存在内存
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(this.username).password(this.password).authorities("ROLE_USER").build());
        return manager;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/oauth/**", "/login")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .and()
                //.csrf().disable()
                .formLogin()
                // 跳转登录页面的控制器
                //.loginPage("/login_page").loginProcessingUrl("/login").failureUrl("/login_page").successForwardUrl("/")
                .permitAll();
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