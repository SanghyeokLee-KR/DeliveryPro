package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService msvc;
    private final HttpSession session;

    // 회원가입 폼 렌더링
    @GetMapping("/join")
    public String showRegistrationForm() {
        return "member/join";
    }

    @PostMapping("/mLogin")
    public ModelAndView mLogin(@ModelAttribute MemberDTO member) {
        System.out.println("\n로그인 메소드\n[1] html → controller : " + member);
        return msvc.mLogin(member);
    }

    @PostMapping("/register")
    public ModelAndView registerMember(@ModelAttribute MemberDTO memberDTO) {
        return msvc.registerMember(memberDTO);
    }

    // mLogout : 로그아웃
    @GetMapping("/mLogout")
    public String mLogout(HttpSession session) {
        if (session != null) {
            // 로그아웃 시 제거할 세션 속성들을 지정합니다.
            session.removeAttribute("mem_id");
            session.removeAttribute("mem_userid");
            session.removeAttribute("mem_email");
            session.removeAttribute("mem_username");
            session.removeAttribute("mem_address");
            session.removeAttribute("mem_nickname");
            session.removeAttribute("mem_grade");
            session.removeAttribute("mem_status");
            session.removeAttribute("mem_point");
        }
        // 로그아웃 후 이동할 페이지 반환
        return "customer-main";
    }

    @GetMapping("/myPage/{mId}")
    public ModelAndView mView(@PathVariable("mId") Long mId) {
        // 서비스 클래스의 mView 메서드를 호출하여 ModelAndView 반환
        return msvc.mView(mId);
    }
}
