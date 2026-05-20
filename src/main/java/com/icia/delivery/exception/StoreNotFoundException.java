// src/main/java/com/icia/delivery/exception/StoreNotFoundException.java
package com.icia.delivery.exception;

/**
 * 세션에서 가게 ID를 찾을 수 없거나, 해당 가게가 존재하지 않을 때 발생하는 예외입니다.
 */
public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(String message) {
        super(message);
    }
}
