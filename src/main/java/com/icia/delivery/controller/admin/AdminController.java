package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.admin.AdminEntity;
import com.icia.delivery.service.admin.AdminService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Admin 로그인 폼을 보여주는 메서드
     *
     * @return 로그인 폼 뷰 이름
     */
    @GetMapping("/admin")
    public String showAdminLoginForm() {
        return "admin/admin-login"; // Path to admin-login.html in templates
    }

    /**
     * Admin 로그인을 처리하는 메서드
     *
     * @param username 입력된 사용자 이름
     * @param password 입력된 비밀번호
     * @param session  HttpSession 객체
     * @return 로그인 성공 시 대시보드로 리다이렉트, 실패 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/admin/login")
    public String loginAdmin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session) {
        boolean isAuthenticated = adminService.login(username, password, session);
        if (isAuthenticated) {
            return "redirect:/admin/dashboard"; // 성공 시 admin.html로 리다이렉트
        } else {
            return "redirect:/admin?error=true"; // 실패 시 URL에 error 파라미터 추가
        }
    }

    /**
     * Admin 대시보드를 보여주는 메서드
     *
     * @param session HttpSession 객체
     * @return 대시보드 뷰 이름 또는 로그인 페이지로 리다이렉트
     */
    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session) {
        AdminEntity admin = (AdminEntity) session.getAttribute("admin");
        if (admin != null) {
            return "admin/admin";
        } else {
            return "redirect:/admin?error=true"; // 세션 없으면 로그인 페이지로 리다이렉트
        }
    }

    /**
     * Admin 로그아웃을 처리하는 메서드
     *
     * @param session HttpSession 객체
     * @return 로그아웃 후 로그인 페이지로 리다이렉트
     */
    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session) {
        adminService.logout(session);
        return "redirect:/admin?logout=true"; // 로그아웃 후 로그인 페이지로 리다이렉트
    }
}
