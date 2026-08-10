package com.icia.delivery.domain.cart.controller;


import com.icia.delivery.domain.cart.dto.CartDTO;
import com.icia.delivery.domain.order.dto.OrderDTO;
import com.icia.delivery.domain.cart.service.CartService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {


    private final CartService csvc;


    @GetMapping("/cart/{mId}")
    public String cart() {
        return "/member/cart";
    }

    // 비정적 내부 클래스는 컴포넌트 스캔 대상이 아니라 매핑이 등록되지 않았다.
    // 바깥 @Controller 의 ResponseEntity 반환 메서드로 옮겨 JSON 응답을 유지한다.
    @PostMapping("/cartList")
    public ResponseEntity<ApiResponse<List<CartDTO>>> cartList() {
        return ResponseEntity.ok(ApiResponse.success(csvc.cartList()));
    }

    @PostMapping("/addCart/{menuId}")
    public ResponseEntity<ApiResponse<CartDTO>>  addCart(@PathVariable Long menuId, @RequestBody OrderDTO orderDTO) {
        CartDTO cartDTO = csvc.addCart(menuId, orderDTO);
        return ResponseEntity.ok(ApiResponse.success(cartDTO));
    }


    // Cart : 장바구니 상품 삭제
    @DeleteMapping("/removeCart")
    public ResponseEntity<ApiResponse<Void>> Cart(@RequestParam("cartId") Long CartId) {

                  csvc.removeCartItem(CartId);
        return ResponseEntity.ok(ApiResponse.success());
    }
    @PostMapping("/deleteCart")
    public ModelAndView deleteCart(@ModelAttribute CartDTO cart) {
        return csvc.deleteCart(cart);
    }

    @PostMapping("/updateQuantity")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(@RequestParam("cartId") Long cartId,
                                       @RequestParam("cartQuantity") Long cartQuantity) {
              csvc.updateQuantity(cartId, cartQuantity);
        return  ResponseEntity.ok(ApiResponse.success());
    }
}
