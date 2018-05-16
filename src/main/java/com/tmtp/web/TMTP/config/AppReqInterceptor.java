package com.tmtp.web.TMTP.config;

import com.cloudinary.utils.StringUtils;
import com.tmtp.web.TMTP.entity.TokenInfo;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.TokenInfoService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class AppReqInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AppReqInterceptor.class);

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    private TokenInfoService tokenInfoService;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private UserDataFacade userDataFacade;

    @Autowired
    @Lazy
    private SecurityService securityService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String deviceHeader = request.getHeader("topbantzDevice");
        String authHeader = request.getHeader("Authorization");

        LOG.info("\n\tDevice header: {}\n\t Auth Header: {}.", deviceHeader, authHeader);

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
                //securityService.autologin(deviceUser.getUsername(), deviceUser.getPassword());

                UsernamePasswordAuthenticationToken authReq
                        = new UsernamePasswordAuthenticationToken(deviceUser.getUsername(), deviceUser.getPassword());
                authReq.setDetails(request);
                Authentication auth = this.authenticationManager.authenticate(authReq);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                LOG.error("Null token or no username found for token: [{}].", tokenInfo);
            }
        } else {
            LOG.debug("Either request isn't from a mobile device or it doesn't contain Authorization header, " +
                    "ignoring check in both cases.");
        }

        return super.preHandle(request, response, handler);
    }
}
