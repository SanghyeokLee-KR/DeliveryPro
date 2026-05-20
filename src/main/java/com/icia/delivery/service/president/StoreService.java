package com.icia.delivery.service.president;

import com.icia.delivery.dao.member.MemberRepository;
import com.icia.delivery.dao.member.OrderRepository;
import com.icia.delivery.dao.member.reviewRepository;
import com.icia.delivery.dao.president.PreMemRepository;
import com.icia.delivery.dao.president.StoreMenuRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dto.member.MemberEntity;
import com.icia.delivery.dto.member.OrderEntity;
import com.icia.delivery.dto.member.ReviewEntity;
import com.icia.delivery.dto.president.*;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class StoreService {

    Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/store-img/store-main-img");
    Path path_2 = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/store-img/store-menu-img");

    private final HttpSession session;

    private final StoreRepository storerepostory;
    private final StoreMenuRepository smrepo;
    private final PreMemRepository pmrepo;
    private final OrderRepository orepo;
    private final reviewRepository rrepo;

    private final MemberRepository mrepo;


    public List<PreStoreDTO> getAllStores() {
        List<PreStoreEntity> entities = storerepostory.findAll();
        return entities.stream()
                .map(PreStoreDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }


    public List<PreStoreDTO> getStoresByCategory(String category) {
        List<PreStoreEntity> entities = storerepostory.findBypreStoCategory(category);
        return entities.stream()
                .map(PreStoreDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }

    public ModelAndView addStore(PreStoreDTO preDTO) {

        ModelAndView mav = new ModelAndView();

        // 세션에서 preMem_id 값을 가져옵니다
        Long preMemId = (Long) session.getAttribute("preMem_id");

        try {
            // 기본값 설정
            preDTO.setPreStoRating(Optional.ofNullable(preDTO.getPreStoRating()).orElse(0.0f));
            preDTO.setPreStoReviewCount(Optional.ofNullable(preDTO.getPreStoReviewCount()).orElse(0));

            // 필수값 강제 설정
            preDTO.setPreStoStatus("보류");
            preDTO.setPreStoCreatedAt(LocalDateTime.now());


            // 파일 가져오기
            MultipartFile prePhoto = preDTO.getPrePhoto();
            String savePath = "";

            if (!prePhoto.isEmpty()) {
                String fileName = prePhoto.getOriginalFilename();
                Long sequence = getNextFileSequence(); // 시퀀스를 가져옵니다.
                String newFileName = sequence + "_" + fileName;

                preDTO.setPreStoPhoto(newFileName);

                savePath = path + "\\" + newFileName;
            } else {
                preDTO.setPreStoPhone("default.jpg");
            }

            prePhoto.transferTo(new File(savePath));

            // DTO -> Entity 변환 및 저장
            PreStoreEntity preEntity = storerepostory.save(PreStoreEntity.toEntity(preDTO)); // 저장

            // addMenu가 실행되었을 때, 세션에 플래그 설정
            // addMenu 값을 모델에 전달
            mav.addObject("addMenu", "true");
            mav.setViewName("redirect:/store-management/" + preMemId);
        } catch (Exception e) {
            // 실패 시 회원가입 폼으로 리다이렉트
            mav.setViewName("redirect:/index");
            mav.addObject("error", "회원가입 처리 중 오류가 발생했습니다.");
            e.printStackTrace(); // 오류 로그 출력
        }
        return mav;

    }

    // 시퀀스를 구현하기 위한 메소드
    public Long getNextFileSequence() {
        return storerepostory.findMaxPreId() + 1;
    }

    public List<PreStoreDTO> storeList(Long pathValue) {
        List<PreStoreDTO> dto = new ArrayList<>();

        // 매장 리스트 가져오기
        List<PreStoreEntity> entityList = storerepostory.findByLoginId(pathValue);

        // 각 매장에 대해 DTO 변환
        for (PreStoreEntity entity : entityList) {
            dto.add(PreStoreDTO.toDTO(entity));
        }

        return dto;
    }

    public Map<String, Object> getStoreDetails(Long storeId) {
        Map<String, Object> response = new HashMap<>();

        // StoreRepository를 통해 매장 정보 조회
        Optional<PreStoreEntity> storeEntity = storerepostory.findByLoginId2(storeId);
        if (storeEntity.isPresent()) {
            PreStoreEntity entity = storeEntity.get();
            session.setAttribute("pre_store_id", entity.getPreStoId());
            session.setAttribute("pre_store_name", entity.getPreStoName());
            session.setAttribute("pre_store_category", entity.getPreStoCategory());
            session.setAttribute("pre_store_addr", entity.getPreStoAddress());
            session.setAttribute("pre_store_phone", entity.getPreStoPhone());
            session.setAttribute("pre_store_intro", entity.getPreStoIntro());
            session.setAttribute("pre_store_minOrder", entity.getPreStoMinOrderAmount());
            session.setAttribute("pre_store_deliFee", entity.getPreStoDeliveryFee());
            session.setAttribute("pre_store_img", entity.getPreStoPhoto());
            session.setAttribute("pre_store_min_time", entity.getPreStoDeliveryTimeMin());
            session.setAttribute("pre_store_max_time", entity.getPreStoDeliveryTimeMax());
            session.setAttribute("pre_store_opening_hours", entity.getPreStoOpeningHours());
            session.setAttribute("pre_store_operation_days", entity.getPreStoOperatingDays());
            session.setAttribute("pre_store_holiday_week", entity.getPreStoHolidayWeek());
            session.setAttribute("pre_store_day_off", entity.getPreStoDayOff());
            session.setAttribute("pre_store_status", entity.getPreStoStatus());


            response.put("storeId", entity.getPreStoId());


            System.out.println("클릭 매장 아이디 : " + session.getAttribute("pre_store_status"));
        } else {
            response.put("error", "Store not found");
        }

        return response;  // JSON 형식으로 반환할 데이터
    }


    public Map<String, Object> addMenu(PreStoreMenuDTO smDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 기본값 설정
            smDTO.setMenuPopularity(Optional.ofNullable(smDTO.getMenuPopularity()).orElse(0L));
            smDTO.setMenuCreatedDate(LocalDateTime.now());

            // 파일 처리
            MultipartFile mpFile = smDTO.getMpFile();
            String savePath = "";

            if (!mpFile.isEmpty()) {
                String fileName = mpFile.getOriginalFilename();
                Long sequence = getNextFileSequence2(); // 시퀀스를 가져옵니다.
                String newFileName = sequence + "_" + fileName;

                smDTO.setMenuPictureUrl(newFileName);
                savePath = path_2 + "\\" + newFileName;
            } else {
                smDTO.setMenuPictureUrl("default.jpg");
            }

            // 파일 저장
            mpFile.transferTo(new File(savePath));

            // DB에 저장
            smrepo.save(PreStoreMenuEntity.toEntity(smDTO));

            // 응답 설정
            response.put("status", "success");
            response.put("preStoId", smDTO.getPreStoId());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "메뉴 추가 처리 중 오류가 발생했습니다.");
            e.printStackTrace(); // 로그 출력
        }
        return response;
    }

    // 시퀀스를 구현하기 위한 메소드
    public Long getNextFileSequence2() {
        return smrepo.findMaxPreId() + 1;
    }


    public List<PreStoreMenuDTO> getStoreMenuList(Long preStoId) {

        List<PreStoreMenuDTO> dtoList = new ArrayList<>();


        List<PreStoreMenuEntity> entityList = smrepo.findByLoginId(preStoId);
        // System.out.println("리스트  받기 : " + entityList);

        for (PreStoreMenuEntity entity : entityList) {
            dtoList.add(PreStoreMenuDTO.toDTO(entity));
        }
        // System.out.println("리스트 보기 : " + dtoList);

        return dtoList;
    }

    public List<PreStoreDTO> getstoresBystoreId(Long storeId) {
        List<PreStoreEntity> entities = storerepostory.findBypreStoId(storeId);
        return entities.stream()
                .map(PreStoreDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }


    public String updateMenuStatus(Long menuId, String newStatus) {

        int updateCount = smrepo.updateMenuStatus(menuId, newStatus);

        // 업데이트가 성공하면 1, 실패하면 0을 반환
        if (updateCount > 0) {
            return "성공";  // 또는 JSON 형식으로 success: true 를 반환할 수도 있음
        } else {
            return "실패";  // 실패 시 처리할 내용
        }
    }

    public String menuDelete(Long menuId) {
        try {
            smrepo.deleteById(menuId);
            return "삭제 성공!";
        } catch (Exception e) {
            System.out.println("메뉴 삭제 중 오류 발생 : " + e.getMessage());
            throw new RuntimeException("메뉴 삭제 처리 중 오류 발생했습니다.");
        }
    }

    public String menuModify(PreStoreMenuDTO menuDTO) {

        try {
            Optional<PreStoreMenuEntity> opEntity = smrepo.findById(menuDTO.getMenuId());

            // 메뉴가 존재하지 않을 경우
            if (opEntity.isEmpty()) {
                return "ㅗ^^ㅗ";
            }

            PreStoreMenuEntity entity = opEntity.get();

            // DTO로 전달된 데이터 중 null이 아닌 필드만 업데이트
            if (menuDTO.getMenuName() != null) entity.setMenuName(menuDTO.getMenuName());
            if (menuDTO.getMenuPrice() != null) entity.setMenuPrice(menuDTO.getMenuPrice());
            if (menuDTO.getMenuDescription() != null) entity.setMenuDescription(menuDTO.getMenuDescription());
            if (menuDTO.getMenuPopularity() != null) entity.setMenuPopularity(menuDTO.getMenuPopularity());

            // 메뉴 수정 날짜 업뎃
            entity.setMenuModifiedDate(LocalDateTime.now());


            if (!menuDTO.getMenuPictureUrl().equals(entity.getMenuPictureUrl())) {

                String delPath = path_2 + "\\" + entity.getMenuPictureUrl();

                File delFile = new File(delPath);
                if (delFile.exists()) {
                    delFile.delete();
                }

                // 파일 가져오기
                MultipartFile mpFile = menuDTO.getMpFile();
                String savePath = "";

                if (!mpFile.isEmpty()) {
                    String fileName = mpFile.getOriginalFilename();
                    Long sequence = getNextFileSequence2(); // 시퀀스를 가져옵니다.
                    String newFileName = sequence + "_" + fileName;

                    entity.setMenuPictureUrl(newFileName);

                    savePath = path_2 + "\\" + newFileName;
                } else {
                    menuDTO.setMenuPictureUrl("default.jpg");
                }

                mpFile.transferTo(new File(savePath));
            }

            smrepo.save(entity);

            return "수정이 성공";
        } catch (Exception e) {
            System.out.println("메뉴 수정 중에 오류 발생 : " + e);
            throw new RuntimeException("메뉴 수정 처리 중 오류 발생했습니다.");
        }
    }

    public int storeCount(Long pathValue) {

        int result = storerepostory.findStoreCount(pathValue);
        // System.out.println("가게 갯수 : " + result);
        return result;
    }


    public List<PreStoreMenuDTO> searchMenuList(String keyword, String category, Long preStoId) {

        List<PreStoreMenuDTO> dtoList = new ArrayList<>();
        List<PreStoreMenuEntity> entityList = smrepo.findByMenuNameContainingOrderByMenuIdAsc(keyword, category, preStoId);

        for (PreStoreMenuEntity entity : entityList) {
            dtoList.add(PreStoreMenuDTO.toDTO(entity));
        }

        return dtoList;
    }

    @Transactional
    public ModelAndView editStoreHours(Long preStoId, String operationDays, String openingHours) {
        ModelAndView mav = new ModelAndView();

        // 세션에서 preMem_id 값을 가져옵니다
        Long preMemId = (Long) session.getAttribute("preMem_id");

        // operationDays와 openingHours가 null이 아니고 빈 문자열이 아닌 경우에만 처리
        if (preStoId != null && operationDays != null && !operationDays.isEmpty() && openingHours != null && !openingHours.isEmpty()) {
            int result = storerepostory.updateEditStoreHours(preStoId, operationDays, openingHours);

            if (result > 0) {
                session.setAttribute("pre_store_operation_days", operationDays);
                session.setAttribute("pre_store_opening_hours", openingHours);
                mav.setViewName("redirect:/store-management/" + preMemId);
            } else {
                System.out.println("업데이트 실행 중에 오류가 났습니다.");
            }
        } else {
            System.out.println("입력 값이 잘못되었습니다.");
        }

        return mav;
    }

    @Transactional
    public ModelAndView editStoreHolidayCycle(Long preStoId, String preStoHolidayWeek, String preStoDayOff) {

        ModelAndView mav = new ModelAndView();

        // 세션에서 preMem_id 값을 가져옵니다
        Long preMemId = (Long) session.getAttribute("preMem_id");

        // operationDays와 openingHours가 null이 아니고 빈 문자열이 아닌 경우에만 처리
        if (preStoId != null && preStoHolidayWeek != null && !preStoHolidayWeek.isEmpty() && preStoDayOff != null && !preStoDayOff.isEmpty()) {
            int result = storerepostory.updateEditStoreWeek(preStoId, preStoHolidayWeek, preStoDayOff);

            if (result > 0) {
                session.setAttribute("pre_store_holiday_week", preStoHolidayWeek);
                session.setAttribute("pre_store_day_off", preStoDayOff);
                mav.setViewName("redirect:/store-management/" + preMemId);
            } else {
                System.out.println("업데이트 실행 중에 오류가 났습니다.");
            }
        } else {
            System.out.println("입력 값이 잘못되었습니다.");
        }

        return mav;
    }


    public String updateStoreDetails(Long preStoreId, PreStoreDTO storeDTO) {
        try {
            // 데이터베이스에서 회원 번호(mId)를 기준으로 회원 정보 조회
            Optional<PreStoreEntity> opStoEntity = storerepostory.findBypreStoIdOptional(preStoreId);

            // 사용자가 존재하지 않을 경우 처리
            if (opStoEntity.isEmpty()) {

                return "T,T";
            }

            // 사용자가 존재하면 정보 업데이트
            PreStoreEntity preSto = opStoEntity.get();

            // DTO로 전달된 데이터 중 null이 아닌 필드만 업데이트
            if (storeDTO.getPreStoName() != null) preSto.setPreStoName(storeDTO.getPreStoName());
            if (storeDTO.getPreStoCategory() != null) preSto.setPreStoCategory(storeDTO.getPreStoCategory());
            if (storeDTO.getPreStoAddress() != null) preSto.setPreStoAddress(storeDTO.getPreStoAddress());
            if (storeDTO.getPreStoPhone() != null) preSto.setPreStoPhone(storeDTO.getPreStoPhone());
            if (storeDTO.getPreStoIntro() != null) preSto.setPreStoIntro(storeDTO.getPreStoIntro());
            if (storeDTO.getPreStoMinOrderAmount() != null)
                preSto.setPreStoMinOrderAmount(storeDTO.getPreStoMinOrderAmount());
            if (storeDTO.getPreStoDeliveryFee() != null) preSto.setPreStoDeliveryFee(storeDTO.getPreStoDeliveryFee());

            // 변경된 정보를 데이터베이스에 저장
            storerepostory.save(preSto);

            // DTO로 전달된 값 중 null이 아닌 경우에만 세션 값 업데이트
            if (storeDTO.getPreStoName() != null) {
                session.setAttribute("pre_store_name", storeDTO.getPreStoName());
            }
            if (storeDTO.getPreStoCategory() != null) {
                session.setAttribute("pre_store_category", storeDTO.getPreStoCategory());
            }
            if (storeDTO.getPreStoAddress() != null) {
                session.setAttribute("pre_store_addr", storeDTO.getPreStoAddress());
            }
            if (storeDTO.getPreStoPhone() != null) {
                session.setAttribute("pre_store_phone", storeDTO.getPreStoPhone());
            }
            if (storeDTO.getPreStoIntro() != null) {
                session.setAttribute("pre_store_intro", storeDTO.getPreStoIntro());
            }
            if (storeDTO.getPreStoMinOrderAmount() != null) {
                session.setAttribute("pre_store_minOrder", storeDTO.getPreStoMinOrderAmount());
            }
            if (storeDTO.getPreStoDeliveryFee() != null) {
                session.setAttribute("pre_store_deliFee", storeDTO.getPreStoDeliveryFee());
            }


        } catch (Exception e) {
            // 오류 발생 시 처리
//            mav.addObject("status", "error");
//            mav.addObject("message", "회원정보 수정 중 오류가 발생했습니다.");
            e.printStackTrace();
//            mav.setViewName("/member/myPage"); // 오류 시 동일 페이지로 이동
        }

        return null;
    }

    public boolean approve(Long id) {
        int result = storerepostory.updatePreStoStatusApprove(id);
        return result > 0;  // 수정된 행이 있으면 true 반환
    }

    public ModelAndView storeBreakTime(PreStoreDTO dto) {
        ModelAndView mav = new ModelAndView();


        Long preMemId = (Long) session.getAttribute("preMem_id");
        try {
            Optional<PreStoreEntity> entityOp = storerepostory.findById(dto.getPreStoId());
            System.out.println("아이디 조회 매장 : " + entityOp);

            // 사용자가 존재하지 않을 경우 처리
            if (entityOp.isEmpty()) {
                mav.setViewName("redirect:/store-management/" + preMemId + "예기치 못한 오류 발생");
                return mav;
            }

            // 사용자가 존재하면 정보 업데이트
            PreStoreEntity preSto = entityOp.get();

            // DTO로 전달된 데이터 중 null이 아닌 필드만 업데이트
            if (dto.getPreStoBreakTime() != null) {
                preSto.setPreStoBreakTime(dto.getPreStoBreakTime());
                preSto.setPreStoStatus("중지");
                preSto.setPreStoBreakStartTime(System.currentTimeMillis());
            }

            // 변경된 정보를 데이터베이스에 저장
            storerepostory.save(preSto);

            // 세션에 저장할 값 변환
            if (dto.getPreStoBreakTime() != null) {
                String breakTimeText = null;
                int breakTime = dto.getPreStoBreakTime();
                // 30분, 1시간, 2시간, 오늘 하루로 변환
                if (breakTime == 30) {
                    breakTimeText = "30분";
                } else if (breakTime == 60) {
                    breakTimeText = "1시간";
                } else if (breakTime == 120) {
                    breakTimeText = "2시간";
                } else if (breakTime == 1440) {
                    breakTimeText = "24시간";
                }

                session.setAttribute("pre_store_break_time", breakTimeText);
                session.setAttribute("pre_store_break_time_num", breakTime);
                session.setAttribute("storeId", dto.getPreStoId());
            }
            mav.setViewName("redirect:/store-management/" + preMemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mav;
    }

    // 경과 시간을 포맷하는 메서드 (밀리초 단위 → 시, 분, 초)
    private String formatElapsedTime(long elapsedTime) {
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @Scheduled(fixedDelay = 60000) // 60초마다 실행
    public void checkStoreBreakTime() {
        // "정지" 상태이며, preStoBreakTime이 설정된 매장 목록을 조회
        List<PreStoreEntity> suspendedStores = storerepostory.findByPreStoStatusAndPreStoBreakTimeIsNotNull("정지");
        long currentTime = System.currentTimeMillis();

        for (PreStoreEntity store : suspendedStores) {
            Long breakStart = store.getPreStoBreakStartTime();
            Integer breakDuration = store.getPreStoBreakTime(); // 휴식시간(분 단위)
            if (breakStart != null && breakDuration != null) {
                // 휴식시간(분)을 밀리초로 환산
                long breakMillis = breakDuration * 60 * 1000L;
                long expiryTime = breakStart + breakMillis;

                if (currentTime >= expiryTime) {
                    // 휴식시간 만료: 매장 상태를 "승인"으로 변경하고, 휴식 관련 필드를 초기화
                    store.setPreStoStatus("승인");
                    store.setPreStoBreakStartTime(null);
                    store.setPreStoBreakTime(null);
                    storerepostory.save(store);
                } else {
                    // 아직 휴식시간이 남은 경우 남은 시간을 계산 후 formatElapsedTime()을 호출
                    long remainingMillis = expiryTime - currentTime;
                    String formattedRemaining = formatElapsedTime(remainingMillis);
                    // 필요한 경우 이 값을 로깅하거나 화면에 출력합니다.
                    System.out.println("매장 ID " + store.getPreStoId() + " 남은 휴식 시간: " + formattedRemaining);
                }
            }
        }
    }

    public Map<String, Object> getStatistics() {
        // 세션에서 Store ID를 가져옴
        Long preStoId = (Long) session.getAttribute("pre_store_id");

        // 오늘 날짜 계산
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endDateTime = today.atTime(23, 59, 59); // 오늘 23:59:59
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minus(7, ChronoUnit.DAYS);

        // DB에서 PreStoreId와 날짜 범위에 맞는 주문 데이터를 조회
        List<OrderEntity> orders = orepo.findByPreStoIdAndOrderCreatedAtBetween(preStoId, startDateTime, endDateTime);
        List<ReviewEntity> reviews = rrepo.findByPreStoIdAndReviewCreatedAtBetween(preStoId, startDateTime, endDateTime);
        List<OrderEntity> weekOrders = orepo.findByPreStoIdAndOrderCreatedAtBetween(preStoId, oneWeekAgo, now);

        List<OrderEntity> totalorder = orepo.findByPreStoId(preStoId);
        List<ReviewEntity> totalreview = rrepo.findByPreStoId(preStoId);

        System.out.println(orders);
        System.out.println(reviews);
        System.out.println(weekOrders);
        System.out.println(totalorder);
        System.out.println(totalreview);


        Optional<PreStoreEntity> star = storerepostory.findById(preStoId);
        Float rating = star.map(PreStoreEntity::getPreStoRating).orElse(0.0f); // 기본값 0.0f

        // 주문/리뷰 총 갯수 계산
        int totalOrders = orders.size();
        int totalReviews = reviews.size();

        int totalOrder = totalorder.size();
        int totalReview = totalreview.size();

        int totalAmount = orders.stream()
                .mapToInt(order -> (int) order.getOrderTotalPrice())  // double을 int로 변환
                .sum();

        int totalAmount2 = weekOrders.stream()
                .mapToInt(order -> (int) order.getOrderTotalPrice())  // double을 int로 변환
                .sum();

        session.setAttribute("pre_store_orderCount", totalOrders);
        session.setAttribute("pre_store_reviewCount", totalReviews);
        session.setAttribute("pre_store_total_order", totalOrder);
        session.setAttribute("pre_store_total_review", totalReview);
        session.setAttribute("pre_store_toDay_total_price", totalAmount);
        session.setAttribute("pre_store_total_price", totalAmount2);

        // 별점별 리뷰 개수 계산
        List<Integer> starRatings = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            starRatings.add(0); // 1, 2, 3, 4, 5별로 개수를 세기 위해 초기화
        }

        // 리뷰를 순회하면서 별점에 맞는 카운트 증가
        for (ReviewEntity review : totalreview) {
            int starRating = review.getReviewRating(); // 리뷰의 별점
            if (starRating >= 1 && starRating <= 5) {
                starRatings.set(starRating - 1, starRatings.get(starRating - 1) + 1); // 별점에 맞는 배열 인덱스에 카운트 증가
            }
        }

        // 오늘의 주문 수 (단일 값으로 반환)
        int todayOrders = totalOrders;  // 오늘의 주문 수

        // 지난 일주일 간의 주문 수 (날짜별로 나누어 주문 수 집계)
        List<Integer> weekOrdersCount = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);  // 지난 일주일 날짜
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            // 해당 날짜의 주문 수 계산
            long count = weekOrders.stream()
                    .filter(order -> order.getOrderCreatedAt().isAfter(startOfDay) && order.getOrderCreatedAt().isBefore(endOfDay))
                    .count();

            weekOrdersCount.add((int) count);  // 해당 날짜의 주문 수를 리스트에 추가
        }

        // 결과를 맵에 저장
        Map<String, Object> response = new HashMap<>();
        response.put("todayOrders", todayOrders);    // 오늘의 주문 수 (단일 값)
        response.put("weekOrders", weekOrdersCount); // 지난 일주일 간의 주문 수
        response.put("starCount", rating);    // 리뷰의 평균 별점
        response.put("starRatings", starRatings);  // 별점별 리뷰 개수

        return response;
    }

    @Scheduled(cron = "0 * * * * *") // 매 분 0초에 실행
    @Transactional
    public String checkStoretime() {
        List<PreStoreEntity> stores = storerepostory.findAll();
        for (PreStoreEntity store : stores) {
            if (store.getPreStoBreakTime() != null && store.getPreStoBreakStartTime() != null) {
                // 휴식 시간이 설정되어 있는 경우, 해당 스케줄러는 운영시간 업데이트를 건너뜁니다.
                continue;
            }
            String currentStatus = store.getPreStoStatus();
            if (currentStatus != null &&
                    (currentStatus.equals("차단") || currentStatus.equals("폐점") || currentStatus.equals("보류"))) {
                continue;
            }
            // 1. 영업일(operatingDays) 체크
            String operatingDays = store.getPreStoOperatingDays();  // 예: "월-금" 또는 "월,화,수,목,금"
            if (operatingDays != null && !operatingDays.trim().isEmpty()) {
                // 현재 요일 구하기 (한글 단축명: "월", "화", "수", "목", "금", "토", "일")
                DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
                String currentDayKorean = switch (currentDay) {
                    case MONDAY -> "월";
                    case TUESDAY -> "화";
                    case WEDNESDAY -> "수";
                    case THURSDAY -> "목";
                    case FRIDAY -> "금";
                    case SATURDAY -> "토";
                    case SUNDAY -> "일";
                };

                // 영업일 정보에 오늘의 요일이 포함되어 있지 않으면 바로 차단
                if (!operatingDays.contains(currentDayKorean)) {
                    store.setPreStoStatus("중지");
                    continue;
                }
            } else {
                // 영업일 정보가 없으면 기본적으로 차단 처리
                store.setPreStoStatus("중지");
                continue;
            }

            // 2. 운영시간(operatingHours) 체크
            String openingHours = store.getPreStoOpeningHours(); // 예: "09:00-22:00" 또는 "23:00-02:00"
            if (openingHours != null) {
                String delimiter = null;
                if (openingHours.contains("-")) {
                    delimiter = "-";
                } else if (openingHours.contains("~")) {
                    delimiter = "~";
                }
                if (delimiter != null) {
                    String[] times = openingHours.split(delimiter);
                    if (times.length == 2) {
                        try {
                            LocalTime openTime = LocalTime.parse(times[0].trim(), DateTimeFormatter.ofPattern("HH:mm"));
                            LocalTime closeTime = LocalTime.parse(times[1].trim(), DateTimeFormatter.ofPattern("HH:mm"));
                            LocalTime now = LocalTime.now();

                            // 자정을 넘는 경우 처리
                            if (closeTime.isBefore(openTime)) {
                                // 예: "23:00-02:00" -> 자정을 넘어가는 경우
                                if (now.isAfter(openTime) || now.isBefore(closeTime)) {
                                    store.setPreStoStatus("승인");
                                } else {
                                    store.setPreStoStatus("중지");
                                }
                            } else {
                                // 일반적인 경우 (자정 이전에 운영 종료)
                                if (now.isAfter(openTime) && now.isBefore(closeTime)) {
                                    store.setPreStoStatus("승인");
                                } else {
                                    store.setPreStoStatus("중지");
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("시간 파싱 중 오류: " + e.getMessage());
                        }
                    }
                }
            }
        }
        return "";
    }
    @Transactional
    public String checkStoreStatus(Long storeId) {
        Optional<PreStoreEntity> storeOpt = storerepostory.findById(storeId);
        if (storeOpt.isPresent()) {
            PreStoreEntity store = storeOpt.get();
            System.out.println("[checkStoreStatus] storeId=" + storeId + ", 상태=" + store.getPreStoStatus());
            return store.getPreStoStatus();
        }
        return "";
    }

    public String getStoreNamebystoreId(Long preStoId) {

        Optional<PreStoreEntity> storeOpt = storerepostory.findById(preStoId);
        if (storeOpt.isPresent()){
            PreStoreEntity store = storeOpt.get();
            return store.getPreStoName();
        }
        return "";
    }


    public Map<String, Object> getStoreInfo(Long preStoId) {

        PreStoreEntity store = storerepostory.findById(preStoId)
                .orElseThrow(() -> new RuntimeException("해당 가게를 찾을 수 없습니다."));

        // 2. 가게와 연관된 사업자(PreMemberEntity) 조회
        //    (예: PreStoreEntity에 preMemberId 필드가 있다고 가정)
        Long preMemberId = store.getPreStoPreMemId(); // 가게 정보에 사업자 ID가 저장되어 있다고 가정합니다.
        PreMemberEntity preMember = pmrepo.findById(preMemberId)
                .orElseThrow(() -> new RuntimeException("해당 사업자 정보를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();
        result.put("preStoName", store.getPreStoName());
        result.put("preStoIntro", store.getPreStoIntro());
        result.put("preStoMinOrderAmount", store.getPreStoMinOrderAmount());
        result.put("preStoAddress", store.getPreStoAddress());
        result.put("preStoPhone", store.getPreStoPhone());
        result.put("preStoOpeningHours", store.getPreStoOpeningHours());
        result.put("preMemBizRegNo",preMember.getPreMemBizRegNo());
        return result;
    }

    public Map<String, Double> storeSalesData() {
        Long preStoId = (Long) session.getAttribute("pre_store_id");
        List<OrderEntity> orderEntities = orepo.findByPreStoId(preStoId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());

        double todaySales = 0, weekSales = 0, monthSales = 0, totalSales = 0;

        for (OrderEntity order : orderEntities) {
            LocalDate orderDate = order.getOrderCreatedAt().toLocalDate();
            double price = order.getOrderTotalPrice();

            if (orderDate.isEqual(today)) {
                todaySales += price;
            }
            if (!orderDate.isBefore(weekStart)) {
                weekSales += price;
            }
            if (!orderDate.isBefore(monthStart)) {
                monthSales += price;
            }
            totalSales += price;
        }

        Map<String, Double> salesData = new HashMap<>();
        salesData.put("todaySales", todaySales);
        salesData.put("weekSales", weekSales);
        salesData.put("monthSales", monthSales);
        salesData.put("totalSales", totalSales);

        return salesData;
    }

    @Transactional
    public Map<String, Object> storeMemBirthSalesData() {
        Long preStoId = (Long) session.getAttribute("pre_store_id");

        List<OrderEntity> orderEntities = orepo.findByPreStoId(preStoId);
        List<MemberEntity> memberEntities = mrepo.findAll(); // Lazy 로딩 해결됨

        // 남녀 성비 저장용
        int maleCount = 0;
        int femaleCount = 0;

        // 연령대별 주문 수 저장용
        Map<String, Integer> ageGroupMap = new HashMap<>();
        List<String> ageGroups = Arrays.asList("10대", "20대", "30대", "40대", "50대", "60대 이상");
        for (String group : ageGroups) {
            ageGroupMap.put(group, 0);
        }

        // 현재 연도 구하기
        int currentYear = LocalDate.now().getYear();

        // 주문한 회원 분석
        for (OrderEntity order : orderEntities) {
            Long memId = order.getMemId();

            // 회원 정보 찾기
            Optional<MemberEntity> memberOpt = memberEntities.stream()
                    .filter(m -> m.getMId().equals(memId))
                    .findFirst();

            if (memberOpt.isPresent()) {
                MemberEntity member = memberOpt.get();

                // 성별 카운트
                if ("남성".equals(member.getGender())) {
                    maleCount++;
                } else if ("여성".equals(member.getGender())) {
                    femaleCount++;
                }

                // 나이대 계산
                int birthYear = member.getBirthday().getYear();
                int age = currentYear - birthYear;
                String ageGroup;

                if (age < 20) {
                    ageGroup = "10대";
                } else if (age < 30) {
                    ageGroup = "20대";
                } else if (age < 40) {
                    ageGroup = "30대";
                } else if (age < 50) {
                    ageGroup = "40대";
                } else if (age < 60) {
                    ageGroup = "50대";
                } else {
                    ageGroup = "60대 이상";
                }

                ageGroupMap.put(ageGroup, ageGroupMap.get(ageGroup) + 1);
            }
        }

        // JSON 반환 데이터 구성
        Map<String, Object> result = new HashMap<>();
        result.put("genderRatio", Map.of("male", maleCount, "female", femaleCount));
        result.put("ageGroups", ageGroupMap);

        return result;
    }

    public Map<String, Object> storeMenuRank() {
        Long preStoId = (Long) session.getAttribute("pre_store_id");

        // 주문 데이터를 불러옵니다.
        List<OrderEntity> orderEntities = orepo.findByPreStoId(preStoId);

        // 메뉴별 주문 횟수를 저장할 맵
        Map<Long, Integer> menuOrderCount = new HashMap<>();

        // 주문된 메뉴의 메뉴 아이디를 카운트합니다.
        for (OrderEntity order : orderEntities) {
            Long menuId = order.getMenuId();
            menuOrderCount.put(menuId, menuOrderCount.getOrDefault(menuId, 0) + 1);
        }

        // 메뉴 정보를 반환할 리스트
        List<Map<String, Object>> menuRankList = new ArrayList<>();

        // 메뉴 이름을 PreStoreMenuEntity에서 가져옵니다.
        for (Map.Entry<Long, Integer> entry : menuOrderCount.entrySet()) {
            Long menuId = entry.getKey();
            Integer count = entry.getValue();

            // PreStoreMenuEntity에서 메뉴 정보를 가져옵니다.
            Optional<PreStoreMenuEntity> menuEntityOpt = smrepo.findById(menuId);
            if (menuEntityOpt.isPresent()) {
                PreStoreMenuEntity menuEntity = menuEntityOpt.get();

                // 메뉴 랭킹 데이터를 리스트에 추가
                Map<String, Object> menuData = new HashMap<>();
                menuData.put("menuId", menuId);
                menuData.put("menuName", menuEntity.getMenuName());
                menuData.put("orderCount", count);
                menuData.put("menuPictureUrl", menuEntity.getMenuPictureUrl());  // 메뉴 이미지 URL 추가

                menuRankList.add(menuData);
            }
        }

        // 결과를 반환
        Map<String, Object> response = new HashMap<>();
        response.put("menuRankList", menuRankList);

        return response;
    }
}