package com.zoctan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * QQ邮箱服务器
 *
 * @author Zoctan
 * @date 2018/06/22
 */
@ServletComponentScan
@SpringBootApplication
public class QQMailApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QQMailApplication.class, args);
    }
}
