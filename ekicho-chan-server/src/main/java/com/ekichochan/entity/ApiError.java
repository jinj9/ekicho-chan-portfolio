package com.ekichochan.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * @author jin
 * 独自例外クラスのエラー情報を格納するJavaBean
 * ※参考記事：https://zenn.dev/ryo7/articles/api-exception-handle01
 */
@Data
public class ApiError {
    
    private Integer code;
    private String message;
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiError(Integer code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
