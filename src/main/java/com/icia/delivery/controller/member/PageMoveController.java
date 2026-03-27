package com.icia.delivery.controller.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageMoveController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

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


    @GetMapping("/president")
    public String president() {
        return "president/president";
    }

    @GetMapping("/pLoginForm")
    public String pLoginForm() {
        return "president/pLogin";
    }

    @GetMapping("/pJoinForm")
    public String pJoinForm() {
        return "president/pJoin";
    }

    @GetMapping("/storeView")
    public String storeView() {
        return "member/store-view";
    }


    /*QNA > 질문하기 페이지 이동 */
    @GetMapping("/question")
    public String question(){
        return "common/question";
    }

    @GetMapping("alarm/{mid}")
    public String alarmPage() {
        return "member/alarm";  //memberalarm.html 파일을 반환
    }

    @GetMapping("/riderReason")
    public String riderReason(){
        return "rider/riderReason";
    }

    @GetMapping("/riderCondition")
    public String riderCondition(){
        return "rider/riderCondition";
    }

    //    버튼 목록

    @GetMapping("/businessHours")
    public String businessHours(){
        return "president/management/presidentguide/businessHours";
    }

    @GetMapping("/manageCoupons")
    public String manageCoupons(){
        return "president/management/presidentguide/managementCoupons";
    }

    @GetMapping("/deliveryCost")
    public String deliveryCost(){
        return "president/management/presidentguide/deliveryCost";
    }

    @GetMapping("/cooNotation")
    public String cooNotation(){
        return "president/management/presidentguide/cooNotation";
    }

    @GetMapping("/menuManagement")
    public String menuManagement(){
        return "president/management/presidentguide/menuManagement";
    }

    @GetMapping("/closedDays")
    public String closedDays(){
        return "president/management/presidentguide/closedDays";
    }

}

