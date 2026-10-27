package com.ekichochan.exception;

public class RegistrationInputException extends RuntimeException {
    private final String code;

    public RegistrationInputException(String code) {
        super("Registration input is invalid");
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
