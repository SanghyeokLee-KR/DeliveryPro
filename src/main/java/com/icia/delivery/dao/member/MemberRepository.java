// src/main/java/com/icia/delivery/dao/member/MemberRepository.java
package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUserId(String userId);

    Optional<MemberEntity> findByEmail(String email);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);   // 이메일 중복 확인

    @Query("SELECT m.username FROM MemberEntity m WHERE m.mId = :memId")
    String findByUserName(@Param("memId") Long memId);

    @Transactional
    @Modifying
    @Query("UPDATE MemberEntity m SET m.address = :newAddress WHERE m.mId = :memberId")
    void updateMemAddress(@Param("memberId") Long memberId, @Param("newAddress") String newAddress);

    /**
     * 다중 필터와 검색어를 기반으로 회원을 페이징하여 조회하는 메서드
     *
     * @param searchQuery 회원 아이디 또는 이름 검색어
     * @param gender      성별 필터
     * @param grade       등급 필터
     * @param status      상태 필터
     * @param pageable    페이징 및 정렬 정보
     * @return 페이징된 회원 엔티티
     */
    @Query("SELECT m FROM MemberEntity m " +
            "WHERE (:searchQuery IS NULL OR m.userId LIKE %:searchQuery% OR m.username LIKE %:searchQuery%) " +
            "AND (:gender IS NULL OR m.gender = :gender) " +
            "AND (:grade IS NULL OR m.grade = :grade) " +
            "AND (:status IS NULL OR m.status = :status)")
    Page<MemberEntity> searchMembers(
            @Param("searchQuery") String searchQuery,
            @Param("gender") String gender,
            @Param("grade") String grade,
            @Param("status") String status,
            Pageable pageable);



    @Query("SELECT m.address FROM MemberEntity m WHERE m.mId = :memId")
    String findAddressByMemberId(Long memId);

    // 회원 ID로 userId만 조회하는 쿼리
    @Query("SELECT m.userId FROM MemberEntity m WHERE m.mId = :memberId")
    List<String> findMemberUserIdById(@Param("memberId") Long memberId);
}

