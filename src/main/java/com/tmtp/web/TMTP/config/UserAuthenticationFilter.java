package com.tmtp.web.TMTP.config;

import com.cloudinary.utils.StringUtils;
import com.tmtp.web.TMTP.dto.exceptions.UnauthorisedAccess;
import com.tmtp.web.TMTP.entity.Role;
import com.tmtp.web.TMTP.entity.TokenInfo;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.TokenInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final TokenInfoService tokenInfoService;

    @Autowired
    public UserAuthenticationFilter(UserService userService, TokenInfoService tokenInfoService) {
        this.userService = userService;
        this.tokenInfoService = tokenInfoService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.isNotBlank(authHeader) && authHeader.contains("bearer")) {

            String tokenValue = authHeader.replace("bearer", "");
            tokenValue = tokenValue.trim();
            TokenInfo tokenInfo = tokenInfoService.getTokenFromTokenString(tokenValue);
            if (tokenInfo != null
                    && StringUtils.isNotBlank(tokenInfo.getUserName())
                    && userService.findByUsername(tokenInfo.getUserName()) != null) {

                //extract user from the token
                User user = userService.findByUsername(tokenInfo.getUserName());
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                for (Role role : user.getRoles()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
                }

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
                Authentication a = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(a);
            } else {
                throw new UnauthorisedAccess();
            }
        }

        filterChain.doFilter(request, response);
    }
}
