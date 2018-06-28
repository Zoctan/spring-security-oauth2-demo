package com.zoctan.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


/**
 * 用户控制器
 *
 * @author Zoctan
 * @date 2018/06/28
 */
@RestController
public class UserController {
    @GetMapping("/principal")
    public String getPrincipal(final Principal principal) {
        return JSON.toJSONString(principal);
    }
}
