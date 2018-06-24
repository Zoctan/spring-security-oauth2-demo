package com.zoctan.entity;

/**
 * 认证服务器返回的包含 token 的响应
 *
 * @author Zoctan
 * @date 2018/06/24
 */
public class Result {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private String scope;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(final String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return this.token_type;
    }

    public void setToken_type(final String token_type) {
        this.token_type = token_type;
    }

    public Integer getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(final Integer expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(final String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }
}
