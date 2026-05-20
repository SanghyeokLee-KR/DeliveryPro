package com.icia.delivery.service.rider;

import com.icia.delivery.dao.rider.RiderAccounRepository;
import com.icia.delivery.dao.rider.RiderRepository;
import com.icia.delivery.dto.rider.RiderAccountDTO;
import com.icia.delivery.dto.rider.RiderAccountEntity;
import com.icia.delivery.dto.rider.RiderDTO;
import com.icia.delivery.dto.rider.RiderEntity;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final BCryptPasswordEncoder pwEnc = new BCryptPasswordEncoder(); // 비밀번호 암호화
    private final HttpSession session; // 세션 객체 주입

    private final RiderRepository riderRepository;
    private final RiderAccounRepository rarepo;

    public ModelAndView riderRegister(RiderDTO riderDTO) {
        ModelAndView mav = new ModelAndView();

        try {
            // 중복 확인: 아이디와 이메일 개별 체크
            if (riderRepository.existsByRiderId(riderDTO.getRiderId())) {
                mav.setViewName("redirect:/r_JoinForm");
                mav.addObject("error", "이미 사용 중인 아이디입니다.");
                return mav;
            }

            // 비밀번호 암호화
            riderDTO.setRiderPw(pwEnc.encode(riderDTO.getRiderPw()));

            // 기본값 설정
            riderDTO.setVehicleType(Optional.ofNullable(riderDTO.getVehicleType()).orElse("등록 안됨"));
            riderDTO.setTotalDeliveries(0);
            riderDTO.setRiderCreatedAt(Optional.ofNullable(riderDTO.getRiderCreatedAt()).orElse(LocalDateTime.now()));

            // 필수값 강제 설정
            riderDTO.setIsAvailable("보류");



            // DTO -> Entity 변환 및 저장
            riderRepository.save(RiderEntity.toEntity(riderDTO)); // 저장 후

            // 성공 시 메인 페이지로 리다이렉트
            mav.setViewName("redirect:/rLoginForm");
        } catch (Exception e) {
            // 실패 시 회원가입 폼으로 리다이렉트
            mav.setViewName("redirect:/r_JoinForm");
            mav.addObject("error", "회원가입 처리 중 오류가 발생했습니다.");
            e.printStackTrace(); // 오류 로그 출력
        }


        return mav;
    }

    public ModelAndView rLogin(RiderDTO riderDTO) {
        ModelAndView mav = new ModelAndView();

        // 아이디로 회원 정보 조회
        Optional<RiderEntity> entityOpt = riderRepository.findByRiderId(riderDTO.getRiderId());

        if (entityOpt.isPresent()) {
            RiderEntity entity = entityOpt.get();

            // 비밀번호 검증
            if (pwEnc.matches(riderDTO.getRiderPw(), entity.getRiderPw())) {
                // 세션에 로그인 정보 저장
                session.setAttribute("rider_no", entity.getRiderNo());
                session.setAttribute("rider_id", entity.getRiderId());
                session.setAttribute("rider_name", entity.getRiderName());

                System.out.println("로그인 후 세션 rider_no: " + session.getAttribute("rider_no"));

                mav.setViewName("redirect:/rider"); // 로그인 성공 시 메인 페이지로 이동
                return mav;
            }
        }

        // 로그인 실패 시 처리
        mav.setViewName("redirect:/rLoginForm");
        mav.addObject("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return mav;
    }

    public ModelAndView rView(Long rNo) {

        ModelAndView mav = new ModelAndView();

        // 데이터베이스에서 회원 번호(mId)로 회원 정보를 조회
        Optional<RiderEntity> entity = riderRepository.findById(rNo);

        // 회원 정보가 존재하는 경우
        if (entity.isPresent()) {
            // Entity를 DTO로 변환
            RiderDTO rider = RiderDTO.toDTO(entity.get());

            // 세션에 사용자 정보 저장
            session.setAttribute("rider_no", rider.getRiderNo());
            session.setAttribute("rider_id", rider.getRiderId());
            session.setAttribute("rider_Name", rider.getRiderName());
            session.setAttribute("rider_Phone", rider.getRiderPhone());
            session.setAttribute("rider_vehicleType", rider.getVehicleType());
            session.setAttribute("rider_gender", rider.getRiderGender());
            session.setAttribute("rider_birth", rider.getRiderBirth());
            session.setAttribute("rider_total_deliveries", rider.getTotalDeliveries());
            session.setAttribute("rider_isAvailable", rider.getIsAvailable());

            // 사용자 정보를 뷰에 추가
            mav.addObject("rider", rider);

            // 마이 페이지 뷰 설정
            mav.setViewName("/rider/management/riderInfoPage");
        } else {
            // 회원 정보가 없으면 메인 페이지로 리다이렉트
            mav.setViewName("redirect:/rider");
        }

        // ModelAndView 객체 반환
        return mav;

    }

    public String riderIdCheck(String rId) {
        String result = "";
        Optional<RiderEntity> entity = riderRepository.findByRiderId(rId);

        if(entity.isPresent()) {
            result = "NO";
        } else {
            result = "OK";
        }
        return result;
    }

    public ModelAndView addRiderAccount(RiderAccountDTO accDTO) {
        ModelAndView mav = new ModelAndView();

        try {
            // 계좌의 필수 값 체크
            if (accDTO.getRiderBankName() == null || accDTO.getRiderBankName().isEmpty()) {
                throw new IllegalArgumentException("은행명을 입력해주세요.");
            }

            if (accDTO.getRiderAccountNumber() == null || accDTO.getRiderAccountNumber().isEmpty()) {
                throw new IllegalArgumentException("계좌번호를 입력해주세요.");
            }

            // DTO -> Entity 변환 및 저장
            rarepo.save(RiderAccountEntity.toEntity(accDTO)); // 저장


            // addMenu가 실행되었을 때, 세션에 플래그 설정
            // addMenu 값을 모델에 전달
            mav.setViewName("redirect:/riderAccount");
        } catch (IllegalArgumentException e) {
            // 필수 값이 누락되었을 때 처리
            mav.setViewName("redirect:/riderAccount");
            mav.addObject("error", e.getMessage()); // 에러 메시지 전달
            e.printStackTrace();
        } catch (Exception e) {
            // 예외 처리 (기타 예외)
            mav.setViewName("redirect:/index");
            mav.addObject("error", "회원가입 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return mav;
    }

    public List<RiderAccountDTO> getRiderAccountList() {

        List<RiderAccountDTO> dto = new ArrayList<>();

        // 세션에서 preMem_id 값을 가져옵니다
        Long riderNo = (Long) session.getAttribute("rider_no");

        List<RiderAccountEntity> riderAccEntity = rarepo.findByRiderNo(riderNo);
        System.out.println("라이더 계좌 등록 리스트 : " + riderAccEntity);


        for(RiderAccountEntity entity : riderAccEntity){
            dto.add(RiderAccountDTO.toDTO(entity));
        }

        return dto;
    }

    public String deleteAccount(Long accountId) {
        Optional<RiderAccountEntity> entity = rarepo.findById(accountId);

        if(entity.isPresent()){
            rarepo.deleteById(accountId); return "성공!";
        } else { return "실패!"; }
    }

    public boolean approve(Long id) {
        int result = riderRepository.updateRiderStatusApprove(id);
        return result > 0;  // 수정된 행이 있으면 true 반환
    }
}
