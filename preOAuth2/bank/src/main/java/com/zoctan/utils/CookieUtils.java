package com.zoctan.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie 工具
 *
 * @author Zoctan
 * @date 2018/06/24
 */
public class CookieUtils {
    public static String get(final HttpServletRequest request, final String key) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void set(final HttpServletResponse response, final String key, final String value) {
        final Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    /**
     * 删除
     */
    public static void delete(final HttpServletRequest request, final HttpServletResponse response, final String key) {
        final Cookie[] cookies = request.getCookies();

        for (int i = 0; i < (cookies == null ? 0 : cookies.length); i++) {
            if ((key).equalsIgnoreCase(cookies[i].getName())) {

                final Cookie cookie = new Cookie(key, "");
                cookie.setPath("/");
                // 设置保存cookie最大时长为0，即使其失效
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
