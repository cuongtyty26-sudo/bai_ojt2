package com.example.baiojt2.exception;

/**
 * Represents a business-rule violation that can be shown safely to the user.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
