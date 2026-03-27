package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.UserProfile;
import com.icia.delivery.service.member.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/nLogin") // 네이버 로그인 요청 처리
    public String naverLogin(HttpSession session) {
        String state = UUID.randomUUID().toString(); // CSRF 방지용 상태 값 생성
        session.setAttribute("oauth_state", state);

        // 네이버 인증 페이지로 리다이렉트 URL 생성
        String authorizationUrl = "https://nid.naver.com/oauth2.0/authorize?" +
                "response_type=code" +
                "&client_id=" + authService.getClientId() +
                "&redirect_uri=" + authService.getRedirectUri() +
                "&state=" + state;

        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/naver-login") // 네이버 로그인 콜백 처리
    public String naverCallback(@RequestParam("code") String code,
                                @RequestParam("state") String state,
                                HttpSession session) throws Exception {
        String accessToken = authService.getAccessToken(code, state); // 액세스 토큰 요청
        UserProfile userProfile = authService.getUserProfile(accessToken); // 사용자 정보 요청

        MemberEntity member = authService.saveOrUpdate(userProfile); // 사용자 정보 저장 또는 업데이트

        // 세션에 사용자 정보 저장
        session.setAttribute("mem_id", member.getMId());
        session.setAttribute("mem_userid", member.getUserId());
        session.setAttribute("mem_email", member.getEmail());
        session.setAttribute("mem_username", member.getUsername());
        session.setAttribute("mem_login_type", member.getLoginType());
        session.setAttribute("mem_nickname", member.getNickname());

        return "redirect:/customer"; // 메인 페이지로 리다이렉트
    }

    @PostMapping("/gLogin") // 구글 로그인 요청 처리
    public String googleLogin() {
        String authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "response_type=code" +
                "&client_id=" + authService.getGClientId() +
                "&redirect_uri=" + authService.getGRedirectUri() +
                "&scope=email profile"; // 필요한 범위 설정

        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/google-login") // 구글 로그인 콜백 처리
    public String googleCallback(@RequestParam("code") String code, HttpSession session) throws Exception {
        String accessToken = authService.getGAccessToken(code); // 액세스 토큰 요청
        UserProfile userProfile = authService.getGUserProfile(accessToken); // 사용자 정보 요청

        MemberEntity member = authService.GsaveOrUpdate(userProfile); // 사용자 정보 저장 또는 업데이트

        // 세션에 사용자 정보 저장
        session.setAttribute("mem_id", member.getMId());
        session.setAttribute("mem_userid", member.getUserId());
        session.setAttribute("mem_email", member.getEmail());
        session.setAttribute("mem_username", member.getUsername());
        session.setAttribute("mem_login_type", member.getLoginType());
        session.setAttribute("mem_nickname", member.getNickname());

        return "redirect:/customer"; // 메인 페이지로 리다이렉트
    }

    @PostMapping("/kLogin") // 카카오 로그인 요청 처리
    public String kakaoLogin() {
        String authorizationUrl = "https://kauth.kakao.com/oauth/authorize?" +
                "response_type=code" +
                "&client_id=" + authService.getKClientId() +
                "&redirect_uri=" + authService.getKRedirectUri() +
                "&scope=profile_nickname,account_email,gender,birthday,phone_number";
        return "redirect:" + authorizationUrl;

    }

    @GetMapping("/kakao-login") // 카카오 로그인 콜백 처리
    public String kakaoCallback(@RequestParam("code") String code, HttpSession session) throws Exception {
        String accessToken = authService.getKAccessToken(code); // 액세스 토큰 요청
        UserProfile userProfile = authService.getKUserProfile(accessToken); // 사용자 정보 요청

        MemberEntity member = authService.KsaveOrUpdate(userProfile); // 사용자 정보 저장 또는 업데이트

        // 세션에 사용자 정보 저장
        session.setAttribute("mem_id", member.getMId());
        session.setAttribute("mem_userid", member.getUserId());
        session.setAttribute("mem_email", member.getEmail());
        session.setAttribute("mem_username", member.getUsername());
        session.setAttribute("mem_login_type", member.getLoginType());
        session.setAttribute("mem_nickname", member.getNickname());

        return "redirect:/customer"; // 메인 페이지로 리다이렉트

    }
}
