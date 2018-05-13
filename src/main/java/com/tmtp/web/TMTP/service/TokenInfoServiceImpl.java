package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.dto.enums.DeviceType;
import com.tmtp.web.TMTP.dto.enums.TokenType;
import com.tmtp.web.TMTP.entity.TokenInfo;
import com.tmtp.web.TMTP.repository.TokenInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenInfoServiceImpl implements TokenInfoService {

    private final TokenInfoRepository tokenRepository;

    @Autowired
    public TokenInfoServiceImpl(TokenInfoRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public TokenInfo getTokenFromTokenString(String tokenValue) {
        return tokenRepository.findByToken(tokenValue);
    }

    @Override
    public void persistToken(String userName, String tokenValue, TokenType tokenType, DeviceType deviceType) {

        List<TokenInfo> existingTokensForSameUserAndDevice = tokenRepository
                .findByUserNameAndDeviceTypeAndTokenType(userName, deviceType, tokenType);
        tokenRepository.delete(existingTokensForSameUserAndDevice);

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setDeviceType(deviceType);
        tokenInfo.setTokenType(tokenType);
        tokenInfo.setToken(tokenValue);
        tokenInfo.setUserName(userName);
        tokenRepository.save(tokenInfo);
    }

    @Override
    public void deleteToken(String userName, String tokenValue, TokenType tokenType, DeviceType deviceType) {
        TokenInfo tokenInfo = tokenRepository.findByTokenAndUserNameAndDeviceTypeAndTokenType(
                tokenValue, userName, deviceType, tokenType);
        tokenRepository.delete(tokenInfo);
    }
}