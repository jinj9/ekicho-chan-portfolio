package com.ekichochan.response;

public record PasswordResetAcceptedResponse(String message) {
    public static PasswordResetAcceptedResponse standardResponse() {
        return new PasswordResetAcceptedResponse(
                "If the account is eligible, password reset instructions will be sent.");
    }
}
