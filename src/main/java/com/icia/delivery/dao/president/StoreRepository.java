package com.icia.delivery.dao.president;


import com.icia.delivery.dto.president.PreStoreEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<PreStoreEntity, Long> {
    List<PreStoreEntity> findBypreStoCategory(String category);

    @Query("SELECT s FROM PreStoreEntity s WHERE s.preStoPreMemId = :pathValue")
    List<PreStoreEntity> findByLoginId(@Param("pathValue") Long pathValue);

    @Query("SELECT COALESCE(MAX(p.preStoId), 0) FROM PreStoreEntity p")
    Long findMaxPreId();

    @Query("SELECT s FROM PreStoreEntity s WHERE s.preStoId = :storeId")
    Optional<PreStoreEntity> findByLoginId2(@Param("storeId") Long storeId);

    List<PreStoreEntity> findBypreStoId(Long storeId);

    @Query("SELECT count(s) FROM PreStoreEntity s WHERE s.preStoPreMemId = :pathValue")
    int findStoreCount(Long pathValue);

    @Query("SELECT p FROM PreStoreEntity p WHERE p.preStoId = :preStoreId")
    Optional<PreStoreEntity> findBypreStoIdOptional(@Param("preStoreId") Long preStoreId);

    @Modifying
    @Query("UPDATE PreStoreEntity p SET p.preStoOperatingDays = :operationDays, p.preStoOpeningHours = :openingHours where p.preStoId = :preStoId")
    int updateEditStoreHours(@Param("preStoId") Long preStoId,
                             @Param("operationDays") String operationDays,
                             @Param("openingHours") String openingHours);

    @Modifying
    @Query("UPDATE PreStoreEntity p SET p.preStoHolidayWeek = :preStoHolidayWeek, p.preStoDayOff = :preStoDayOff where p.preStoId = :preStoId")
    int updateEditStoreWeek(@Param("preStoId") Long preStoId,
                            @Param("preStoHolidayWeek") String preStoHolidayWeek,
                            @Param("preStoDayOff") String preStoDayOff);


    // 페이징 처리 추가된 부분
    @Query("SELECT s FROM PreStoreEntity s WHERE " +
            "(:searchQuery IS NULL OR LOWER(s.preStoName) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) AND " +
            "(:category IS NULL OR s.preStoCategory = :category) AND " +
            "(:status IS NULL OR s.preStoStatus = :status)")
    Page<PreStoreEntity> findStores(@Param("searchQuery") String searchQuery,
                                    @Param("category") String category,
                                    @Param("status") String status,
                                    Pageable pageable);


    @Query("SELECT s FROM PreStoreEntity s WHERE s.preStoStatus IN ('보류')")
    Page<PreStoreEntity> findStatusNone(Pageable pageable);

    @Query("SELECT s FROM PreStoreEntity s WHERE s.preStoStatus = '승인'")
    Page<PreStoreEntity> findApprovedStores(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE PreStoreEntity p SET p.preStoStatus = '승인' WHERE p.preStoId = :id")
    int updatePreStoStatusApprove(@Param("id") Long id);



    @Modifying
    @Query(value = "UPDATE PRE_STORE P "
            + "   SET P.pre_sto_review_count = :reviewCount "
            + " WHERE P.pre_sto_id = :storeId",
            nativeQuery = true)
    void updatePreStoreCount(Long storeId, int reviewCount);

    @Query("SELECT s FROM PreStoreEntity s WHERE s.preStoId IN :preStoIds")
    List<PreStoreEntity> findBypreStoIdCategoryList(List<Long> preStoIds);


    @Query("SELECT p FROM PreStoreEntity p WHERE p.preStoStatus = :status AND p.preStoBreakTime IS NOT NULL")
    List<PreStoreEntity> findByPreStoStatusAndPreStoBreakTimeIsNotNull(@Param("status") String status);
}


