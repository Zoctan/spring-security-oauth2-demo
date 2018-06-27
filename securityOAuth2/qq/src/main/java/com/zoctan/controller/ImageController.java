package com.zoctan.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


/**
 * 图片控制器
 *
 * @author Zoctan
 * @date 2018/06/27
 */
@RestController
public class ImageController {
    @GetMapping("/image")
    public Object getImageList() {
        return JSON.toJSON(Arrays.asList("1.png", "2.png"));
    }
}
