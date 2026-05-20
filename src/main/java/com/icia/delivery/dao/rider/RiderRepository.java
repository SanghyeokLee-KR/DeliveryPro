package com.icia.delivery.dao.rider;

import com.icia.delivery.dto.rider.RiderEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RiderRepository extends JpaRepository<RiderEntity, Long> {

    boolean existsByRiderId(String riderId);

    Optional<RiderEntity> findByRiderId(String riderId);

    // 페이징 처리 추가된 부분
    @Query("SELECT r FROM RiderEntity r WHERE " +
            "(:searchQuery IS NULL OR LOWER(r.riderName) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) AND " +
            "(:status IS NULL OR r.isAvailable = :status)")
    Page<RiderEntity> findStores(@Param("searchQuery") String searchQuery,
                                 @Param("status") String status,
                                 Pageable pageable);

    @Query("SELECT r FROM RiderEntity r WHERE r.isAvailable != '승인'")
    Page<RiderEntity> findApprovedRider1(Pageable pageable);

    @Query("SELECT r FROM RiderEntity r WHERE r.isAvailable = '승인'")
    Page<RiderEntity> findApprovedRider2(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE RiderEntity r SET r.isAvailable = '승인' WHERE r.riderNo = :id")
    int updateRiderStatusApprove(@Param("id") Long id);

    @Query("SELECT p FROM RiderEntity p WHERE p.riderNo = :riderNo")
    Optional<RiderEntity> findByRiderNoOptional(@Param("riderNo") Long riderNo);
}
