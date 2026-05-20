package com.icia.delivery.service.admin;

import com.icia.delivery.dao.admin.AdminRepository;
import com.icia.delivery.dto.admin.AdminDTO;
import com.icia.delivery.dto.admin.AdminEntity;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    /**
     * 로그인 처리 메서드
     *
     * @param username 로그인 시 입력된 사용자 이름
     * @param password 로그인 시 입력된 비밀번호
     * @param session  HttpSession 객체
     * @return 로그인 성공 여부
     */
    public boolean login(String username, String password, HttpSession session) {
        Optional<AdminEntity> optionalAdmin = adminRepository.findByAdminUsername(username);
        if (optionalAdmin.isPresent()) {
            AdminEntity admin = optionalAdmin.get();
            // 비밀번호 비교 (암호화 없이)
            if (admin.getAdminPassword().equals(password)) {
                // 세션에 관리자 정보 저장
                session.setAttribute("admin", admin);
                session.setAttribute("admin_id", admin.getAdminId());
                session.setAttribute("admin_username", admin.getAdminUsername());
                session.setAttribute("admin_role", admin.getAdminRole());
                // 마지막 로그인 시간 업데이트
                admin.setAdminLastLogin(LocalDateTime.now());
                adminRepository.save(admin);
                return true;
            }
        }
        return false;
    }

    /**
     * 로그아웃 처리 메서드
     *
     * @param session HttpSession 객체
     */
    public void logout(HttpSession session) {
        session.invalidate();
    }

    public ModelAndView adminView(Long adminid) {

        ModelAndView mav = new ModelAndView();

        // 데이터베이스에서 회원 번호(mId)로 회원 정보를 조회
        Optional<AdminEntity> entity = adminRepository.findById(adminid);
        if (entity.isPresent()) {
            // Entity를 DTO로 변환
            AdminDTO admin = AdminDTO.toDTO(entity.get());

            mav.addObject("admin", admin);


            mav.setViewName("/admin/admin-info");
            mav.setViewName("/admin/admin");
        }
        return mav;
    }
}
