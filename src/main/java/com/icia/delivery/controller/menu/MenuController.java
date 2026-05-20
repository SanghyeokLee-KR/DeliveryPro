package com.icia.delivery.controller.menu;


import com.icia.delivery.dto.president.PreStoreMenuDTO;
import com.icia.delivery.service.menu.MenuService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final HttpSession session;
    /**
     * 특정 가게의 메뉴 리스트 조회
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<List<PreStoreMenuDTO>> getMenuByStoreId(@PathVariable Long storeId) {
        System.out.println("대표자 아이디(메뉴) : " + storeId);
        List<PreStoreMenuDTO> menuList = menuService.getMenuByStoreId(storeId);
        return ResponseEntity.ok(menuList);
    }
    @PostMapping("menu/{menuId}")
    public ResponseEntity<List<PreStoreMenuDTO>> getMenuBymenuId(@PathVariable("menuId") Long menuId) {
        List<PreStoreMenuDTO> menuList = menuService.getMenuBymenuId(menuId);
        session.setAttribute("menuId" , menuId);
        return ResponseEntity.ok(menuList);
    }
}