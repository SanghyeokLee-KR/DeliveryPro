package com.icia.delivery.dao.menu;

import com.icia.delivery.dto.menu.MenuEntity;
import com.icia.delivery.dto.president.PreStoreMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    List<PreStoreMenuEntity> findByStoreId(Long storeId);
}
