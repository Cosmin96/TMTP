package com.tmtp.web.TMTP.config;

import com.cloudinary.utils.StringUtils;
import com.tmtp.web.TMTP.entity.TokenInfo;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.TokenInfoService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.mobile.IOSController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationFilter.class);

    @Autowired
    private TokenInfoService tokenInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataFacade userDataFacade;

    @Autowired
    private SecurityService securityService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        LOG.debug("########### URL called: {}.", request.getRequestURL());

        String deviceHeader = request.getHeader("topbantzDevice");
        String authHeader = request.getHeader("Authorization");

        if (userDataFacade.retrieveLoggedUser() == null &&
                //first check if the request is from a mobile device or not
                StringUtils.isNotBlank(deviceHeader) &&
                //check if request contains bearer Authorization token
                StringUtils.isNotBlank(authHeader) && authHeader.contains("bearer")) {
            String tokenValue = authHeader.replace("bearer", "");
            tokenValue = tokenValue.trim();
            TokenInfo tokenInfo = tokenInfoService.getTokenFromTokenString(tokenValue);

            if(tokenInfo != null && StringUtils.isNotBlank(tokenInfo.getUserName())) {
                //extract user from the token
                User deviceUser = userService.findByUsername(tokenInfo.getUserName());
                securityService.autologin(deviceUser.getUsername(), deviceUser.getPassword());
            } else {
                LOG.error("Null token or no username found for token: [{}].", tokenInfo);
            }
        } else {
            LOG.debug("Either request isn't from a mobile device or it doesn't contain Authorization header, " +
                    "ignoring check in both cases.");
        }

        filterChain.doFilter(request, response);
    }
}
