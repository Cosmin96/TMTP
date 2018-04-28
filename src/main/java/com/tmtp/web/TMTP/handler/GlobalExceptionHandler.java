package com.tmtp.web.TMTP.handler;

import com.tmtp.web.TMTP.dto.ErrorMessage;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.NoUserFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ResponseBody
    @ExceptionHandler(BadFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage badRequest(HttpServletRequest request, Exception exception) {
        String exceptionName = "BadFormatException";
        ErrorMessage message = getErrorMessage(request, exception, HttpStatus.BAD_REQUEST.value());
        LOG.error(messageSource.getMessage("EXCEPTION_MESSAGE", new Object[]{exceptionName, message, exception}, Locale.ENGLISH));
        return message;
    }

    @ResponseBody
    @ExceptionHandler(NoUserFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage noUserFound(HttpServletRequest request, Exception exception) {
        String exceptionName = "NoUserFound";
        ErrorMessage message = getErrorMessage(request, exception, HttpStatus.NOT_FOUND.value());
        LOG.error(messageSource.getMessage("EXCEPTION_MESSAGE", new Object[]{exceptionName, message, exception}, Locale.ENGLISH));
        return message;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage exceptionFound(HttpServletRequest request, Exception exception) {
        String exceptionName = "Exception";
        ErrorMessage message = getErrorMessage(request, exception, HttpStatus.INTERNAL_SERVER_ERROR.value());
        LOG.error(messageSource.getMessage("EXCEPTION_MESSAGE", new Object[]{exceptionName, message, exception}, Locale.ENGLISH));
        return message;
    }

    private ErrorMessage getErrorMessage(HttpServletRequest request, Exception exception, int status) {
        ErrorMessage message = new ErrorMessage();
        message.setTimestamp(new Date().getTime());
        message.setError(exception.getMessage());
        message.setMessage(exception.getMessage());
        message.setStatus(status);
        message.setException(exception.getClass().getSimpleName());
        message.setPath(request.getRequestURI());
        return message;
    }
}
