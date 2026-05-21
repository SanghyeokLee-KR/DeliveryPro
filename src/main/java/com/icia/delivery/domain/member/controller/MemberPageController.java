package com.icia.delivery.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberPageController {

    @GetMapping("/mJoinForm")
    public String mJoinForm() {
        return "member/join";
    }

    @GetMapping("/mLoginForm")
    public String getLogin() {
        return "member/login";
    }

    @GetMapping("/agree")
    public String agree() {
        return "member/agree";
    }

    @GetMapping("/customer")
    public String customerMain() {
        return "customer-main";
    }

    @GetMapping("/payment")
    public String payment() {
        return "member/payment";
    }

    @GetMapping("/cart")
    public String cart() {
        return "member/cart";
    }

    @GetMapping("/storeList")
    public String menu() {
        return "member/store-list";
    }

    @GetMapping("/store")
    public String store() {
        return "member/store";
    }

    @GetMapping("/storeView")
    public String storeView() {
        return "member/store-view";
    }

    @GetMapping("alarm/{mid}")
    public String alarmPage() {
        return "member/alarm";
    }
}
