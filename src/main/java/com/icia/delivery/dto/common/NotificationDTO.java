// src/main/java/com/icia/delivery/dto/common/NotificationDTO.java
package com.icia.delivery.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함한 생성자
public class NotificationDTO {

    private Long id; // 알림 ID

    private String senderType; // 발신자 유형 (MEMBER, ADMIN, STORE, RIDER)

    private Long senderId; // 발신자 ID

    private String recipientType; // 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)

    private Long recipientId; // 수신자 ID

    private String category; // 알림 카테고리

    private String message; // 알림 메시지

    private LocalDateTime createdAt; // 알림 생성 시간

    private String status; // 알림 상태 (읽지 않음, 읽음)

    /**
     * NotificationEntity 객체를 NotificationDTO 객체로 변환하는 정적 메서드
     *
     * @param entity 변환할 NotificationEntity 객체
     * @return 변환된 NotificationDTO 객체
     */
    public static NotificationDTO fromEntity(NotificationEntity entity) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setSenderType(entity.getSenderType());
        dto.setSenderId(entity.getSenderId());
        dto.setRecipientType(entity.getRecipientType());
        dto.setRecipientId(entity.getRecipientId());
        dto.setCategory(entity.getCategory());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
