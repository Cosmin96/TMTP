package com.tmtp.web.TMTP.dto.exceptions;

import com.tmtp.web.TMTP.dto.exceptions.StorageException;

public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
