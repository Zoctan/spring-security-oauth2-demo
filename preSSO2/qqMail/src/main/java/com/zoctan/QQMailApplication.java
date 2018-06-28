package com.zoctan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * QQ邮箱服务器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@SpringBootApplication
public class QQMailApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QQMailApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
