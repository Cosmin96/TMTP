package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.dto.enums.DeviceType;
import com.tmtp.web.TMTP.dto.enums.TokenType;
import com.tmtp.web.TMTP.entity.TokenInfo;

public interface TokenInfoService {

    TokenInfo getTokenFromTokenString(String tokenValue);

    void persistToken(String userName, String tokenValue, TokenType tokenType, DeviceType deviceType);

    void deleteToken(String userName, String tokenValue, TokenType tokenType, DeviceType deviceType);
}
