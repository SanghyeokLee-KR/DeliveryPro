package com.icia.delivery.global.config;

import com.icia.delivery.domain.admin.entity.AdminEntity;
import com.icia.delivery.domain.admin.entity.AdvertisementEntity;
import com.icia.delivery.domain.admin.repository.AdminRepository;
import com.icia.delivery.domain.admin.repository.AdvertisementRepository;
import com.icia.delivery.domain.board.entity.BoardEntity;
import com.icia.delivery.domain.board.repository.BoardRepository;
import com.icia.delivery.domain.coupon.entity.CouponEntity;
import com.icia.delivery.domain.coupon.repository.CouponRepository;
import com.icia.delivery.domain.deliveryaddress.entity.DeliveryAddressEntity;
import com.icia.delivery.domain.deliveryaddress.repository.DeliveryAddressRepository;
import com.icia.delivery.domain.member.entity.MemberEntity;
import com.icia.delivery.domain.member.repository.MemberRepository;
import com.icia.delivery.domain.notification.entity.NotificationEntity;
import com.icia.delivery.domain.notification.repository.NotificationRepository;
import com.icia.delivery.domain.order.entity.OrderEntity;
import com.icia.delivery.domain.order.entity.OrderItemEntity;
import com.icia.delivery.domain.order.repository.OrderItemRepository;
import com.icia.delivery.domain.order.repository.OrderRepository;
import com.icia.delivery.domain.president.entity.PreMemberEntity;
import com.icia.delivery.domain.president.repository.PreMemRepository;
import com.icia.delivery.domain.review.entity.ReviewEntity;
import com.icia.delivery.domain.review.repository.reviewRepository;
import com.icia.delivery.domain.rider.entity.RiderEntity;
import com.icia.delivery.domain.rider.repository.RiderRepository;
import com.icia.delivery.domain.store.entity.PreStoreEntity;
import com.icia.delivery.domain.store.repository.StoreRepository;
import com.icia.delivery.domain.storemenu.entity.PreStoreMenuEntity;
import com.icia.delivery.domain.storemenu.repository.StoreMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * h2 데모 프로파일 전용 초기 데이터.
 *
 * 데모 계정
 * 관리자   /admin            admin  / admin1234
 * 회원     /mLoginForm       user01 / user1234
 * 회원     /mLoginForm       user02 / user1234
 * 사장님   /pLoginForm       boss01 / boss1234
 * 사장님   /pLoginForm       boss02 / boss1234
 * 사장님   /pLoginForm       boss03 / boss1234
 * 사장님   /pLoginForm       boss04 / boss1234
 * 라이더   /rLoginForm       rider01 / rider1234
 * 라이더   /rLoginForm       rider02 / rider1234
 *
 * 관리자만 평문으로 넣는다. AdminService 가 String.equals 로 대조하기 때문이다.
 */
