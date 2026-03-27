// src/main/java/com/icia/delivery/exception/InvalidUserException.java
package com.icia.delivery.exception;

/**
 * 유효하지 않은 사용자가 요청을 했을 때 발생하는 예외입니다.
 */
public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }
}
