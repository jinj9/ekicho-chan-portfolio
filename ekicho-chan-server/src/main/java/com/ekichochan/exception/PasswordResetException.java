package com.ekichochan.exception;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException() {
        super("Password reset token is invalid or expired");
    }
}
