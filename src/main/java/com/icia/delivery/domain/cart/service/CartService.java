package com.icia.delivery.domain.cart.service;

import com.icia.delivery.domain.member.repository.MemberRepository;
import com.icia.delivery.domain.cart.dto.CartDTO;
import com.icia.delivery.domain.cart.entity.CartEntity;
import com.icia.delivery.domain.cart.repository.CartRepository;
import com.icia.delivery.domain.storemenu.entity.PreStoreMenuEntity;
import com.icia.delivery.domain.storemenu.repository.StoreMenuRepository;
import com.icia.delivery.domain.member.entity.MemberEntity;
import com.icia.delivery.domain.order.dto.OrderDTO;
import com.icia.delivery.domain.order.dto.OrderItemDTO;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    private final MemberRepository mrepo;
    private final StoreMenuRepository strepo;

    private final CartRepository crepo;
    private final HttpSession session;
    private ModelAndView mav;

    /**
     * 장바구니 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CartDTO> cartList() {
        List<CartDTO> dtoList = new ArrayList<>();

        // 세션에서 mem_id 가져오기
        Long memId = (Long) session.getAttribute("mem_id");

        // MemberEntity 조회
        Optional<MemberEntity> entity = mrepo.findById(memId);

        if (entity.isEmpty()) {
            log.debug("Member not found while loading cart. memId={}", memId);
            return dtoList; // 빈 목록 반환
        }

        // CartEntity 목록 가져오기
        List<CartEntity> cartItems = crepo.findByMemId(memId);
        if (cartItems.isEmpty()) {
            log.debug("No cart items found. memId={}", memId);
            return dtoList; // 빈 목록 반환
        }

        // CartEntity -> CartDTO 변환
        for (CartEntity cartItem : cartItems) {
            dtoList.add(CartDTO.toDTO(cartItem)); // DTO로 변환 후 리스트에 추가
        }

        return dtoList; // 변환된 DTO 리스트 반환
    }

    /*
     * 장바구니 항목 삭제
     */
    @Transactional
    public ModelAndView removeCartItem(Long CartId) {
        mav = new ModelAndView();

        crepo.deleteById(CartId);

        mav.setViewName("redirect:/cart");
        mav.addObject("CartId", CartId);
        return mav;
    }

    /*
     * 장바구니 비우기
     */
    /*
     장바구니 항목 수량 업데이트
   */
    @Transactional
    public ModelAndView updateQuantity(Long CartId, Long CartQuantity) {
        // CNum으로 장바구니 항목 조회
        List<CartEntity> cartList = crepo.findAllByCartId(CartId);

        // 조회된 카트 항목이 없으면 처리
        if (cartList.isEmpty()) {
            log.warn("Cart item not found for quantity update. cartId={}", CartId);
            // 필요한 경우 예외를 던지거나 메시지를 반환할 수 있습니다.
            ModelAndView mav = new ModelAndView();
            mav.setViewName("redirect:/cart");  // 장바구니로 리디렉션
            return mav;
        }
        // 여러 항목에 대해 수량 업데이트
        ModelAndView mav = new ModelAndView();
        for (CartEntity cart : cartList) {
            cart.setCartQuantity(CartQuantity);  // 수량 업데이트
            crepo.save(cart);  // 엔티티 저장
        }

        // 장바구니 페이지로 리디렉션
        mav.setViewName("redirect:/cart");
        return mav;
    }

    @Transactional
    public CartDTO addCart(Long menuId, OrderDTO orderDTO) {

        ModelAndView mav = new ModelAndView();

        Long memId = (Long) session.getAttribute("mem_id");

        // 메뉴 정보 가져오기
        Optional<PreStoreMenuEntity> menuOpt = strepo.findById(menuId);
        if (menuOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "해당 메뉴를 찾을 수 없습니다: menuId = " + menuId);
        }

        PreStoreMenuEntity menu = menuOpt.get();

        // 클라이언트에서 받은 OrderDTO에서 OrderItemDTO 리스트 가져오기
        List<OrderItemDTO> orderItems = orderDTO.getOrderItems();  // OrderItemDTO 리스트 가져오기

        // 첫 번째 OrderItemDTO를 가져오기 (수량을 얻기 위해)
        OrderItemDTO orderItem = null;
        if (!orderItems.isEmpty()) {
            orderItem = orderItems.getFirst();  // 첫 번째 항목을 가져옵니다.
        }

        // 수량 설정 (null 체크 필요)
        Long quantity = 1L;  // 기본 수량은 1로 설정
        if (orderItem != null) {
            quantity = orderItem.getQuantity();  // 실제 수량을 가져옴
        }

        // CartEntity 생성 및 설정
        CartEntity cartEntity = new CartEntity();
        cartEntity.setMemId(memId);
        cartEntity.setMenuId(menuId);
        cartEntity.setCartName(menu.getMenuName());
        cartEntity.setCartImgUrl(menu.getMenuPictureUrl());
        cartEntity.setCartQuantity(quantity);  // 가져온 수량 설정
        cartEntity.setCartPrice(menu.getMenuPrice());

        // 데이터베이스에 저장
        CartEntity savedEntity = crepo.save(cartEntity);

        // CartDTO로 변환 (optional)
        return   CartDTO.toDTO(savedEntity);
    }

    @Transactional
    public ModelAndView deleteCart(CartDTO cart) {

        mav = new ModelAndView();
        crepo.deleteAll();
        mav.setViewName("redirect:/cart");
        mav.addObject("cart", cart);
        return mav;
    }
}
