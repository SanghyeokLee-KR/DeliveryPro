// src/main/java/com/icia/delivery/dto/common/NotificationEntity.java
package com.icia.delivery.dto.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드를 포함한 생성자
@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 알림 ID (Primary Key)

    @Column(name = "sender_type", length = 50)
    private String senderType; // 발신자 유형 (MEMBER, ADMIN, STORE, RIDER)

    @Column(name = "sender_id")
    private Long senderId; // 발신자 ID

    @Column(name = "recipient_type", nullable = false, length = 50)
    private String recipientType; // 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId; // 수신자 ID

    @Column(name = "category", nullable = false, length = 50)
    private String category; // 알림 카테고리

    @Column(name = "message", nullable = false, length = 255)
    private String message; // 알림 메시지

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 알림 생성 시간

    @Column(name = "status", nullable = false, length = 50)
    private String status = "읽지 않음"; // 알림 상태 (읽지 않음, 읽음)

    /**
     * NotificationDTO 객체를 NotificationEntity 객체로 변환하는 정적 메서드
     *
     * @param dto 변환할 NotificationDTO 객체
     * @return 변환된 NotificationEntity 객체
     */
    public static NotificationEntity toEntity(NotificationDTO dto) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(dto.getId());
        entity.setSenderType(dto.getSenderType());
        entity.setSenderId(dto.getSenderId());
        entity.setRecipientType(dto.getRecipientType());
        entity.setRecipientId(dto.getRecipientId());
        entity.setCategory(dto.getCategory());
        entity.setMessage(dto.getMessage());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
