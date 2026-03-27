package com.icia.delivery.service.president;

import com.icia.delivery.dao.member.LoginHistoryRepository;
import com.icia.delivery.dao.president.PreMemRepository;
import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.president.PreMemberDTO;
import com.icia.delivery.dto.president.PreMemberEntity;
import com.icia.delivery.service.IpService;
import com.icia.delivery.service.member.DeliveryAddressService;
import com.icia.delivery.service.member.LoginHistoryService;
import com.icia.delivery.util.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreMemService {

    private final HttpServletRequest request; // HttpServletRequest 주입

    private final PreMemRepository pmrepo;

    private final IpService ipService; // IpService 주입
    private final BCryptPasswordEncoder pwEnc = new BCryptPasswordEncoder(); // 비밀번호 암호화
    private final HttpSession session; // 세션 객체 주입
    private final LoginHistoryRepository lrepo; // HisLoginHistoryRepository 주입
    private final LoginHistoryService loginHistoryService; // HisLoginHistoryService 주입
    private final DeliveryAddressService deliveryAddressService;

    Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/upload/BusinessLicense");


    public ModelAndView storeForm(PreMemberDTO preMemDTO) {
        ModelAndView mav = new ModelAndView();
        try {
            // 중복 확인: 아이디와 이메일 개별 체크
            if (pmrepo.existsByPreMemUserId(preMemDTO.getPreMemUserId())) {
                mav.setViewName("redirect:/pJoinForm");
                mav.addObject("error", "이미 사용 중인 아이디입니다.");
                return mav;
            }

            if (pmrepo.existsByPreMemEmail(preMemDTO.getPreMemEmail())) {
                mav.setViewName("redirect:/pJoinForm");
                mav.addObject("error", "이미 사용 중인 이메일입니다.");
                return mav;
            }

            // 비밀번호 암호화
            preMemDTO.setPreMemPassword(pwEnc.encode(preMemDTO.getPreMemPassword()));

            // 기본값 설정
            // preMemDTO.setPreMemStatus(Optional.ofNullable(preMemDTO.getPreMemStatus()).orElse("A"));
            // preMemDTO.setPreMemApprovalStatus(Optional.ofNullable(preMemDTO.getPreMemApprovalStatus()).orElse("A"));


            // 필수값 강제 설정
            preMemDTO.setPreMemStatus("정상");
            preMemDTO.setPreMemCreatedAt(LocalDateTime.now());
            preMemDTO.setPreMemApprovalStatus("보류");

            // 업로드된 사진 파일 이름에 시퀀스를 추가하여 변경
            // String originalFileName = preMemDTO.getPreMemBizLicensePhoto();
            // Long sequence = getNextFileSequence(); // 시퀀스를 가져옵니다.
            // String newFileName = "pre_mem_" + sequence + "_" + originalFileName;
            // preMemDTO.setPreMemBizLicensePhoto(newFileName);

            // 파일 가져오기
            MultipartFile pFile = preMemDTO.getPFile();
            String savePath = "";

            if (!pFile.isEmpty()) {
                String fileName = pFile.getOriginalFilename();
                Long sequence = getNextFileSequence(); // 시퀀스를 가져옵니다.
                String newFileName = "pre_mem_" + sequence + "_" + fileName;

                preMemDTO.setPreMemBizLicensePhoto(newFileName);

                savePath = path + "\\" + newFileName;
            } else {
                preMemDTO.setPreMemBizLicensePhoto("default.jpg");
            }

            // DTO -> Entity 변환 및 저장
            pmrepo.save(PreMemberEntity.toEntity(preMemDTO));
            pFile.transferTo(new File(savePath));

            // 메인 배송지 추가
            // deliveryAddressService.addMainAddress(preMember.get(), memberDTO.getAddress());

            // 성공 시 메인 페이지로 리다이렉트
            mav.setViewName("redirect:/pLoginForm");
        } catch (Exception e) {
            // 실패 시 회원가입 폼으로 리다이렉트
            mav.setViewName("redirect:/pJoinForm");
            mav.addObject("error", "회원가입 처리 중 오류가 발생했습니다.");
            e.printStackTrace(); // 오류 로그 출력
        }
        return mav;
    }

    // 시퀀스를 구현하기 위한 메소드
    public Long getNextFileSequence() {
        return pmrepo.findMaxPreMemId() + 1;
    }

    public ModelAndView pLogin(PreMemberDTO preMemDTO) {
        ModelAndView mav = new ModelAndView();

        // 아이디로 회원 정보 조회
        Optional<PreMemberEntity> entityOpt = pmrepo.findByPreMemUserId(preMemDTO.getPreMemUserId());

        if (entityOpt.isPresent()) {
            PreMemberEntity entity = entityOpt.get();

            // 비밀번호 검증
            if (pwEnc.matches(preMemDTO.getPreMemPassword(), entity.getPreMemPassword())) {
                // 세션에 로그인 정보 저장
                session.setAttribute("preMem_id", entity.getPreMemId());
                session.setAttribute("preMem_userid", entity.getPreMemUserId());
                session.setAttribute("preMem_email", entity.getPreMemEmail());
                session.setAttribute("preMem_username", entity.getPreMemCeoName());
                //session.setAttribute("mem_address", entity.getAddress());
                //session.setAttribute("mem_nickname", entity.getNickname());
                //session.setAttribute("mem_grade", entity.getGrade());
                //session.setAttribute("mem_status", entity.getStatus());

                // 로그인 성공 시 마지막 로그인 정보 업데이트
                // entity.setLastLoginDate(LocalDateTime.now());
                // entity.setLastLoginIp(ipService.getPublicIp());
                // mrepo.save(entity); // 업데이트된 정보 저장

                // 로그인 기록 저장
                try {
                    String deviceOs = UserAgentUtil.getDeviceOs(request);
                    String browser = UserAgentUtil.getBrowser(request);
                    String clientIp = ipService.getPublicIp();

                    LoginHistoryDTO loginHistoryDTO = new LoginHistoryDTO();
                    loginHistoryDTO.setHisMid(entity.getPreMemId()); // 회원 ID
                    loginHistoryDTO.setHisIpAddress(clientIp); // 클라이언트 IP
                    loginHistoryDTO.setHisDeviceOs(deviceOs); // OS 정보
                    loginHistoryDTO.setHisBrowser(browser); // 브라우저 정보
                    loginHistoryDTO.setHisLoginDate(LocalDateTime.now()); // 로그인 날짜

                    loginHistoryService.saveLoginHistory(loginHistoryDTO); // 로그인 내역 저장
                } catch (Exception e) {
                    e.printStackTrace(); // 오류 로그 출력
                }

                mav.setViewName("redirect:/president"); // 로그인 성공 시 메인 페이지로 이동
                return mav;
            }
        }

        // 로그인 실패 시 처리
        mav.setViewName("redirect:/pLoginForm");
        mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return mav;
    }
}
