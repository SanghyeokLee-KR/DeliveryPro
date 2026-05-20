package com.icia.delivery.service.member;

public class OrderNotFoundException extends RuntimeException {
    // 생성자에서 메시지를 받을 수 있게 설정
    public OrderNotFoundException(String message) {
        super(message);
    }
}