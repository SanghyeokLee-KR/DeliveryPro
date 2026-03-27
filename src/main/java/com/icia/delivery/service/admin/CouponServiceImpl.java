package com.icia.delivery.service.admin;

import com.icia.delivery.dao.admin.CouponRepository;
import com.icia.delivery.dao.admin.CouponUsageRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.admin.CouponDTO;
import com.icia.delivery.dto.admin.CouponEntity;
import com.icia.delivery.dto.admin.CouponUsageEntity;
import com.icia.delivery.dto.member.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final MemberRepository memberRepository;

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    // -----------------------------------------------------------------------------------
    // 관리자 측 기능 구현
    // -----------------------------------------------------------------------------------

    /**
     * 쿠폰 등록
     */
    @Override
    @Transactional
    public void registerCoupon(CouponDTO couponDTO) throws Exception {
        try {
            // DTO를 Entity로 변환
            CouponEntity coupon = new CouponEntity();
            coupon.setCode(couponDTO.getCode());
            coupon.setName(couponDTO.getName());
            coupon.setContent(couponDTO.getContent());
            coupon.setDeductPrice(couponDTO.getDeductPrice());
            coupon.setMinPrice(couponDTO.getMinPrice());

            // LocalDate를 LocalDateTime으로 변환 (시간을 23:59:59로 설정)
            if (couponDTO.getExpiredDate() != null) {
                coupon.setExpiredDate(couponDTO.getExpiredDate().atTime(23, 59, 59));
            } else {
                throw new Exception("만료일이 필요합니다.");
            }

            coupon.setStatus(couponDTO.getStatus());
            coupon.setOrderType(couponDTO.getOrderType());

            logger.info("등록하려는 쿠폰 코드: {}", coupon.getCode());
            logger.info("Saving coupon 확인용: {}", coupon);

            // 쿠폰 코드 중복 확인
            if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
                logger.warn("이미 존재하는 쿠폰 코드입니다: {}", coupon.getCode());
                throw new Exception("이미 존재하는 쿠폰 코드입니다.");
            }

            // 쿠폰 저장 (@PrePersist에서 createdDate와 modifiedDate가 자동 설정됨)
            couponRepository.save(coupon);
            logger.info("쿠폰이 성공적으로 등록되었습니다: {}", coupon.getId());
        } catch (Exception e) {
            logger.error("쿠폰 등록 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 쿠폰 수정
     */
    @Override
    @Transactional
    public void updateCoupon(CouponDTO couponDTO) throws Exception {
        try {
            logger.info("수정하려는 쿠폰 ID: {}", couponDTO.getId());

            // 기존 쿠폰 조회
            CouponEntity existingCoupon = couponRepository.findById(couponDTO.getId())
                    .orElseThrow(() -> {
                        logger.warn("수정할 쿠폰을 찾을 수 없습니다. ID: {}", couponDTO.getId());
                        return new RuntimeException("수정할 쿠폰을 찾을 수 없습니다.");
                    });

            // 쿠폰 코드 중복 확인 (수정 시)
            Optional<CouponEntity> duplicateCoupon = couponRepository.findByCode(couponDTO.getCode());
            if (duplicateCoupon.isPresent() && !duplicateCoupon.get().getId().equals(couponDTO.getId())) {
                logger.warn("이미 존재하는 쿠폰 코드입니다: {}", couponDTO.getCode());
                throw new Exception("이미 존재하는 쿠폰 코드입니다.");
            }

            // 필드 업데이트
            existingCoupon.setCode(couponDTO.getCode());
            existingCoupon.setName(couponDTO.getName());
            existingCoupon.setContent(couponDTO.getContent());
            existingCoupon.setDeductPrice(couponDTO.getDeductPrice());
            existingCoupon.setMinPrice(couponDTO.getMinPrice());

            // LocalDate를 LocalDateTime으로 변환 (시간을 23:59:59로 설정)
            if (couponDTO.getExpiredDate() != null) {
                existingCoupon.setExpiredDate(couponDTO.getExpiredDate().atTime(23, 59, 59));
            } else {
                throw new Exception("만료일이 필요합니다.");
            }

            existingCoupon.setStatus(couponDTO.getStatus());
            existingCoupon.setOrderType(couponDTO.getOrderType());

            // 쿠폰 상태 업데이트 (필요 시)
            if ("Y".equals(existingCoupon.getStatus())) {
                // 추가 로직이 필요하면 여기에 작성
            }

            // 변경된 쿠폰 저장 (@PreUpdate에서 modifiedDate가 자동 설정됨)
            couponRepository.save(existingCoupon);
            logger.info("쿠폰이 성공적으로 수정되었습니다: {}", existingCoupon.getId());
        } catch (Exception e) {
            logger.error("쿠폰 수정 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 쿠폰 삭제
     */
    @Override
    @Transactional
    public void deleteCoupon(Long couponId) throws Exception {
        try {
            logger.info("삭제하려는 쿠폰 ID: {}", couponId);

            // 쿠폰 존재 여부 확인
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> {
                        logger.warn("삭제할 쿠폰을 찾을 수 없습니다. ID: {}", couponId);
                        return new RuntimeException("삭제할 쿠폰을 찾을 수 없습니다.");
                    });

            // 쿠폰 삭제
            couponRepository.delete(coupon);
            logger.info("쿠폰이 성공적으로 삭제되었습니다: {}", couponId);
        } catch (Exception e) {
            logger.error("쿠폰 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 모든 쿠폰 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CouponEntity> getAllCoupons() {
        try {
            logger.info("모든 쿠폰을 조회합니다.");
            List<CouponEntity> coupons = couponRepository.findAll();
            logger.info("조회된 쿠폰 수: {}", coupons.size());
            return coupons;
        } catch (Exception e) {
            logger.error("모든 쿠폰 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 쿠폰 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CouponEntity> getCouponById(Long couponId) {
        try {
            logger.info("특정 쿠폰을 조회합니다. ID: {}", couponId);
            Optional<CouponEntity> coupon = couponRepository.findById(couponId);
            if (coupon.isPresent()) {
                logger.info("쿠폰을 성공적으로 조회했습니다: {}", couponId);
            } else {
                logger.warn("쿠폰을 찾을 수 없습니다: {}", couponId);
            }
            return coupon;
        } catch (Exception e) {
            logger.error("특정 쿠폰 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 쿠폰 사용 (관리자 측)
     *
     * @param couponId 사용하려는 쿠폰의 ID
     */
    @Override
    @Transactional
    public void useCoupon(Long couponId) throws Exception {
        try {
            logger.info("관리자 측에서 쿠폰을 사용합니다. ID: {}", couponId);

            // 쿠폰 존재 여부 확인
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> {
                        logger.warn("쿠폰을 찾을 수 없습니다. ID: {}", couponId);
                        return new RuntimeException("쿠폰을 찾을 수 없습니다.");
                    });

            // 쿠폰 상태 확인
            if (!"Y".equals(coupon.getStatus())) {
                logger.warn("사용 불가능한 쿠폰 상태입니다. ID: {}, 상태: {}", couponId, coupon.getStatus());
                throw new Exception("사용 불가능한 쿠폰입니다.");
            }

            // 쿠폰 사용 기록 생성 (관리자 사용 시 member는 null)
            CouponUsageEntity usage = new CouponUsageEntity();
            usage.setCoupon(coupon);
            usage.setUsedAt(LocalDateTime.now());

            // 사용 기록 저장
            couponUsageRepository.save(usage);
            logger.info("쿠폰 사용 기록이 성공적으로 저장되었습니다. 쿠폰 ID: {}", couponId);

            // 쿠폰 상태 업데이트
            coupon.setStatus("N");
            couponRepository.save(coupon);
            logger.info("쿠폰 상태가 'N'으로 업데이트되었습니다. 쿠폰 ID: {}", couponId);
        } catch (Exception e) {
            logger.error("쿠폰 사용 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 쿠폰 사용 기록 조회 (관리자 측)
     *
     * @param couponId 조회하려는 쿠폰의 ID
     * @return 쿠폰 사용 기록 목록
     * @throws Exception 쿠폰을 찾을 수 없을 경우
     */
    @Override
    @Transactional(readOnly = true)
    public List<CouponUsageEntity> getCouponUsages(Long couponId) throws Exception {
        try {
            logger.info("쿠폰 사용 기록을 조회합니다. 쿠폰 ID: {}", couponId);

            // 쿠폰 존재 여부 확인
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> {
                        logger.warn("쿠폰을 찾을 수 없습니다. ID: {}", couponId);
                        return new RuntimeException("쿠폰을 찾을 수 없습니다.");
                    });

            // 쿠폰 사용 기록 조회
            List<CouponUsageEntity> usages = couponUsageRepository.findByCouponId(couponId);
            logger.info("조회된 사용 기록 수: {}", usages.size());
            return usages;
        } catch (Exception e) {
            logger.error("쿠폰 사용 기록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    // -----------------------------------------------------------------------------------
    // 사용자 측 기능 구현
    // -----------------------------------------------------------------------------------

    /**
     * 사용자 ID로 쿠폰 목록 조회
     *
     * @param memberId 현재 사용자의 ID
     * @return 사용자가 등록한 쿠폰 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<CouponEntity> getCouponsByMemberId(Long memberId) {
        try {
            logger.info("사용자 ID로 쿠폰 목록을 조회합니다. 사용자 ID: {}", memberId);
            List<CouponUsageEntity> usages = couponUsageRepository.findByMemberId(memberId);
            List<CouponEntity> coupons = usages.stream()
                    .map(CouponUsageEntity::getCoupon)
                    .collect(Collectors.toList());
            logger.info("조회된 쿠폰 수: {}", coupons.size());
            return coupons;
        } catch (Exception e) {
            logger.error("사용자 ID로 쿠폰 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자 쿠폰 등록
     *
     * @param code     등록하려는 쿠폰 코드
     * @param memberId 현재 사용자의 ID
     * @throws Exception 쿠폰 등록 실패 시 예외 발생
     */
    @Override
    @Transactional
    public void registerUserCoupon(String code, Long memberId) throws Exception {
        try {
            logger.info("사용자가 쿠폰을 등록하려고 합니다. 코드: {}, 사용자 ID: {}", code, memberId);

            // 쿠폰 코드로 쿠폰 조회
            CouponEntity coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> {
                        logger.warn("존재하지 않는 쿠폰 코드입니다: {}", code);
                        return new RuntimeException("존재하지 않는 쿠폰 코드입니다.");
                    });

            // 이미 사용 중인 쿠폰인지 확인
            boolean alreadyUsed = couponUsageRepository.existsByCouponIdAndMemberId(coupon.getId(), memberId);
            if (alreadyUsed) {
                logger.warn("사용자가 이미 사용한 쿠폰입니다. 쿠폰 ID: {}, 사용자 ID: {}", coupon.getId(), memberId);
                throw new Exception("이미 사용한 쿠폰입니다.");
            }

            // 회원 조회
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        logger.warn("회원 정보를 찾을 수 없습니다. 사용자 ID: {}", memberId);
                        return new RuntimeException("회원 정보를 찾을 수 없습니다.");
                    });

            // 쿠폰 사용 기록 생성
            CouponUsageEntity usage = new CouponUsageEntity();
            usage.setCoupon(coupon);
            usage.setMember(member); // 실제 회원 정보 설정
            usage.setUsedAt(LocalDateTime.now());

            // 사용 기록 저장
            couponUsageRepository.save(usage);
            logger.info("사용자가 쿠폰을 성공적으로 등록했습니다. 쿠폰 ID: {}, 사용자 ID: {}", coupon.getId(), memberId);
        } catch (Exception e) {
            logger.error("사용자 쿠폰 등록 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자 쿠폰 사용
     *
     * @param couponId 사용하려는 쿠폰의 ID
     * @param memberId 현재 사용자의 ID
     * @throws Exception 쿠폰 사용 실패 시 예외 발생
     */
    @Override
    @Transactional
    public void useCoupon(Long couponId, Long memberId) throws Exception {
        try {
            logger.info("사용자가 쿠폰을 사용하려고 합니다. 쿠폰 ID: {}, 사용자 ID: {}", couponId, memberId);

            // 쿠폰 존재 여부 확인
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> {
                        logger.warn("쿠폰을 찾을 수 없습니다. ID: {}", couponId);
                        return new RuntimeException("쿠폰을 찾을 수 없습니다.");
                    });

            // 쿠폰 상태 확인
            if (!"Y".equals(coupon.getStatus())) {
                logger.warn("사용 불가능한 쿠폰 상태입니다. ID: {}, 상태: {}", couponId, coupon.getStatus());
                throw new Exception("사용 불가능한 쿠폰입니다.");
            }

            // 이미 사용한 쿠폰인지 확인
            boolean alreadyUsed = couponUsageRepository.existsByCouponIdAndMemberId(coupon.getId(), memberId);
            if (alreadyUsed) {
                logger.warn("사용자가 이미 사용한 쿠폰입니다. 쿠폰 ID: {}, 사용자 ID: {}", couponId, memberId);
                throw new Exception("이미 사용한 쿠폰입니다.");
            }

            // 회원 조회
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        logger.warn("회원 정보를 찾을 수 없습니다. 사용자 ID: {}", memberId);
                        return new RuntimeException("회원 정보를 찾을 수 없습니다.");
                    });

            // 쿠폰 사용 기록 생성
            CouponUsageEntity usage = new CouponUsageEntity();
            usage.setCoupon(coupon);
            usage.setMember(member); // 실제 회원 정보 설정
            usage.setUsedAt(LocalDateTime.now());

            // 사용 기록 저장
            couponUsageRepository.save(usage);
            logger.info("사용자가 쿠폰을 성공적으로 사용했습니다. 쿠폰 ID: {}, 사용자 ID: {}", couponId, memberId);

            // 쿠폰 상태 업데이트 (일회용 쿠폰 가정)
            coupon.setStatus("N");
            couponRepository.save(coupon);
            logger.info("쿠폰 상태가 'N'으로 업데이트되었습니다. 쿠폰 ID: {}", couponId);
        } catch (Exception e) {
            logger.error("사용자 쿠폰 사용 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자 ID로 쿠폰 사용 기록 조회
     *
     * @param memberId 현재 사용자의 ID
     * @return 사용자가 사용한 쿠폰 사용 기록 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<CouponUsageEntity> getCouponUsagesByMemberId(Long memberId) {
        try {
            logger.info("사용자 ID로 쿠폰 사용 기록을 조회합니다. 사용자 ID: {}", memberId);
            List<CouponUsageEntity> usages = couponUsageRepository.findByMemberId(memberId);
            logger.info("조회된 쿠폰 사용 기록 수: {}", usages.size());
            return usages;
        } catch (Exception e) {
            logger.error("사용자 ID로 쿠폰 사용 기록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<CouponEntity> getCoupons() {
        // couponRepository.findAll()은 모든 쿠폰을 조회합니다.
        return couponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<CouponEntity> getUserCoupons(Long memberId, Pageable pageable) {
        logger.info("사용자 ID({})로 페이징 쿠폰 목록을 조회합니다.", memberId);
        return couponRepository.findByMemberId(memberId, pageable);
    }

    @Override
    public void useUserCoupon(Long couponId) throws Exception {
        // Optional에서 CouponEntity를 꺼내거나, 없으면 예외 발생
        CouponEntity couponEntity = couponRepository.findById(couponId)
                .orElseThrow(() -> new Exception("쿠폰을 찾을 수 없습니다."));

        // 이미 사용된 쿠폰인지 확인 (쿠폰의 상태 비교)
        if ("N".equals(couponEntity.getStatus())) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        // 쿠폰 상태를 '사용완료'로 변경
        couponEntity.setStatus("N");
        couponRepository.save(couponEntity);
    }
}
