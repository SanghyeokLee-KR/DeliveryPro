// src/main/java/com/icia/delivery/service/common/NotificationService.java
package com.icia.delivery.service.common;

import com.icia.delivery.dao.admin.AdminRepository;
import com.icia.delivery.dao.common.NotificationRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dao.rider.RiderRepository;
import com.icia.delivery.dto.common.NotificationDTO;
import com.icia.delivery.dto.common.NotificationEntity;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.president.PreStoreEntity;
import com.icia.delivery.exception.InvalidUserException;
import com.icia.delivery.exception.StoreNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository; // 알림 데이터 접근을 위한 Repository

    @Autowired
    private MemberRepository memberRepository; // 회원 데이터 접근을 위한 Repository

    @Autowired
    private AdminRepository adminRepository; // 운영자 데이터 접근을 위한 Repository

    @Autowired
    private StoreRepository storeRepository; // StoreRepository 주입

    @Autowired
    private RiderRepository riderRepository; // 라이더 데이터 접근을 위한 Repository

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // WebSocket을 통한 메시지 전송을 위한 템플릿

    @Autowired
    private HttpSession httpSession; // 세션 접근을 위한 의존성 주입

    /**
     * 알림을 생성하고 데이터베이스에 저장한 후, 실시간으로 전송합니다.
     *
     * @param dto 알림 정보가 담긴 DTO
     * @return 저장된 알림 엔티티
     */
    @Transactional
    public NotificationEntity createNotification(NotificationDTO dto) {
        logger.info("Creating notification for recipientType: {}, recipientId: {}", dto.getRecipientType(), dto.getRecipientId());

        // 발신자 검증 (발신자 유형과 ID가 유효한지 확인)
        if (dto.getSenderType() != null && dto.getSenderId() != null) {
            if (!isValidUser(dto.getSenderType(), dto.getSenderId())) {
                logger.error("Invalid sender information: Type={}, ID={}", dto.getSenderType(), dto.getSenderId());
                throw new InvalidUserException("Invalid sender information");
            }
        }

        // 수신자 검증 (수신자 유형과 ID가 유효한지 확인)
        if ("STORE".equalsIgnoreCase(dto.getRecipientType())) {
            // 수신자가 STORE인 경우, 세션의 pre_store_id를 사용
            Long preStoreId = (Long) httpSession.getAttribute("preStoId");
            if (preStoreId == null) {
                logger.error("Store ID not found in session.");
                throw new StoreNotFoundException("Store ID not found in session.");
            }
            dto.setRecipientId(preStoreId);
            logger.info("Store ID from session set as recipientId: {}", preStoreId);
        } else {
            if (!isValidUser(dto.getRecipientType(), dto.getRecipientId())) {
                logger.error("Invalid recipient information: Type={}, ID={}", dto.getRecipientType(), dto.getRecipientId());
                throw new InvalidUserException("Invalid recipient information");
            }
        }

        if ("MEMBER".equalsIgnoreCase(dto.getSenderType()) && "STORE".equalsIgnoreCase(dto.getRecipientType())) {
            // 회원이 가게에 보내는 알림: "[사용자명] 새로운 주문이 들어왔습니다."
            String userNickname = getUserNickname(dto.getSenderId());
            dto.setMessage("회원 " + userNickname + "님의 새로운 주문이 들어왔습니다.");
            logger.info("Notification message set for MEMBER to STORE: {}", dto.getMessage());
        } else if ("STORE".equalsIgnoreCase(dto.getSenderType()) && "MEMBER".equalsIgnoreCase(dto.getRecipientType())) {
            // 가게가 회원에게 보내는 알림: 주문 상태에 따라 메시지 설정
            if ("접수됨".equalsIgnoreCase(dto.getCategory())) {
                dto.setMessage("주문이 접수되었습니다.");
                logger.info("Notification message set for STORE to MEMBER (accepted): {}", dto.getMessage());
            } else if ("취소됨".equalsIgnoreCase(dto.getCategory())) {
                dto.setMessage("주문이 취소되었습니다.");
                logger.info("Notification message set for STORE to MEMBER (rejected): {}", dto.getMessage());
            } else {
                // 기본 메시지 설정
                dto.setMessage("주문 상태가 변경되었습니다.");
                logger.info("Notification message set for STORE to MEMBER (default): {}", dto.getMessage());
            }
        } else if ("STORE".equalsIgnoreCase(dto.getRecipientType())) {
            // 가게에 단순히 새로운 주문이 접수되었음을 알리는 경우
            dto.setMessage("가게에 새로운 주문이 접수되었습니다.");
            logger.info("Notification message set for STORE: {}", dto.getMessage());
        }

        // 알림 생성 시, 가게 관련 알림이라면 가게 정보를 참조할 수 있습니다.
        if ("STORE".equalsIgnoreCase(dto.getRecipientType())) {
            // 비표준 메서드 이름을 사용하여 가게 조회
            Optional<PreStoreEntity> storeOpt = storeRepository.findBypreStoIdOptional(dto.getRecipientId());
            if (storeOpt.isEmpty()) {
                logger.error("Store not found with ID: {}", dto.getRecipientId());
                throw new StoreNotFoundException("Store not found with ID: " + dto.getRecipientId());
            }
            PreStoreEntity store = storeOpt.get();
            logger.info("Store found: {}", store);
            // 필요 시, 추가적인 가게 정보 활용 가능 (현재는 메시지에 이미 반영됨)
        }

        // NotificationDTO를 NotificationEntity로 변환
        NotificationEntity entity = NotificationEntity.toEntity(dto);

        // createdAt과 status 필드 설정
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus("읽지 않음");

        // 알림 엔티티를 데이터베이스에 저장
        NotificationEntity savedEntity = notificationRepository.save(entity);
        logger.info("Notification saved with ID: {}", savedEntity.getId());

        // WebSocket을 통해 실시간으로 알림 전송
        String destination = String.format("/topic/notifications/%s/%d",
                dto.getRecipientType().toLowerCase(), dto.getRecipientId());
        messagingTemplate.convertAndSend(destination, savedEntity);
        logger.info("Notification sent to destination: {}", destination);

        return savedEntity;
    }

    /**
     * 특정 수신자 유형과 ID에 해당하는 모든 알림을 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotifications(String recipientType, Long recipientId) {
        logger.info("Fetching notifications for recipientType: {}, recipientId: {}", recipientType, recipientId);
        List<NotificationEntity> notifications = notificationRepository.findByRecipientTypeAndRecipientIdNative(recipientType, recipientId);
        logger.info("Fetched {} notifications.", notifications.size());
        return notifications;
    }

    /**
     * 특정 발신자 유형과 ID에 해당하는 모든 알림을 조회합니다.
     *
     * @param senderType 발신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param senderId   발신자 ID
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsBySender(String senderType, Long senderId) {
        logger.info("Fetching notifications by senderType: {}, senderId: {}", senderType, senderId);
        List<NotificationEntity> notifications = notificationRepository.findBySenderTypeAndSenderIdNative(senderType, senderId);
        logger.info("Fetched {} notifications.", notifications.size());
        return notifications;
    }

    /**
     * 특정 카테고리에 해당하는 모든 알림을 조회합니다.
     *
     * @param category 알림 카테고리
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsByCategory(String category) {
        logger.info("Fetching notifications by category: {}", category);
        List<NotificationEntity> notifications = notificationRepository.findByCategoryNative(category);
        logger.info("Fetched {} notifications.", notifications.size());
        return notifications;
    }

    /**
     * 특정 상태에 해당하는 모든 알림을 조회합니다.
     *
     * @param status 알림 상태 (읽지 않음, 읽음)
     * @return 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsByStatus(String status) {
        logger.info("Fetching notifications by status: {}", status);
        List<NotificationEntity> notifications = notificationRepository.findByStatusNative(status);
        logger.info("Fetched {} notifications.", notifications.size());
        return notifications;
    }

    /**
     * 특정 수신자 유형과 ID에 해당하는 읽지 않은 알림의 수를 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 읽지 않은 알림 수
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(String recipientType, Long recipientId) {
        logger.info("Counting unread notifications for recipientType: {}, recipientId: {}", recipientType, recipientId);
        long count = notificationRepository.countUnreadByRecipient(recipientType, recipientId);
        logger.info("Unread notifications count: {}", count);
        return count;
    }

    /**
     * 특정 알림을 '읽음'으로 변경합니다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        logger.info("Marking notification ID {} as read.", notificationId);
        // 알림 엔티티를 데이터베이스에서 조회 (비표준 메서드 사용)
        NotificationEntity notification = notificationRepository.findByIdNative(notificationId)
                .orElseThrow(() -> {
                    logger.error("Notification not found with ID: {}", notificationId);
                    return new RuntimeException("Notification not found with ID: " + notificationId);
                });
        // 알림 상태를 '읽음'으로 변경
        notification.setStatus("읽음");
        // 변경된 알림 엔티티를 데이터베이스에 저장
        notificationRepository.save(notification);
        logger.info("Notification ID {} marked as read.", notificationId);
    }

    /**
     * 특정 알림을 삭제합니다.
     *
     * @param notificationId 알림 ID
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        logger.info("Deleting notification ID {}.", notificationId);
        // JPA의 기본 deleteById 메서드 사용
        if (!notificationRepository.existsById(notificationId)) {
            logger.error("Notification not found with ID: {}", notificationId);
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
        logger.info("Notification ID {} deleted.", notificationId);
    }

    /**
     * 특정 수신자의 알림을 페이징하여 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @param offset        시작 위치
     * @param limit         조회할 개수
     * @return 페이징된 알림 목록
     */
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsPaged(String recipientType, Long recipientId, int offset, int limit) {
        logger.info("Fetching paged notifications for recipientType: {}, recipientId: {}, offset: {}, limit: {}", recipientType, recipientId, offset, limit);
        List<NotificationEntity> notifications = notificationRepository.findByRecipientTypeAndRecipientIdWithPagingNative(recipientType, recipientId, offset, limit);
        logger.info("Fetched {} notifications.", notifications.size());
        return notifications;
    }

    /**
     * 사용자 유형과 ID가 유효한지 검증합니다.
     *
     * @param type 사용자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param id   사용자 ID
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    private boolean isValidUser(String type, Long id) {
        switch (type.toUpperCase()) {
            case "MEMBER":
                return memberRepository.existsById(id);
            case "ADMIN":
                return adminRepository.existsById(id);
            case "STORE":
                return storeRepository.findBypreStoIdOptional(id).isPresent(); // 비표준 메서드 사용
            case "RIDER":
                return riderRepository.existsById(id);
            default:
                return false;
        }
    }

    /**
     * 사용자 ID를 통해 닉네임을 조회하는 메서드
     *
     * @param memId 사용자 ID
     * @return 사용자 닉네임
     */
    private String getUserNickname(Long memId) {
        Optional<MemberEntity> memberOpt = memberRepository.findById(memId);
        return memberOpt.map(MemberEntity::getNickname)
                .orElse("Unknown User");
    }
}
