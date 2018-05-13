package com.tmtp.web.TMTP.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tmtp.web.TMTP.dto.enums.DeviceType;
import com.tmtp.web.TMTP.dto.enums.TokenType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Document(collection = "token_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenInfo implements Serializable {

    private static final long serialVersionUID = -6507874041300292751L;

    @Id
    private String tokenId;

    private String token;
    private String userName;
    private TokenType tokenType;
    private DeviceType deviceType;

    private Date createdOn = new Date();

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenInfo tokenInfo = (TokenInfo) o;
        return Objects.equals(tokenId, tokenInfo.tokenId) &&
                Objects.equals(token, tokenInfo.token) &&
                Objects.equals(userName, tokenInfo.userName) &&
                tokenType == tokenInfo.tokenType &&
                deviceType == tokenInfo.deviceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId, token, userName, tokenType, deviceType);
    }

    @Override
    public String toString() {
        return String.format("TokenInfo{tokenId='%s', userName='%s', tokenType=%s, deviceType=%s, createdOn=%s}",
                tokenId, userName, tokenType, deviceType, createdOn);
    }
}
