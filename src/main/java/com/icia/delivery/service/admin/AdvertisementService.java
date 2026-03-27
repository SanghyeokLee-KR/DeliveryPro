package com.icia.delivery.service.admin;

import com.icia.delivery.dao.admin.AdvertisementRepository;
import com.icia.delivery.dto.admin.AdvertisementDTO;
import com.icia.delivery.dto.admin.AdvertisementEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    // 광고 최대 개수
    private static final int MAX_AD_COUNT = 5;

    /**
     * 광고 등록 (1) 비어있는 advOrder 찾기 → (2) 파일 업로드 → (3) DB 저장
     */
    public void createAdvertisement(String advTitle, MultipartFile file) throws IOException {
        System.out.println("=== [Service] createAdvertisement() START ===");

        // 1) 아직 사용되지 않은 advOrder(1~5) 찾기
        //    예: 이미 DB에 advOrder=1,2,3 있으면 4가 빈 슬롯
        List<AdvertisementEntity> existingList = advertisementRepository.findAll();
        if (existingList.size() >= MAX_AD_COUNT) {
            throw new IllegalStateException("광고는 최대 5개까지만 등록 가능합니다.");
        }

        int foundOrder = findAvailableOrder(existingList);
        if (foundOrder == -1) {
            throw new IllegalStateException("광고는 최대 5개까지만 등록 가능합니다 (슬롯이 없음).");
        }

        // 2) 파일 체크
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("파일이 없습니다.");
        }
        // 확장자
        String ext = getExtension(originalFilename).toLowerCase();
        if (!ext.equals("jpg") && !ext.equals("png")) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식 (jpg, png만 가능)");
        }

        // 3) 파일명: "광고_{advOrder}번_{원본파일명}"
        String newFileName = "광고_" + foundOrder + "번_" + originalFilename;

        // 4) 실제 저장 폴더 (ex: src/main/resources/static/admin/img/광고)
        Path folder = Paths.get(System.getProperty("user.dir"),
                "src", "main", "resources", "static",
                "admin", "img", "광고");
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        Path target = folder.resolve(newFileName);
        // 물리 파일 저장
        file.transferTo(target.toFile());

        // 5) DB 저장
        AdvertisementEntity entity = new AdvertisementEntity();
        entity.setAdvOrder(foundOrder);
        entity.setAdvTitle(advTitle);
        entity.setAdvImageUrl("/admin/img/광고/" + newFileName);
        entity.setAdvCreatedAt(LocalDateTime.now());
        entity.setAdvUpdatedAt(LocalDateTime.now());

        advertisementRepository.save(entity);
        System.out.println("=== [Service] createAdvertisement() END ===");
    }

    /**
     * 광고 목록
     */
    @Transactional(readOnly = true)
    public List<AdvertisementDTO> getAllAdvertisements() {
        return advertisementRepository.findAll().stream()
                // advOrder 기준으로 정렬해서 리턴해도 됨 (sort by advOrder asc)
                .sorted((a, b) -> a.getAdvOrder().compareTo(b.getAdvOrder()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 광고 조회
     */
    @Transactional(readOnly = true)
    public AdvertisementDTO getAdvertisement(Long advId) {
        AdvertisementEntity e = advertisementRepository.findById(advId)
                .orElseThrow(() -> new IllegalArgumentException("광고가 존재하지 않습니다. ID=" + advId));
        return toDTO(e);
    }

    /**
     * 광고 수정 (이미지 파일도 변경 가능)
     */
    public void updateAdvertisement(AdvertisementDTO dto, MultipartFile file) throws IOException {
        AdvertisementEntity e = advertisementRepository.findById(dto.getAdvId())
                .orElseThrow(() -> new IllegalArgumentException("광고가 존재하지 않습니다. ID=" + dto.getAdvId()));

        // 제목 변경
        e.setAdvTitle(dto.getAdvTitle());
        e.setAdvUpdatedAt(LocalDateTime.now());

        // 이미지 파일이 새로 올라왔다면 처리
        if (file != null && !file.isEmpty()) {
            // 기존 파일 삭제
            deletePhysicalFile(e.getAdvImageUrl());

            // 새 파일명: "광고_{advOrder}번_{원본파일명}"
            String originalFilename = file.getOriginalFilename();
            String newFileName = "광고_" + e.getAdvOrder() + "번_" + originalFilename;

            Path folder = Paths.get(System.getProperty("user.dir"),
                    "src", "main", "resources", "static",
                    "admin", "img", "광고");
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }
            Path target = folder.resolve(newFileName);
            file.transferTo(target.toFile());

            e.setAdvImageUrl("/admin/img/광고/" + newFileName);
        }

        advertisementRepository.save(e);
    }

    /**
     * 광고 삭제 (DB + 물리 파일 삭제)
     */
    public void deleteAdvertisement(Long advId) {
        AdvertisementEntity entity = advertisementRepository.findById(advId)
                .orElseThrow(() -> new IllegalArgumentException("광고가 존재하지 않습니다. ID=" + advId));

        // 물리 파일 삭제
        deletePhysicalFile(entity.getAdvImageUrl());

        // DB 삭제
        advertisementRepository.deleteById(advId);

        // (선택) advOrder 정리? ex) reorder? 여기서는 그냥 빈 슬롯 남기는 식.
        // if we want to shift subsequent orders, we'd do that here.
    }


    // ========================= 내부 헬퍼 메서드 =============================
    // advOrder가 1~5 중 비어있는 번호 찾기
    private int findAvailableOrder(List<AdvertisementEntity> existingList) {
        // 예: already used order -> put in set
        boolean[] used = new boolean[MAX_AD_COUNT + 1]; // index 1..5
        for (AdvertisementEntity e : existingList) {
            if (e.getAdvOrder() != null && e.getAdvOrder() >= 1 && e.getAdvOrder() <= 5) {
                used[e.getAdvOrder()] = true;
            }
        }
        // 1..5 중 false인 첫 번째
        for (int i = 1; i <= MAX_AD_COUNT; i++) {
            if (!used[i]) return i; // 비어있음
        }
        return -1; // 없음
    }

    // 파일 경로에서 실제 파일 삭제
    private void deletePhysicalFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        // imageUrl = "/admin/img/광고/광고_3번_치킨.jpg"
        // 실제 경로 =  "project/src/main/resources/static" + "/admin/img/광고/광고_3번_치킨.jpg"
        Path filePath = Paths.get(System.getProperty("user.dir"),
                        "src", "main", "resources", "static")
                .resolve(imageUrl.replaceFirst("^/", ""));
        // remove leading "/" to avoid double slash

        try {
            Files.deleteIfExists(filePath);
            System.out.println("[deletePhysicalFile] Deleted file: " + filePath);
        } catch (IOException e) {
            System.err.println("[deletePhysicalFile] Failed to delete file: " + filePath + " : " + e.getMessage());
        }
    }

    private AdvertisementDTO toDTO(AdvertisementEntity e) {
        AdvertisementDTO d = new AdvertisementDTO();
        d.setAdvId(e.getAdvId());
        d.setAdvOrder(e.getAdvOrder());
        d.setAdvTitle(e.getAdvTitle());
        d.setAdvImageUrl(e.getAdvImageUrl());
        d.setAdvCreatedAt(e.getAdvCreatedAt());
        d.setAdvUpdatedAt(e.getAdvUpdatedAt());
        return d;
    }

    private String getExtension(String filename) {
        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1) return "";
        return filename.substring(dotPos + 1);
    }
}
