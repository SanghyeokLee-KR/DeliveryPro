package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.LoginHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {

    /**
     * 회원 번호(mem_id)를 기준으로 모든 로그인 내역을 조회하는 메서드
     *
     * @param mId 회원 번호
     * @return LoginHistoryDTO 리스트
     */
    @Query("SELECT new com.icia.delivery.dto.member.LoginHistoryDTO(lh.hisLoginId, lh.member.mId, lh.hisLoginDate, lh.hisIpAddress, lh.hisDeviceOs, lh.hisBrowser) " +
            "FROM LoginHistoryEntity lh WHERE lh.member.mId = :mId ORDER BY lh.hisLoginDate DESC")
    List<LoginHistoryDTO> findLoginHistoriesByMemberId(@Param("mId") Long mId);

    /**
     * 새로운 로그인 내역을 저장하는 메서드
     *
     * @param entity LoginHistoryEntity 객체
     * @return 저장된 LoginHistoryEntity 객체
     */
    @Override
    <S extends LoginHistoryEntity> S save(S entity);


    @Query("SELECT l FROM LoginHistoryEntity l " +
            "JOIN l.member m " +
            "WHERE (:searchQuery IS NULL OR m.userId LIKE %:searchQuery%) " + // userId 비교
            "AND (:hisDeviceOs IS NULL OR l.hisDeviceOs = :hisDeviceOs) " +
            "AND (:hisBrowser IS NULL OR l.hisBrowser = :hisBrowser)")
    Page<LoginHistoryEntity> searchMemberLogs(
            @Param("searchQuery") String searchQuery,
            @Param("hisDeviceOs") String hisDeviceOs,
            @Param("hisBrowser") String hisBrowser,
            Pageable pageable);
}
