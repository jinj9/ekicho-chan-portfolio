/**
 * 
 */
package com.ekichochan.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

/**
 * @author jin
 * 独自例外処理クラス
 *@param message 出力メッセージ
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private final Logger logger = LoggerFactory.getLogger("appLogger");

    private Integer code;

    public BadRequestException(String message) {
        super(message);
        logger.info(message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
