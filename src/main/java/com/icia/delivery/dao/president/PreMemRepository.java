package com.icia.delivery.dao.president;

import com.icia.delivery.dto.president.PreMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreMemRepository extends JpaRepository<PreMemberEntity, Long> {

    boolean existsByPreMemUserId(String preMemUserId);

    boolean existsByPreMemEmail(String preMemEmail);

    Optional<PreMemberEntity> findByPreMemUserId(String preMemUserId);

    @Query("SELECT COALESCE(MAX(p.preMemId), 0) FROM PreMemberEntity p")
    Long findMaxPreMemId();

    @Query("SELECT p.preMemCeoName FROM PreMemberEntity p where p.preMemId = :pathValue")
    String findByPreMemName(@Param("pathValue") Long pathValue);
}
