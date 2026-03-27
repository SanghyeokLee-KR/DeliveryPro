package com.icia.delivery.dao.president;

import com.icia.delivery.dto.president.PreStoreMenuEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreMenuRepository extends JpaRepository<PreStoreMenuEntity, Long> {

    @Query("SELECT s FROM PreStoreMenuEntity s WHERE s.preStoId = :preStoId")
    List<PreStoreMenuEntity> findByLoginId(@Param("preStoId") Long preStoId);

    @Query("SELECT COALESCE(MAX(p.menuId), 0) FROM PreStoreMenuEntity p")
    Long findMaxPreId();

    List<PreStoreMenuEntity> findBypreStoId(Long storeId);

    @Modifying
    @Transactional
    @Query("UPDATE PreStoreMenuEntity m SET m.menuStatus = :newStatus WHERE m.menuId = :menuId")
    int updateMenuStatus(@Param("menuId") Long menuId, @Param("newStatus") String newStatus);

    List<PreStoreMenuEntity> findBymenuId(Long menuId);

    @Query("SELECT m FROM PreStoreMenuEntity m " +
            "WHERE (:keyword IS NULL OR m.menuName LIKE %:keyword%) " +
            "AND (:category IS NULL OR m.menuCategory  = :category) AND m.preStoId = :preStoId")
    List<PreStoreMenuEntity> findByMenuNameContainingOrderByMenuIdAsc(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("preStoId") Long preStoId);
}
