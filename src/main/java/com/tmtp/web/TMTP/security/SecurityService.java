package com.tmtp.web.TMTP.security;

public interface SecurityService {

    String findLoggedInUsername();

    void autologin(String username, String password);
}
