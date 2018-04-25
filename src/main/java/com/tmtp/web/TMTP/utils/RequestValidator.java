package com.tmtp.web.TMTP.utils;

import com.tmtp.web.TMTP.entity.UserInfo;
import com.tmtp.web.TMTP.security.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    private final UserService userService;
    private final MessageSource messageSource;

    public RequestValidator(
            final UserService userService,
            final MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public String validateRegistration(UserInfo userInfo) {

        if (userInfo.getUsername().trim().isEmpty()) {
            return "Username cannot be empty or only spaces";
        } else if (userInfo.getUsername().length() < 6 || userInfo.getUsername().length() > 32) {
            return getMessage("Size.userForm.username");
        } else if (userInfo.getPassword().trim().isEmpty()) {
            return "Password cannot be empty or only spaces.";
        } else if (userInfo.getPassword().length() < 6 || userInfo.getPassword().length() > 32) {
            return getMessage("Size.userForm.password");
        } else if (userService.findByUsername(userInfo.getUsername()) != null) {
            return getMessage("Duplicate.userForm.username");
        } else if (userService.findByEmail(userInfo.getEmail()) != null) {
            return getMessage("Duplicate.userForm.email");
        }

        return null;
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}
