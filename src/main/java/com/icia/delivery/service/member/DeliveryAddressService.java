package com.icia.delivery.service.member;

import com.icia.delivery.dao.member.DeliveryAddressRepository;
import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dto.member.DeliveryAddressDTO;
import com.icia.delivery.dto.member.DeliveryAddressEntity;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository;
    private final MemberRepository memberRepository;
    private final HttpSession session; // 세션 객체 주입

    /**
     * 배송지를 메인으로 설정하는 메서드
     *
     * @param memberId 회원 ID
     * @param addrId   배송지 ID
     * @return 업데이트된 DeliveryAddressDTO
     */
    @Transactional
    public DeliveryAddressDTO setMainAddress(Long memberId, Long addrId) {
        // 1. 모든 기존 메인 주소를 서브로 변경
        addressRepository.clearMainAddress(memberId);

        // 2. 선택한 배송지의 addr_is_main을 "메인"으로 변경
        DeliveryAddressEntity mainAddress = addressRepository.findById(addrId)
                .orElseThrow(() -> new RuntimeException("해당 배송지를 찾을 수 없습니다."));

        if (!mainAddress.getAddrMemberId().equals(memberId)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        mainAddress.setAddrIsMain("메인");
        addressRepository.save(mainAddress);

        // 3. 회원의 mem_address 필드를 업데이트
        memberRepository.updateMemAddress(memberId, mainAddress.getAddrAddress());

        // 4. 세션의 mem_address 업데이트
        session.setAttribute("mem_address", mainAddress.getAddrAddress());

        return DeliveryAddressDTO.toDTO(mainAddress);
    }

    /**
     * 배송지 추가
     */
    @Transactional
    public DeliveryAddressDTO addAddress(DeliveryAddressDTO dto) {
        // 메인 배송지인 경우, 기존 메인 주소를 서브로 변경
        if ("메인".equals(dto.getIsMain())) {
            // 기존 메인 주소를 서브로 초기화
            addressRepository.clearMainAddress(dto.getMemberId());

            // 회원의 mem_address 업데이트
            memberRepository.updateMemAddress(dto.getMemberId(), dto.getAddress());

            // 세션의 mem_address 업데이트
            session.setAttribute("mem_address", dto.getAddress());
        }

        // DTO를 엔티티로 변환하고 저장
        DeliveryAddressEntity address = DeliveryAddressEntity.toEntity(dto);
        addressRepository.save(address);

        return DeliveryAddressDTO.toDTO(address);
    }

    /**
     * 배송지 수정
     */
    @Transactional
    public DeliveryAddressDTO updateAddress(Long id, DeliveryAddressDTO dto) {
        DeliveryAddressEntity address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배송지 정보를 찾을 수 없습니다."));

        if ("메인".equals(dto.getIsMain()) && !"메인".equals(address.getAddrIsMain())) {
            // 기존 메인 주소를 서브로 초기화
            addressRepository.clearMainAddress(dto.getMemberId());

            // 선택한 주소를 메인으로 설정
            address.setAddrIsMain("메인");
            addressRepository.save(address);

            // 회원의 mem_address 업데이트
            memberRepository.updateMemAddress(dto.getMemberId(), address.getAddrAddress());

            // 세션의 mem_address 업데이트
            session.setAttribute("mem_address", address.getAddrAddress());
        } else if ("서브".equals(dto.getIsMain()) && "메인".equals(address.getAddrIsMain())) {
            // 메인 주소를 서브로 변경
            address.setAddrIsMain("서브");
            addressRepository.save(address);

            // 회원의 mem_address를 null로 설정
            memberRepository.updateMemAddress(dto.getMemberId(), null);

            // 세션의 mem_address 제거
            session.removeAttribute("mem_address");

            // 다른 메인 주소가 있는지 확인하고, 있다면 세션에 설정
            Optional<DeliveryAddressEntity> newMainAddress = addressRepository.findMainAddress(dto.getMemberId());
            if (newMainAddress.isPresent()) {
                session.setAttribute("mem_address", newMainAddress.get().getAddrAddress());
            }
        } else {
            // 기타 필드 업데이트
            address.setAddrAddress(dto.getAddress());
            address.setAddrName(dto.getName());
            addressRepository.save(address);
        }

        return DeliveryAddressDTO.toDTO(address);
    }

    /**
     * 배송지 삭제
     */
    @Transactional
    public void deleteAddress(Long id) {
        DeliveryAddressEntity address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배송지 정보를 찾을 수 없습니다."));
        addressRepository.delete(address);

        // 삭제된 주소가 메인 주소였다면, 회원의 mem_address를 null로 설정
        if ("메인".equals(address.getAddrIsMain())) {
            memberRepository.updateMemAddress(address.getAddrMemberId(), null);

            // 세션의 mem_address 제거
            session.removeAttribute("mem_address");

            // 다른 메인 주소가 있는지 확인하고, 있다면 세션에 설정
            Optional<DeliveryAddressEntity> newMainAddress = addressRepository.findMainAddress(address.getAddrMemberId());
            if (newMainAddress.isPresent()) {
                session.setAttribute("mem_address", newMainAddress.get().getAddrAddress());
            }
        }
    }

    /**
     * 회원 번호로 배송지 조회
     */
    @Transactional(readOnly = true)
    public List<DeliveryAddressDTO> getAddressesByMemberId(Long memberId) {
        List<DeliveryAddressEntity> addresses = addressRepository.findByAddrMemberId(memberId);
        return addresses.stream()
                .map(DeliveryAddressDTO::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 회원 가입 시 메인 배송지 추가 (옵션: 필요 시 사용)
     */
    @Transactional
    public void addMainAddress(Long memberId, String address) {
        DeliveryAddressDTO dto = new DeliveryAddressDTO(null, memberId, "집", address, "메인", null);
        addAddress(dto);
    }
}
