package com.icia.delivery.controller.member;


import com.icia.delivery.dto.member.CartDTO;
import com.icia.delivery.dto.member.OrderDTO;
import com.icia.delivery.service.member.CartService;
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

    @RestController
    public class CartRestController {
        @PostMapping("/cartList")
    public List<CartDTO> cartList() {
        return csvc.cartList();
    }
}

    @PostMapping("/addCart/{menuId}")
    public ResponseEntity<CartDTO>  addCart(@PathVariable Long menuId, @RequestBody OrderDTO orderDTO) {
        CartDTO cartDTO = csvc.addCart(menuId, orderDTO);
        return ResponseEntity.ok(cartDTO);
    }


    // Cart : 장바구니 상품 삭제
    @DeleteMapping("/removeCart")
    public ResponseEntity<CartDTO> Cart(@RequestParam("cartId") Long CartId) {

                  csvc.removeCartItem(CartId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/deleteCart")
    public ModelAndView deleteCart(@ModelAttribute CartDTO cart) {
        return csvc.deleteCart(cart);
    }

    @PostMapping("/updateQuantity")
    public ResponseEntity<CartDTO> updateQuantity(@RequestParam("cartId") Long cartId,
                                       @RequestParam("cartQuantity") Long cartQuantity) {
              csvc.updateQuantity(cartId, cartQuantity);
        return  ResponseEntity.ok().build();
    }
}
