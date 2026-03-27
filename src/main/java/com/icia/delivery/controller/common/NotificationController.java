// src/main/java/com/icia/delivery/controller/common/NotificationController.java
package com.icia.delivery.controller.common;

import com.icia.delivery.dto.common.NotificationDTO;
import com.icia.delivery.dto.common.NotificationEntity;
import com.icia.delivery.exception.InvalidUserException;
import com.icia.delivery.exception.StoreNotFoundException;
import com.icia.delivery.service.common.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NotificationController는 알림 관련 REST API 엔드포인트를 제공하는 컨트롤러 클래스입니다.
 * 클라이언트의 요청을 받아 적절한 서비스 메서드를 호출하고, 결과를 반환합니다.
 */
@RestController
@RequestMapping("/api/notifications") // 공통 경로 설정
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService; // 알림 서비스

    /**
     * 새로운 알림을 생성합니다.
     *
     * @param dto 알림 정보가 담긴 NotificationDTO 객체
     * @return 생성된 알림 정보가 담긴 NotificationDTO 객체
     */
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO dto) {
        logger.info("Received request to create notification: {}", dto);
        try {
            // 알림 생성 서비스 호출
            NotificationEntity savedEntity = notificationService.createNotification(dto);

            // 저장된 엔티티를 DTO로 변환
            NotificationDTO responseDto = NotificationDTO.fromEntity(savedEntity);

            // 응답 반환
            logger.info("Notification created successfully with ID: {}", responseDto.getId());
            return ResponseEntity.ok(responseDto);
        } catch (InvalidUserException | StoreNotFoundException e) {
            logger.error("Error creating notification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error creating notification: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 수신자의 모든 알림을 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 알림 목록이 담긴 리스트
     */
    @GetMapping("/{recipientType}/{recipientId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @PathVariable String recipientType,
            @PathVariable Long recipientId) {
        logger.info("Received request to fetch notifications for recipientType: {}, recipientId: {}", recipientType, recipientId);
        try {
            // 알림 조회 서비스 호출 (네이티브 쿼리 메서드 사용)
            List<NotificationEntity> entities = notificationService.getNotifications(recipientType, recipientId);

            // 엔티티 리스트를 DTO 리스트로 변환
            List<NotificationDTO> dtos = entities.stream()
                    .map(NotificationDTO::fromEntity) // 수정된 매핑 메서드 사용
                    .toList();

            // 응답 반환
            logger.info("Fetched {} notifications.", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching notifications: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 발신자 유형과 ID에 해당하는 모든 알림을 조회합니다.
     *
     * @param senderType 발신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param senderId   발신자 ID
     * @return 알림 목록이 담긴 리스트
     */
    @GetMapping("/sender/{senderType}/{senderId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsBySender(
            @PathVariable String senderType,
            @PathVariable Long senderId) {
        logger.info("Received request to fetch notifications by senderType: {}, senderId: {}", senderType, senderId);
        try {
            // 알림 조회 서비스 호출 (네이티브 쿼리 메서드 사용)
            List<NotificationEntity> entities = notificationService.getNotificationsBySender(senderType, senderId);

            // 엔티티 리스트를 DTO 리스트로 변환
            List<NotificationDTO> dtos = entities.stream()
                    .map(NotificationDTO::fromEntity) // 수정된 매핑 메서드 사용
                    .toList();

            // 응답 반환
            logger.info("Fetched {} notifications by sender.", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching notifications by sender: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 카테고리에 해당하는 모든 알림을 조회합니다.
     *
     * @param category 알림 카테고리
     * @return 알림 목록이 담긴 리스트
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByCategory(
            @PathVariable String category) {
        logger.info("Received request to fetch notifications by category: {}", category);
        try {
            // 알림 조회 서비스 호출 (네이티브 쿼리 메서드 사용)
            List<NotificationEntity> entities = notificationService.getNotificationsByCategory(category);

            // 엔티티 리스트를 DTO 리스트로 변환
            List<NotificationDTO> dtos = entities.stream()
                    .map(NotificationDTO::fromEntity) // 수정된 매핑 메서드 사용
                    .toList();

            // 응답 반환
            logger.info("Fetched {} notifications by category.", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching notifications by category: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 상태에 해당하는 모든 알림을 조회합니다.
     *
     * @param status 알림 상태 (읽지 않음, 읽음)
     * @return 알림 목록이 담긴 리스트
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByStatus(
            @PathVariable String status) {
        logger.info("Received request to fetch notifications by status: {}", status);
        try {
            // 알림 조회 서비스 호출 (네이티브 쿼리 메서드 사용)
            List<NotificationEntity> entities = notificationService.getNotificationsByStatus(status);

            // 엔티티 리스트를 DTO 리스트로 변환
            List<NotificationDTO> dtos = entities.stream()
                    .map(NotificationDTO::fromEntity) // 수정된 매핑 메서드 사용
                    .toList();

            // 응답 반환
            logger.info("Fetched {} notifications by status.", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching notifications by status: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 수신자의 읽지 않은 알림 수를 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 읽지 않은 알림 수
     */
    @GetMapping("/{recipientType}/{recipientId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(
            @PathVariable String recipientType,
            @PathVariable Long recipientId) {
        logger.info("Received request to count unread notifications for recipientType: {}, recipientId: {}", recipientType, recipientId);
        try {
            // 읽지 않은 알림 수 조회 서비스 호출
            long count = notificationService.countUnreadNotifications(recipientType, recipientId);

            // 응답 반환
            logger.info("Unread notifications count: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error counting unread notifications: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 알림을 '읽음' 상태로 변경합니다.
     *
     * @param id 알림 ID
     * @return 응답 상태 (성공 시 200 OK)
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        logger.info("Received request to mark notification ID {} as read.", id);
        try {
            // 알림 상태 변경 서비스 호출
            notificationService.markAsRead(id);
            logger.info("Notification ID {} marked as read successfully.", id);
            // 응답 반환
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 알림을 삭제합니다.
     *
     * @param id 알림 ID
     * @return 응답 상태 (성공 시 200 OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        logger.info("Received request to delete notification ID {}.", id);
        try {
            // 알림 삭제 서비스 호출
            notificationService.deleteNotification(id);
            logger.info("Notification ID {} deleted successfully.", id);
            // 응답 반환
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Unexpected error deleting notification: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 특정 수신자의 알림을 페이징하여 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @param page          페이지 번호 (0부터 시작)
     * @param size          페이지 당 알림 개수
     * @return 알림 목록이 담긴 리스트
     */
    @GetMapping("/{recipientType}/{recipientId}/paged")
    public ResponseEntity<List<NotificationDTO>> getNotificationsPaged(
            @PathVariable String recipientType,
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to fetch paged notifications for recipientType: {}, recipientId: {}, page: {}, size: {}", recipientType, recipientId, page, size);
        try {
            // 페이징을 위한 offset과 limit 계산
            int offset = page * size;
            int limit = size;

            // 페이징된 알림 조회 서비스 호출 (네이티브 쿼리 메서드 사용)
            List<NotificationEntity> entities = notificationService.getNotificationsPaged(recipientType, recipientId, offset, limit);

            // 엔티티 리스트를 DTO 리스트로 변환
            List<NotificationDTO> dtos = entities.stream()
                    .map(NotificationDTO::fromEntity) // 수정된 매핑 메서드 사용
                    .toList();

            // 응답 반환
            logger.info("Fetched {} paged notifications.", dtos.size());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error fetching paged notifications: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
