package com.ekichochan.response;

public record AuthRegistrationAcceptedResponse(String message) {
    public static AuthRegistrationAcceptedResponse standardResponse() {
        return new AuthRegistrationAcceptedResponse(
                "If the registration can be accepted, verification instructions will be sent.");
    }
}
