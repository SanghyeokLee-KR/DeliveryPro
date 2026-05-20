package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findAllByCartId(Long cartId);


    List<CartEntity> findByMemId(Long memId);
}
