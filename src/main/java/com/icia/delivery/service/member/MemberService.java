package com.icia.delivery.service.member;

import com.icia.delivery.dao.member.LoginHistoryRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.service.IpService;
import com.icia.delivery.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final HttpServletRequest request; // HttpServletRequest 주입
    private final MemberRepository mrepo; // MemberRepository 주입
    private final IpService ipService; // IpService 주입
    private final BCryptPasswordEncoder pwEnc = new BCryptPasswordEncoder(); // 비밀번호 암호화
    private final HttpSession session; // 세션 객체 주입
    private final LoginHistoryRepository lrepo; // HisLoginHistoryRepository 주입
    private final LoginHistoryService loginHistoryService; // HisLoginHistoryService 주입
    private final DeliveryAddressService deliveryAddressService;

    /**
     * 회원 정보를 등록하는 메서드
     *
     * @param memberDTO 회원 정보 DTO
     * @return ModelAndView 객체를 반환
     */
    public ModelAndView registerMember(MemberDTO memberDTO) {
        ModelAndView mav = new ModelAndView();
        try {
            // 중복 확인: 아이디와 이메일 개별 체크
            if (mrepo.existsByUserId(memberDTO.getUserId())) {
                mav.setViewName("redirect:/mJoinForm");
                mav.addObject("error", "이미 사용 중인 아이디입니다.");
                return mav;
            }

            if (mrepo.existsByEmail(memberDTO.getEmail())) {
                mav.setViewName("redirect:/mJoinForm");
                mav.addObject("error", "이미 사용 중인 이메일입니다.");
                return mav;
            }

            // 비밀번호 암호화
            memberDTO.setPassword(pwEnc.encode(memberDTO.getPassword()));

            // 기본값 설정
            memberDTO.setGrade(Optional.ofNullable(memberDTO.getGrade()).orElse("REGULAR"));
            memberDTO.setStatus(Optional.ofNullable(memberDTO.getStatus()).orElse("A"));
            memberDTO.setPoint(Optional.ofNullable(memberDTO.getPoint()).orElse(0L));
            memberDTO.setRegisterDate(Optional.ofNullable(memberDTO.getRegisterDate()).orElse(LocalDateTime.now()));

            // 필수값 강제 설정
            memberDTO.setOpenProfile("승인");
            memberDTO.setReceiveEmail("승인");
            memberDTO.setReceiveNotify("승인");
            memberDTO.setLoginType("LOCAL"); // 기본 로그인 유형 설정

            // IP 설정
            if (memberDTO.getRegisterIp() == null || memberDTO.getRegisterIp().isEmpty()) {
                String clientIp = ipService.getPublicIp();
                memberDTO.setRegisterIp(clientIp);
            }

            // DTO -> Entity 변환 및 저장
            MemberEntity member = mrepo.save(MemberEntity.toEntity(memberDTO)); // 저장 후 반환값을 member 변수에 할당

            // 메인 배송지 추가
            deliveryAddressService.addMainAddress(member.getMId(), memberDTO.getAddress());

            session.removeAttribute("mem_address");

            // 성공 시 메인 페이지로 리다이렉트
            mav.setViewName("redirect:/customer");
        } catch (Exception e) {
            // 실패 시 회원가입 폼으로 리다이렉트
            mav.setViewName("redirect:/mJoinForm");
            mav.addObject("error", "회원가입 처리 중 오류가 발생했습니다.");
            e.printStackTrace(); // 오류 로그 출력
        }
        return mav;
    }


    /**
     * 사용자 ID가 존재하는지 확인하는 메서드
     *
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    public boolean isUserIdExists(String userId) {
        return mrepo.existsByUserId(userId);
    }

    /**
     * 회원 로그인을 처리하는 메서드
     *
     * @param member 로그인 정보 DTO
     * @return ModelAndView 객체를 반환
     */
    public ModelAndView mLogin(MemberDTO member) {
        ModelAndView mav = new ModelAndView();

        // 아이디로 회원 정보 조회
        Optional<MemberEntity> entityOpt = mrepo.findByUserId(member.getUserId());

        if (entityOpt.isPresent()) {
            MemberEntity entity = entityOpt.get();

            // 비밀번호 검증
            if (pwEnc.matches(member.getPassword(), entity.getPassword())) {
                // 세션에 로그인 정보 저장
                session.setAttribute("mem_id", entity.getMId());
                session.setAttribute("mem_userid", entity.getUserId());
                session.setAttribute("mem_email", entity.getEmail());
                session.setAttribute("mem_username", entity.getUsername());
                session.setAttribute("mem_address", entity.getAddress());
                session.setAttribute("mem_nickname", entity.getNickname());
                session.setAttribute("mem_grade", entity.getGrade());
                session.setAttribute("mem_status", entity.getStatus());
                session.setAttribute("mem_point", entity.getPoint());

                System.out.println("로그인 후 세션 mem_id: " + session.getAttribute("mem_id"));

                // 로그인 성공 시 마지막 로그인 정보 업데이트
                entity.setLastLoginDate(LocalDateTime.now());
                entity.setLastLoginIp(ipService.getPublicIp());
                mrepo.save(entity); // 업데이트된 정보 저장

                // 로그인 기록 저장
                try {
                    String deviceOs = UserAgentUtil.getDeviceOs(request);
                    String browser = UserAgentUtil.getBrowser(request);
                    String clientIp = ipService.getPublicIp();

                    LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
                    loginHistoryDTO.setHisMid(entity.getMId()); // 회원 ID
                    loginHistoryDTO.setHisIpAddress(clientIp); // 클라이언트 IP
                    loginHistoryDTO.setHisDeviceOs(deviceOs); // OS 정보
                    loginHistoryDTO.setHisBrowser(browser); // 브라우저 정보
                    loginHistoryDTO.setHisLoginDate(LocalDateTime.now()); // 로그인 날짜

                    loginHistoryService.saveLoginHistory(loginHistoryDTO); // 로그인 내역 저장
                } catch (Exception e) {
                    e.printStackTrace(); // 오류 로그 출력
                }

                mav.setViewName("redirect:/customer"); // 로그인 성공 시 메인 페이지로 이동
                return mav;
            }
        }

        // 로그인 실패 시 처리
        mav.setViewName("redirect:/loginForm");
        mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return mav;
    }

    /**
     * 회원 번호(mId)를 기반으로 사용자 이름을 가져오는 메서드
     *
     * @param mId 회원 고유 번호
     * @return 사용자 이름
     */
    public String getUserNameById(Long mId) {
        Optional<MemberEntity> entity = mrepo.findById(mId);
        return entity.map(MemberEntity::getUsername).orElse(null);
    }

    /**
     * 회원 번호(mId)를 기반으로 마이페이지를 구성하는 메서드
     *
     * @param mId 회원 고유 번호
     * @return ModelAndView 객체를 반환
     */
    public ModelAndView mView(Long mId) {
        ModelAndView mav = new ModelAndView();

        // 데이터베이스에서 회원 번호(mId)로 회원 정보를 조회
        Optional<MemberEntity> entity = mrepo.findById(mId);

        // 회원 정보가 존재하는 경우
        if (entity.isPresent()) {
            // Entity를 DTO로 변환
            MemberDTO member = MemberDTO.toDTO(entity.get());

            // 세션에 사용자 정보 저장
            session.setAttribute("mem_id", member.getMId());
            session.setAttribute("mem_userid", member.getUserId());

            // 사용자 정보를 뷰에 추가
            mav.addObject("view", member);

            // 마이 페이지 뷰 설정
            mav.setViewName("/member/myPage");
        } else {
            // 회원 정보가 없으면 메인 페이지로 리다이렉트
            mav.setViewName("redirect:/index");
        }

        // ModelAndView 객체 반환
        return mav;
    }

    /**
     * 회원 정보를 수정하는 메서드
     *
     * @param mId       회원 고유 번호
     * @param memberDTO 업데이트할 회원 정보 DTO
     * @return ModelAndView 객체를 반환
     */
    @Transactional
    public ModelAndView updateMemberModal(Long mId, MemberDTO memberDTO) {
        ModelAndView mav = new ModelAndView(); // 응답 데이터를 담을 객체 생성
        try {
            // 데이터베이스에서 회원 번호(mId)를 기준으로 회원 정보 조회
            Optional<MemberEntity> optionalMember = mrepo.findById(mId);

            // 사용자가 존재하지 않을 경우 처리
            if (optionalMember.isEmpty()) {
                mav.addObject("status", "error");
                mav.addObject("message", "사용자를 찾을 수 없습니다.");
                mav.setViewName("/member/myPage"); // 적절한 뷰로 설정
                return mav;
            }

            // 사용자가 존재하면 정보 업데이트
            MemberEntity member = optionalMember.get();

            // DTO로 전달된 데이터 중 null이 아닌 필드만 업데이트
            if (memberDTO.getNickname() != null) member.setNickname(memberDTO.getNickname());
            if (memberDTO.getPhone() != null) member.setPhone(memberDTO.getPhone());
            if (memberDTO.getEmail() != null) member.setEmail(memberDTO.getEmail());
            if (memberDTO.getBirthday() != null) member.setBirthday(memberDTO.getBirthday());
            if (memberDTO.getGender() != null) member.setGender(memberDTO.getGender());

            // 변경된 정보를 데이터베이스에 저장
            mrepo.save(member);

            // 성공 메시지 및 업데이트된 회원 정보 설정
            mav.addObject("status", "success");
            mav.addObject("message", "회원정보가 성공적으로 수정되었습니다.");
            mav.addObject("member", MemberDTO.toDTO(member));

            // 마이 페이지 뷰 설정
            mav.setViewName("/member/myPage");

        } catch (Exception e) {
            // 오류 발생 시 처리
            mav.addObject("status", "error");
            mav.addObject("message", "회원정보 수정 중 오류가 발생했습니다.");
            e.printStackTrace();
            mav.setViewName("/member/myPage"); // 오류 시 동일 페이지로 이동
        }
        return mav;
    }

    /**
     * 사용자 장바구니를 처리하는 메서드 (구현 필요)
     *
     * @param mId 회원 고유 번호
     * @return ModelAndView 객체를 반환
     */
    public ModelAndView cart(Long mId) {
        ModelAndView mav = new ModelAndView();
        // 여기에 사용자 장바구니 로직을 추가해야 함
        // 예시: 장바구니 정보 추가
        // mav.addObject("cart", cartService.getCartByMemberId(mId));
        mav.setViewName("/member/cart");
        return mav;
    }

    /**
     * 회원 탈퇴를 처리하는 메서드
     *
     * @param memberDTO 회원 정보 DTO
     * @return 성공 메시지
     */
    @Transactional
    public String delete(MemberDTO memberDTO) {
        // 회원 번호(mId)를 기준으로 데이터베이스에서 회원 정보 조회
        Optional<MemberEntity> mentity = mrepo.findById(memberDTO.getMId());

        // 사용자가 존재하지 않으면 예외 발생
        if (mentity.isEmpty()) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        try {
            // 회원의 고유 ID(mId)를 가져옴
            Long mId = mentity.get().getMId();

            // 해당 회원과 관련된 기록 삭제
            lrepo.deleteById(mId);

            // 회원 정보 삭제
            mrepo.deleteById(mId);

            // 세션을 무효화하여 사용자 로그아웃 처리
            session.invalidate();

            return "회원 탈퇴가 완료되었습니다."; // 성공 메시지 반환
        } catch (Exception e) {
            // 오류 발생 시 메시지 출력 및 예외 발생
            System.err.println("회원 탈퇴 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("회원 탈퇴 처리 중 문제가 발생했습니다.");
        }
    }


    @Transactional
    public String getAddressByMemId(Long memId) {
        Long memberId = Optional.ofNullable(memId)
                .orElseThrow(() -> new IllegalArgumentException("🚨 회원 ID가 존재하지 않습니다."));

        return Optional.ofNullable(mrepo.findAddressByMemberId(memberId))
                .orElse("주소 정보 없음");
    }
}
