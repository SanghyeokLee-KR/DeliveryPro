// src/main/java/com/icia/delivery/dao/common/NotificationRepository.java
package com.icia.delivery.dao.common;

import com.icia.delivery.dto.common.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * NotificationRepository는 NotificationEntity를 관리하는 JPA Repository 인터페이스입니다.
 * 네이티브 쿼리를 사용하여 데이터베이스와 상호작용합니다.
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // -------------------- 네이티브 쿼리 메서드 --------------------

    /**
     * 특정 수신자 유형과 ID에 해당하는 모든 알림을 네이티브 쿼리로 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 알림 목록
     */
    @Query(value = "SELECT * FROM notifications WHERE recipient_type = :recipientType AND recipient_id = :recipientId ORDER BY created_at DESC", nativeQuery = true)
    List<NotificationEntity> findByRecipientTypeAndRecipientIdNative(
            @Param("recipientType") String recipientType,
            @Param("recipientId") Long recipientId
    );

    /**
     * 특정 발신자 유형과 ID에 해당하는 모든 알림을 네이티브 쿼리로 조회합니다.
     *
     * @param senderType 발신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param senderId   발신자 ID
     * @return 알림 목록
     */
    @Query(value = "SELECT * FROM notifications WHERE sender_type = :senderType AND sender_id = :senderId ORDER BY created_at DESC", nativeQuery = true)
    List<NotificationEntity> findBySenderTypeAndSenderIdNative(
            @Param("senderType") String senderType,
            @Param("senderId") Long senderId
    );

    /**
     * 특정 카테고리에 해당하는 모든 알림을 네이티브 쿼리로 조회합니다.
     *
     * @param category 알림 카테고리
     * @return 알림 목록
     */
    @Query(value = "SELECT * FROM notifications WHERE category = :category ORDER BY created_at DESC", nativeQuery = true)
    List<NotificationEntity> findByCategoryNative(
            @Param("category") String category
    );

    /**
     * 특정 상태에 해당하는 모든 알림을 네이티브 쿼리로 조회합니다.
     *
     * @param status 알림 상태 (읽지 않음, 읽음)
     * @return 알림 목록
     */
    @Query(value = "SELECT * FROM notifications WHERE status = :status ORDER BY created_at DESC", nativeQuery = true)
    List<NotificationEntity> findByStatusNative(
            @Param("status") String status
    );

    /**
     * 특정 수신자 유형과 ID에 해당하는 읽지 않은 알림의 수를 네이티브 쿼리로 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @return 읽지 않은 알림 수
     */
    @Query(value = "SELECT COUNT(*) FROM notifications WHERE recipient_type = :recipientType AND recipient_id = :recipientId AND status = '읽지 않음'", nativeQuery = true)
    long countUnreadByRecipient(
            @Param("recipientType") String recipientType,
            @Param("recipientId") Long recipientId
    );

    /**
     * 특정 수신자 유형과 ID에 해당하는 알림을 페이징하여 네이티브 쿼리로 조회합니다.
     *
     * @param recipientType 수신자 유형 (MEMBER, ADMIN, STORE, RIDER)
     * @param recipientId   수신자 ID
     * @param offset        시작 위치
     * @param limit         조회할 개수
     * @return 페이징된 알림 목록
     */
    @Query(value = "SELECT * FROM notifications WHERE recipient_type = :recipientType AND recipient_id = :recipientId ORDER BY created_at DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<NotificationEntity> findByRecipientTypeAndRecipientIdWithPagingNative(
            @Param("recipientType") String recipientType,
            @Param("recipientId") Long recipientId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    // -------------------- 추가적인 네이티브 쿼리 메서드 --------------------

    /**
     * 알림 ID로 알림을 네이티브 쿼리로 조회합니다.
     *
     * @param id 알림 ID
     * @return 알림 엔티티
     */
    @Query(value = "SELECT * FROM notifications WHERE id = :id", nativeQuery = true)
    Optional<NotificationEntity> findByIdNative(@Param("id") Long id);

    /**
     * 알림을 네이티브 쿼리로 삭제합니다.
     *
     * @param id 알림 ID
     */
    @Query(value = "DELETE FROM notifications WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(@Param("id") Long id);
}
