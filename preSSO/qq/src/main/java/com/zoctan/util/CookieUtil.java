package com.zoctan.util;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 *
 * @author Zoctan
 * @date 7/3/18 14:53
 */
public class CookieUtil {

    /**
     * 添加cookie
     */
    public static void set(final HttpServletResponse response,
                           final String cookieName,
                           final String value) {
        final Cookie cookie = new Cookie(cookieName, value);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    /**
     * 删除cookie
     */
    public static void delete(final HttpServletResponse response,
                              final String cookieName) {
        final Cookie uid = new Cookie(cookieName, null);
        uid.setPath("/");
        uid.setMaxAge(0);
        response.addCookie(uid);
    }

    /**
     * 获取cookie值
     */
    public static String get(final HttpServletRequest request,
                             final String cookieName) {
        try {
            final Cookie[] cookies = request.getCookies();
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        } catch (final Exception ignored) {
        }
        return null;
    }
}