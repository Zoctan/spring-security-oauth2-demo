package com.zoctan.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 认证服务器返回的包含 token 的响应
 *
 * @author Zoctan
 * @date 2018/06/24
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

    public static final class Builder {
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String refreshToken;
        private String scope;

        private Builder() {
        }

        public static Builder aResult() {
            return new Builder();
        }

        public Builder withAccessToken(final String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withTokenType(final String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder withExpiresIn(final Integer expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder withRefreshToken(final String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder withScope(final String scope) {
            this.scope = scope;
            return this;
        }

        public Builder but() {
            return aResult().withAccessToken(this.accessToken).withTokenType(this.tokenType).withExpiresIn(this.expiresIn).withRefreshToken(this.refreshToken).withScope(this.scope);
        }

        public Result build() {
            final Result result = new Result();
            result.setAccessToken(this.accessToken);
            result.setTokenType(this.tokenType);
            result.setExpiresIn(this.expiresIn);
            result.setRefreshToken(this.refreshToken);
            result.setScope(this.scope);
            return result;
        }
    }
}
