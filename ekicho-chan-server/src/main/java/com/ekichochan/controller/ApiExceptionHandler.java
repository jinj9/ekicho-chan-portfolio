package com.ekichochan.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ekichochan.entity.ApiError;
import com.ekichochan.exception.BadRequestException;
import com.ekichochan.exception.StationDataNotFoundException;
import com.ekichochan.exception.RegistrationInputException;
import com.ekichochan.exception.EmailVerificationException;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.exception.PasswordResetException;

/**
 * @author jin 例外処理をハンドリングするクラス
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<Object> handlePasswordResetException(
            PasswordResetException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(),
                "INVALID_OR_EXPIRED_PASSWORD_RESET_TOKEN", path));
    }

    @ExceptionHandler(AuthLoginException.class)
    public ResponseEntity<Object> handleAuthLoginException(AuthLoginException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError(HttpStatus.UNAUTHORIZED.value(), "LOGIN_FAILED", path));
    }

    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<Object> handleEmailVerificationException(
            EmailVerificationException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),
                "INVALID_OR_EXPIRED_VERIFICATION_TOKEN", path);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(RegistrationInputException.class)
    public ResponseEntity<Object> handleRegistrationInputException(
            RegistrationInputException ex, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getCode(), path);
        return ResponseEntity.badRequest().body(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {
        // リクエストのBodyのセット
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError;
        if (body instanceof ApiError existingError) {
            apiError = existingError;
        } else {
            String message = statusCode.is4xxClientError()
                    ? "Request could not be processed"
                    : "Unexpected server error";
            apiError = new ApiError(statusCode.value(), message, path);
        }

        return super.handleExceptionInternal(ex, apiError, headers, statusCode, request);
    }

    // StationDataNotFoundException
    @ExceptionHandler({ StationDataNotFoundException.class })
    public ResponseEntity<Object> handleStationDataNotFoundException(StationDataNotFoundException ex, WebRequest request) {
        // リクエストのBodyのセット
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), path);

        // リクエストヘッダーの作成
        HttpHeaders httpHeaders = new HttpHeaders();

        return handleExceptionInternal(ex, apiError, httpHeaders, HttpStatus.NOT_FOUND, request);
    }

    // BadRequestException
    @ExceptionHandler({ BadRequestException.class })
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
        // リクエストのBodyのセット
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), path);

        // リクエストヘッダーの作成
        HttpHeaders httpHeaders = new HttpHeaders();

        return handleExceptionInternal(ex, apiError, httpHeaders, HttpStatus.BAD_REQUEST, request);
    }

}
