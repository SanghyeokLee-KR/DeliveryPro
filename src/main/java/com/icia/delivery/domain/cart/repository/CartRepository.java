package com.icia.delivery.domain.cart.repository;

import com.icia.delivery.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findAllByCartId(Long cartId);


    List<CartEntity> findByMemId(Long memId);
}
