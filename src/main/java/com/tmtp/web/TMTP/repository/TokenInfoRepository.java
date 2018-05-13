package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.dto.enums.DeviceType;
import com.tmtp.web.TMTP.dto.enums.TokenType;
import com.tmtp.web.TMTP.entity.TokenInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenInfoRepository extends MongoRepository<TokenInfo, String> {

    TokenInfo findByToken(String token);

    TokenInfo findByTokenAndUserNameAndDeviceTypeAndTokenType(
            String tokenValue, String userName, DeviceType deviceType, TokenType tokenType);

    List<TokenInfo> findByUserNameAndDeviceTypeAndTokenType(
            String userName, DeviceType deviceType, TokenType tokenType);
}