@Slf4j
@Component
@Profile("h2")
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    /**
     * 업로드 산출물이 저장소에서 분리돼 store-img 아래가 비어 있다.
     * 화면이 항상 붙이는 접두 경로를 거슬러 올라가 정적 카테고리 아이콘으로 대체한다.
     */
    private static final String ICON = "../../common/img/customer_main/menu_images/";

    private static final String ICON_CHICKEN = ICON + "음식_02_치킨.png";
    private static final String ICON_PIZZA = ICON + "음식_03_피자.png";
    private static final String ICON_KOREAN = ICON + "음식_06_한식.png";
    private static final String ICON_SNACK = ICON + "음식_10_분식.png";

    private static final String DEMO_IP = "127.0.0.1";

    private final AdminRepository adminRepository;
    private final AdvertisementRepository advertisementRepository;
    private final MemberRepository memberRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final PreMemRepository preMemRepository;
    private final StoreRepository storeRepository;
    private final StoreMenuRepository storeMenuRepository;
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final reviewRepository reviewRepository;
    private final BoardRepository boardRepository;
    private final CouponRepository couponRepository;
    private final NotificationRepository notificationRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (memberRepository.count() > 0) {
            log.info("데모 데이터가 이미 있어 시딩을 건너뛴다.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        seedAdmin(now);
        seedAdvertisements(now);
        seedCoupons(now);

        Long memberOne = seedMember("user01", "user1234", "김도현", "도현", "user01@example.com",
                "010-2451-8830", LocalDate.of(1996, 4, 12), "남성", 3200L, "Welcome",
                "인천광역시 미추홀구 인하로 67, 101동 1203호", now);
        Long memberTwo = seedMember("user02", "user1234", "박서연", "서연", "user02@example.com",
                "010-7712-6094", LocalDate.of(1993, 11, 3), "여성", 15400L, "VIP",
                "인천광역시 미추홀구 경인로 320, 502호", now);

        seedAddress(memberOne, "집", "인천광역시 미추홀구 인하로 67, 101동 1203호");
        seedAddress(memberTwo, "회사", "인천광역시 미추홀구 경인로 320, 502호");

        Long bossOne = seedPresident("boss01", "정민석", "382-11-00471", "boss01@example.com",
                "010-3391-7742", "승인", now);
        Long bossTwo = seedPresident("boss02", "한유진", "121-45-88213", "boss02@example.com",
                "010-5580-2214", "승인", now);
        Long bossThree = seedPresident("boss03", "오재원", "606-32-90118", "boss03@example.com",
                "010-9042-3316", "승인", now);
        Long bossFour = seedPresident("boss04", "임하늘", "755-19-40027", "boss04@example.com",
                "010-2287-6650", "보류", now);

        // 별점과 리뷰 수는 아래에서 실제로 넣는 리뷰와 맞춘다.
        Long storeChicken = seedStore(bossOne, "주안통닭 본점", "치킨",
                "인천광역시 미추홀구 미추홀대로 716", ICON_CHICKEN, "032-421-7788",
                "매일 아침 국내산 생닭만 손질해 씁니다. 튀김옷은 얇게, 기름은 하루 두 번 갈아 씁니다.",
                16000, 3000, 30, 45, 4.5f, 2, "승인", now);
        Long storeSnack = seedStore(bossTwo, "미추홀 분식당", "분식",
                "인천광역시 미추홀구 인하로 100", ICON_SNACK, "032-873-1140",
                "떡은 당일 뽑은 쌀떡만 씁니다. 매운맛은 순한맛부터 매운맛까지 세 단계로 조절됩니다.",
                12000, 2500, 20, 35, 4.0f, 1, "승인", now);
        Long storePizza = seedStore(bossThree, "숭의동 화덕피자", "피자",
                "인천광역시 미추홀구 매소홀로 488", ICON_PIZZA, "032-765-2093",
                "장작 화덕에서 400도로 90초간 굽습니다. 도우는 저온에서 48시간 숙성합니다.",
                18000, 3500, 35, 50, 0.0f, 0, "승인", now);
        // 관리자 승인대기 목록을 비우지 않으려고 보류 상태 가게를 하나 남긴다.
        seedStore(bossFour, "용현동 왕돈까스", "한식",
                "인천광역시 미추홀구 용현동 627-31", ICON_KOREAN, "032-882-4417",
                "1인분에 250그램을 씁니다. 소스는 사흘에 걸쳐 직접 졸입니다.",
                13000, 3000, 25, 40, 0.0f, 0, "보류", now);

        Long menuFried = seedMenu(storeChicken, "치킨", "후라이드 통닭", 18000L, ICON_CHICKEN,
                1L, "판매중", "얇은 튀김옷에 소금 간만 해서 튀긴 기본 메뉴", now);
        Long menuSeasoned = seedMenu(storeChicken, "치킨", "양념 통닭", 19000L, ICON_CHICKEN,
                1L, "판매중", "고추장 양념을 두 번 바르고 다시 한 번 구워낸 메뉴", now);
        seedMenu(storeChicken, "치킨", "반반 통닭", 19500L, ICON_CHICKEN,
                0L, "판매중", "후라이드와 양념을 반씩 담은 구성", now);
        seedMenu(storeChicken, "치킨", "마늘 간장 통닭", 20000L, ICON_CHICKEN,
                0L, "판매중", "간장 베이스에 구운 마늘을 얹은 메뉴", now);
        seedMenu(storeChicken, "치킨", "순살 크리스피", 21000L, ICON_CHICKEN,
                0L, "판매중", "닭다리살만 발라 튀긴 뼈 없는 메뉴", now);
        Long menuFries = seedMenu(storeChicken, "사이드", "감자튀김", 4000L, ICON_CHICKEN,
                0L, "품절", "치즈 시즈닝을 따로 담아 보내는 사이드", now);

        Long menuTteok = seedMenu(storeSnack, "분식", "떡볶이", 5500L, ICON_SNACK,
                1L, "판매중", "당일 뽑은 쌀떡에 어묵과 대파를 넣고 끓인 기본 메뉴", now);
        Long menuRose = seedMenu(storeSnack, "분식", "로제 떡볶이", 7000L, ICON_SNACK,
                1L, "판매중", "생크림을 넣어 매운맛을 눌러낸 메뉴", now);
        Long menuGimbap = seedMenu(storeSnack, "김밥", "김밥", 4000L, ICON_SNACK,
                0L, "판매중", "단무지와 우엉을 넣은 기본 김밥", now);
        seedMenu(storeSnack, "김밥", "참치김밥", 5000L, ICON_SNACK,
                0L, "판매중", "참치와 마요네즈를 따로 버무려 넣은 김밥", now);
        seedMenu(storeSnack, "튀김", "튀김 모둠", 5000L, ICON_SNACK,
                0L, "판매중", "오징어, 고구마, 김말이를 두 개씩 담은 구성", now);
        Long menuSundae = seedMenu(storeSnack, "분식", "순대", 5500L, ICON_SNACK,
                0L, "판매중", "찰순대에 간과 허파를 곁들인 메뉴", now);

        seedMenu(storePizza, "피자", "마르게리타 피자", 15900L, ICON_PIZZA,
                1L, "판매중", "토마토 소스와 생모차렐라, 바질만 올린 메뉴", now);
        Long menuPepperoni = seedMenu(storePizza, "피자", "페퍼로니 피자", 17900L, ICON_PIZZA,
                1L, "판매중", "페퍼로니를 두 겹으로 덮은 메뉴", now);
        seedMenu(storePizza, "피자", "고구마 피자", 18900L, ICON_PIZZA,
                0L, "판매중", "구운 고구마 무스를 테두리까지 채운 메뉴", now);
        seedMenu(storePizza, "피자", "불고기 피자", 19900L, ICON_PIZZA,
                0L, "판매중", "직접 재운 불고기와 양파를 올린 메뉴", now);
        seedMenu(storePizza, "파스타", "갈릭 크림 파스타", 12900L, ICON_PIZZA,
                0L, "판매중", "구운 마늘을 갈아 넣은 크림 파스타", now);

        Long riderOne = seedRider("rider01", "최준영", "010-8820-4471", "오토바이", "남성",
                "1994-07-21", 312, "승인", now);
        seedRider("rider02", "이가온", "010-4417-9902", "자전거", "여성",
                "1999-02-08", 0, "보류", now);

        Long orderPickup = seedOrder(storeChicken, memberOne, menuFried, "주문접수", "픽업중",
                "한집배달", "카드", 25000, 3000, 0,
                "튀김옷 바삭하게 부탁드립니다.", "공동현관 비밀번호 1204#", now.minusMinutes(12));
        seedOrderItem(orderPickup, menuFried, "후라이드 통닭", 1L, 18000, 18000, now.minusMinutes(12));
        seedOrderItem(orderPickup, menuFries, "감자튀김", 1L, 4000, 4000, now.minusMinutes(12));

        Long orderAssigning = seedOrder(storeSnack, memberTwo, menuRose, "주문접수", "배차중",
                "묶음배달", "간편결제", 15500, 2500, 2000,
                "매운맛 순하게 해주세요.", "도착하면 문 앞에 놓고 연락 주세요.", now.minusMinutes(25));
        seedOrderItem(orderAssigning, menuRose, "로제 떡볶이", 1L, 7000, 7000, now.minusMinutes(25));
        seedOrderItem(orderAssigning, menuGimbap, "김밥", 2L, 4000, 8000, now.minusMinutes(25));

        Long orderOnWay = seedOrder(storePizza, memberOne, menuPepperoni, "주문접수", "배달중",
                "한집배달", "카드", 21400, 3500, 0,
                "피클은 빼주세요.", "엘리베이터가 점검 중입니다.", now.minusMinutes(38));
        seedOrderItem(orderOnWay, menuPepperoni, "페퍼로니 피자", 1L, 17900, 17900, now.minusMinutes(38));

        Long orderDoneChicken = seedOrder(storeChicken, memberTwo, menuSeasoned, "주문접수", "배달완료",
                "한집배달", "간편결제", 26000, 3000, 0,
                "양념 따로 담아주세요.", "경비실에 맡겨 주세요.", now.minusDays(3));
        seedOrderItem(orderDoneChicken, menuSeasoned, "양념 통닭", 1L, 19000, 19000, now.minusDays(3));
        seedOrderItem(orderDoneChicken, menuFries, "감자튀김", 1L, 4000, 4000, now.minusDays(3));

        Long orderDoneSnack = seedOrder(storeSnack, memberOne, menuTteok, "주문접수", "배달완료",
                "묶음배달", "카드", 19000, 2500, 0,
                "순대는 소금 많이 주세요.", "문 앞에 놓아주세요.", now.minusDays(5));
        seedOrderItem(orderDoneSnack, menuTteok, "떡볶이", 2L, 5500, 11000, now.minusDays(5));
        seedOrderItem(orderDoneSnack, menuSundae, "순대", 1L, 5500, 5500, now.minusDays(5));

        seedReview(orderDoneChicken, storeChicken, memberTwo, menuSeasoned, 5,
                "닭이 크고 양념이 과하지 않습니다. 배달도 예상 시간보다 빨랐습니다.", now.minusDays(3).plusHours(2));
        seedReview(orderDoneChicken, storeChicken, memberTwo, menuFries, 4,
                "감자튀김이 눅눅하지 않게 왔습니다. 양이 조금 아쉽습니다.", now.minusDays(3).plusHours(2));
        seedReview(orderDoneSnack, storeSnack, memberOne, menuTteok, 4,
                "떡이 쫄깃하고 국물이 자극적이지 않습니다. 순대는 조금 식어서 왔습니다.", now.minusDays(4).plusHours(9));

        seedBoard(memberOne, "배달이 지연되면 어디로 문의하나요",
                "주문 후 예상 시간이 20분 넘게 지났는데 라이더 연락이 없습니다. 문의 창구를 알려주세요.",
                "미답변", now.minusDays(1));
        seedBoard(memberTwo, "결제 수단을 변경하고 싶습니다",
                "간편결제로 등록한 카드를 다른 카드로 바꾸려는데 마이페이지에서 찾지 못했습니다.",
                "답변완료", now.minusDays(4));
        seedBoard(memberOne, "리뷰에 올린 사진을 지우고 싶습니다",
                "작성한 리뷰는 남기고 첨부한 사진만 삭제할 수 있는지 알고 싶습니다.",
                "미답변", now.minusDays(6));

        seedNotification("STORE", storeChicken, "MEMBER", memberOne, "주문",
                "신규 주문이 접수됐습니다. 주문번호 " + orderPickup, now.minusMinutes(12));
        seedNotification("MEMBER", memberOne, "RIDER", riderOne, "배달",
                "주문하신 음식이 배달을 시작했습니다.", now.minusMinutes(6));
        seedNotification("ADMIN", 1L, "STORE", bossFour, "가입",
                "신규 사장님 가입 승인 요청이 있습니다.", now.minusHours(3));

        log.info("데모 데이터 시딩 완료. 회원 {}, 가게 {}, 메뉴 {}, 주문 {}",
                memberRepository.count(), storeRepository.count(),
                storeMenuRepository.count(), orderRepository.count());
    }

    private void seedAdmin(LocalDateTime now) {
        AdminEntity admin = new AdminEntity();
        admin.setAdminUsername("admin");
        admin.setAdminPassword("admin1234");
        admin.setAdminEmail("admin@deliverypro.demo");
        admin.setAdminRole("슈퍼 관리자");
        admin.setAdminCreatedAt(now.minusMonths(6));
        admin.setAdminLastLogin(now.minusHours(2));
        admin.setAdminStatus("활성");
        adminRepository.save(admin);
    }

    private void seedAdvertisements(LocalDateTime now) {
        advertisementRepository.save(advertisement(1, "도미노피자 신메뉴",
                "/admin/img/광고/광고_1번사진_도미노피자.jpg", now));
        advertisementRepository.save(advertisement(2, "메가커피 여름 음료",
                "/admin/img/광고/광고_3번사진_메가커피.jpg", now));
        advertisementRepository.save(advertisement(3, "롯데리아 세트 할인",
                "/admin/img/광고/광고_5번사진_롯데리아.jpg", now));
    }

    private AdvertisementEntity advertisement(int order, String title, String imageUrl, LocalDateTime now) {
        AdvertisementEntity entity = new AdvertisementEntity();
        entity.setAdvOrder(order);
        entity.setAdvTitle(title);
        entity.setAdvImageUrl(imageUrl);
        entity.setAdvCreatedAt(now.minusDays(10));
        entity.setAdvUpdatedAt(now.minusDays(10));
        return entity;
    }

    private void seedCoupons(LocalDateTime now) {
        couponRepository.save(coupon("WELCOME3000", "신규 가입 축하 쿠폰",
                "첫 주문에 쓸 수 있는 3천원 할인 쿠폰", 3000L, 12000L, now.plusDays(30)));
        couponRepository.save(coupon("RAINY2000", "우천 배달비 지원",
                "비 오는 날 배달비 2천원 지원", 2000L, 15000L, now.plusDays(14)));
    }

    private CouponEntity coupon(String code, String name, String content,
                                Long deductPrice, Long minPrice, LocalDateTime expiredDate) {
        CouponEntity entity = new CouponEntity();
        entity.setCode(code);
        entity.setName(name);
        entity.setContent(content);
        entity.setDeductPrice(deductPrice);
        entity.setMinPrice(minPrice);
        entity.setOrderType("배달");
        entity.setStatus("Y");
        entity.setExpiredDate(expiredDate);
        return entity;
    }

    private Long seedMember(String userId, String rawPassword, String username, String nickname,
                            String email, String phone, LocalDate birthday, String gender,
                            Long point, String grade, String address, LocalDateTime now) {
        MemberEntity entity = new MemberEntity();
        entity.setUserId(userId);
        entity.setPassword(passwordEncoder.encode(rawPassword));
        entity.setUsername(username);
        entity.setNickname(nickname);
        entity.setEmail(email);
        entity.setPhone(phone);
        entity.setBirthday(birthday);
        entity.setGender(gender);
        entity.setPoint(point);
        entity.setGrade(grade);
        entity.setAddress(address);
        entity.setRegisterIp(DEMO_IP);
        entity.setLastLoginIp(DEMO_IP);
        entity.setRegisterDate(now.minusMonths(4));
        entity.setLastLoginDate(now.minusHours(5));
        entity.setStatus("활성");
        entity.setReceiveEmail("승인");
        entity.setOpenProfile("승인");
        entity.setReceiveNotify("승인");
        entity.setLoginType("LOCAL");
        return memberRepository.save(entity).getMId();
    }

    private void seedAddress(Long memberId, String name, String address) {
        DeliveryAddressEntity entity = new DeliveryAddressEntity();
        entity.setAddrMemberId(memberId);
        entity.setAddrName(name);
        entity.setAddrAddress(address);
        entity.setAddrIsMain("메인");
        deliveryAddressRepository.save(entity);
    }

    private Long seedPresident(String userId, String ceoName, String bizRegNo, String email,
                               String phone, String approvalStatus, LocalDateTime now) {
        PreMemberEntity entity = new PreMemberEntity();
        entity.setPreMemUserId(userId);
        entity.setPreMemPassword(passwordEncoder.encode("boss1234"));
        entity.setPreMemCeoName(ceoName);
        entity.setPreMemBizRegNo(bizRegNo);
        entity.setPreMemEmail(email);
        entity.setPreMemPhone(phone);
        entity.setPreMemCreatedAt(now.minusMonths(8));
        entity.setPreMemUpdatedAt(now.minusMonths(8));
        entity.setPreMemStatus("정상");
        entity.setPreMemApprovalStatus(approvalStatus);
        return preMemRepository.save(entity).getPreMemId();
    }

    private Long seedStore(Long preMemId, String name, String category, String address, String photo,
                           String phone, String intro, int minOrderAmount, int deliveryFee,
                           int deliveryTimeMin, int deliveryTimeMax, float rating, int reviewCount,
                           String status, LocalDateTime now) {
        PreStoreEntity entity = new PreStoreEntity();
        entity.setPreStoPreMemId(preMemId);
        entity.setPreStoName(name);
        entity.setPreStoCategory(category);
        entity.setPreStoAddress(address);
        entity.setPreStoPhoto(photo);
        entity.setPreStoPhone(phone);
        entity.setPreStoIntro(intro);
        entity.setPreStoMinOrderAmount(minOrderAmount);
        entity.setPreStoDeliveryFee(deliveryFee);
        entity.setPreStoDeliveryTimeMin(deliveryTimeMin);
        entity.setPreStoDeliveryTimeMax(deliveryTimeMax);
        entity.setPreStoRating(rating);
        entity.setPreStoReviewCount(reviewCount);
        entity.setPreStoCreatedAt(now.minusMonths(7));
        entity.setPreStoUpdatedAt(now.minusDays(2));
        entity.setPreStoStatus(status);
        entity.setPreStoOperatingDays("월,화,수,목,금,토,일");
        // 매 분 도는 checkStoretime 이 영업시간 밖이면 상태를 중지로 덮는다. 캡처 시각과 무관하게 열려 있어야 한다.
        entity.setPreStoOpeningHours("00:00-23:59");
        entity.setPreStoDayOff("연중무휴");
        entity.setPreStoDeliveryArea("인천 미추홀구 주안동, 용현동, 숭의동, 도화동");
        return storeRepository.save(entity).getPreStoId();
    }

    private Long seedMenu(Long storeId, String category, String name, Long price, String pictureUrl,
                          Long popularity, String status, String description, LocalDateTime now) {
        PreStoreMenuEntity entity = new PreStoreMenuEntity();
        entity.setPreStoId(storeId);
        entity.setMenuCategory(category);
        entity.setMenuName(name);
        entity.setMenuPrice(price);
        entity.setMenuPictureUrl(pictureUrl);
        entity.setMenuPopularity(popularity);
        entity.setMenuStatus(status);
        entity.setMenuDescription(description);
        entity.setMenuCreatedDate(now.minusMonths(7));
        entity.setMenuModifiedDate(now.minusDays(2));
        return storeMenuRepository.save(entity).getMenuId();
    }

    private Long seedRider(String riderId, String riderName, String phone, String vehicleType,
                           String gender, String birth, int totalDeliveries, String isAvailable,
                           LocalDateTime now) {
        RiderEntity entity = new RiderEntity();
        entity.setRiderId(riderId);
        entity.setRiderPw(passwordEncoder.encode("rider1234"));
        entity.setRiderName(riderName);
        entity.setRiderPhone(phone);
        entity.setVehicleType(vehicleType);
        entity.setRiderGender(gender);
        entity.setRiderBirth(birth);
        entity.setTotalDeliveries(totalDeliveries);
        entity.setIsAvailable(isAvailable);
        entity.setRiderCreatedAt(now.minusMonths(5));
        return riderRepository.save(entity).getRiderNo();
    }

    private Long seedOrder(Long storeId, Long memberId, Long menuId, String orderStatus,
                           String deliveryStatus, String deliveryType, String paymentMethod,
                           double totalPrice, int deliveryFee, int discountAmount,
                           String customerMessage, String deliveryMessage, LocalDateTime createdAt) {
        OrderEntity entity = new OrderEntity();
        entity.setPreStoId(storeId);
        entity.setMemId(memberId);
        entity.setMenuId(menuId);
        entity.setOrderStatus(orderStatus);
        entity.setDeliveryStatus(deliveryStatus);
        entity.setDeliveryType(deliveryType);
        entity.setPaymentMethod(paymentMethod);
        entity.setOrderTotalPrice(totalPrice);
        entity.setDeliveryFee(deliveryFee);
        entity.setDiscountAmount(discountAmount);
        entity.setCustomerMessage(customerMessage);
        entity.setDeliveryMessage(deliveryMessage);
        entity.setOrderCreatedAt(createdAt);
        entity.setOrderUpdatedAT(createdAt);
        return orderRepository.save(entity).getOrderId();
    }

    private void seedOrderItem(Long orderId, Long menuId, String itemName, Long quantity,
                               double itemPrice, double totalPrice, LocalDateTime orderDate) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setOrderId(orderId);
        entity.setMenuId(menuId);
        entity.setItemName(itemName);
        entity.setQuantity(quantity);
        entity.setItemPrice(itemPrice);
        entity.setTotalPrice(totalPrice);
        entity.setOrderDate(orderDate);
        orderItemRepository.save(entity);
    }

    private void seedReview(Long orderId, Long storeId, Long memberId, Long menuId, int rating,
                            String content, LocalDateTime createdAt) {
        ReviewEntity entity = new ReviewEntity();
        entity.setOrderId(orderId);
        entity.setPreStoId(storeId);
        entity.setMemId(memberId);
        entity.setMenuId(menuId);
        entity.setReviewRating(rating);
        entity.setReviewContent(content);
        entity.setReviewCreatedAt(createdAt);
        reviewRepository.save(entity);
    }

    private void seedBoard(Long memberId, String title, String content, String answerStatus,
                           LocalDateTime createdAt) {
        BoardEntity entity = new BoardEntity();
        entity.setMemId(memberId);
        entity.setBoardTitle(title);
        entity.setBoardContent(content);
        entity.setBoardAnswerStatus(answerStatus);
        entity.setBoardCreatedAt(createdAt);
        entity.setBoardUpdatedAt(createdAt);
        boardRepository.save(entity);
    }

    private void seedNotification(String recipientType, Long recipientId, String senderType,
                                  Long senderId, String category, String message,
                                  LocalDateTime createdAt) {
        NotificationEntity entity = new NotificationEntity();
        entity.setRecipientType(recipientType);
        entity.setRecipientId(recipientId);
        entity.setSenderType(senderType);
        entity.setSenderId(senderId);
        entity.setCategory(category);
        entity.setMessage(message);
        entity.setStatus("읽지 않음");
        entity.setCreatedAt(createdAt);
        notificationRepository.save(entity);
    }
}
