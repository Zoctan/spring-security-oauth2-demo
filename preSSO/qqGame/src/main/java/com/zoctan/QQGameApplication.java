package com.zoctan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * QQ游戏服务器
 *
 * @author Zoctan
 * @date 2018/07/03
 */
@ServletComponentScan
@SpringBootApplication
public class QQGameApplication {

    public static void main(final String[] args) {
        SpringApplication.run(QQGameApplication.class, args);
    }
}
