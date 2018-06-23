package com.zoctan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 豆瓣服务器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@SpringBootApplication
public class DouBanApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DouBanApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
