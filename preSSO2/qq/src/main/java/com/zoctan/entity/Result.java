package com.zoctan.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 认证服务器返回的包含 token 的响应
 *
 * @author Zoctan
 * @date 2018/06/28
 */
public class Result {
    @JSONField(name = "access_token")
    private String accessToken;
    @JSONField(name = "token_type")
    private String tokenType;
    @JSONField(name = "expires_in")
    private Integer expiresIn;
    @JSONField(name = "refresh_token")
    private String refreshToken;
    @JSONField(name = "scope")
    private String scope;
    @JSONField(name = "error")
    private String error;
    @JSONField(name = "error_description")
    private String errorDescription;

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(final Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getError() {
        return this.error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
