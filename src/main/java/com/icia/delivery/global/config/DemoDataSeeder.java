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
import com.icia.delivery.domain.deliverygroup.entity.DeliveryGroupEntity;
import com.icia.delivery.domain.deliverygroup.entity.DeliveryGroupItemEntity;
import com.icia.delivery.domain.deliverygroup.repository.DeliveryGroupItemRepository;
import com.icia.delivery.domain.deliverygroup.repository.DeliveryGroupRepository;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * h2 데모 프로파일 전용 초기 데이터.
 *
 * 데모 계정
 * 관리자   /admin            admin  / admin1234
 * 회원     /mLoginForm       user01 ~ user10 / user1234
 * 사장님   /pLoginForm       boss01 ~ boss15 / boss1234
 * 라이더   /rLoginForm       rider01 ~ rider08 / rider1234
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

    private static final String DEMO_IP = "127.0.0.1";

    /** 매 분 도는 checkStoretime 이 영업시간 밖이면 상태를 중지로 덮는다. 캡처 시각과 무관하게 열려 있어야 한다. */
    private static final String OPEN_ALL_DAY = "00:00-23:59";
    private static final String OPEN_EVERY_DAY = "월,화,수,목,금,토,일";

    private static final String DELIVERY_AREA = "인천 미추홀구 주안동, 용현동, 숭의동, 도화동, 학익동";

    private static final int ORDER_COUNT = 48;

    /** 이 번호부터 끝까지가 배차 대기 주문이다. 라이더 배차 화면에 뜨는 것도 이 구간뿐이다. */
    private static final int DISPATCH_FROM = 41;

    /**
     * 배차 대기 주문 중 앞의 다섯 건은 같은 가게에 몰아 한 묶음으로 만든다.
     * 라이더 화면이 가게와 호출 시각으로 카드를 묶으므로, 가게가 다르면 카드가 1건씩 갈라져
     * 방문 순서 계산이 목적지를 둘 이상 받는 일이 없다. 목적지가 다섯이어야 방문 순서를
     * 바꿔 얻는 이득이 화면에서 눈에 보인다.
     */
    private static final int GROUP_DEMO_SIZE = 5;
    private static final int GROUP_DEMO_STORE_INDEX = 0;

    private record MenuSeed(String menuCategory, String name, long price, String description) {
    }

    private record StoreSeed(String name, String ownerUserId, String address, String phone, String intro,
                             int minOrderAmount, int deliveryFee, int deliveryTimeMin, int deliveryTimeMax,
                             String status) {
    }

    /** name 은 customer-main.html 의 data-category 12종과 글자 단위로 같아야 한다. */
    private record CategorySeed(String name, String icon, StoreSeed[] stores, MenuSeed[] menus) {
    }

    private record OwnerSeed(String userId, String ceoName, String bizRegNo, String phone, String approvalStatus) {
    }

    private record MemberSeed(String userId, String username, String nickname, String phone, LocalDate birthday,
                              String gender, long point, String grade, String address) {
    }

    /** lon·lat 는 업무 시작 지점이다. 데모 서비스 구역 안에 흩어 두어 라이더마다 경로가 달라지게 한다. */
    private record RiderSeed(String riderId, String name, String phone, String vehicleType, String gender,
                             String birth, int totalDeliveries, String isAvailable, double lon, double lat) {
    }

    private record AddressSeed(int memberIndex, String name, String address, String isMain) {
    }

    private record BoardSeed(int memberIndex, String title, String content, String answerStatus, int daysAgo) {
    }

    /** 주문과 리뷰를 만들 때 필요한 가게별 메뉴 정보를 들고 있는다. */
    private static final class SeededStore {
        private final Long id;
        private final String name;
        private final String address;
        private final int deliveryFee;
        private final List<Long> menuIds = new ArrayList<>();
        private final List<String> menuNames = new ArrayList<>();
        private final List<Long> menuPrices = new ArrayList<>();
        private final List<Integer> ratings = new ArrayList<>();

        private SeededStore(Long id, String name, String address, int deliveryFee) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.deliveryFee = deliveryFee;
        }
    }

    /** 배차 대기 주문. 배달 그룹을 만들 때 필요한 값만 들고 있는다. */
    private record DispatchOrder(Long orderId, Long storeId, String storeAddress, String userAddress,
                                 String deliveryType, int deliveryFee, String customerRequest) {
    }

    private static final OwnerSeed[] OWNERS = {
            new OwnerSeed("boss01", "정민석", "382-11-00471", "010-3391-7742", "승인"),
            new OwnerSeed("boss02", "한유진", "121-45-88213", "010-5580-2214", "승인"),
            new OwnerSeed("boss03", "오재원", "606-32-90118", "010-9042-3316", "승인"),
            new OwnerSeed("boss04", "임하늘", "755-19-40027", "010-2287-6650", "보류"),
            new OwnerSeed("boss05", "서지훈", "214-08-31159", "010-4471-8802", "승인"),
            new OwnerSeed("boss06", "문가영", "330-27-51940", "010-6613-2274", "승인"),
            new OwnerSeed("boss07", "배준호", "489-52-71026", "010-7729-4438", "승인"),
            new OwnerSeed("boss08", "신예린", "512-36-19483", "010-3358-9014", "승인"),
            new OwnerSeed("boss09", "강태오", "627-41-62205", "010-8846-5527", "승인"),
            new OwnerSeed("boss10", "윤소미", "148-73-20894", "010-2214-7763", "승인"),
            new OwnerSeed("boss11", "조현우", "903-15-47712", "010-5590-3308", "승인"),
            new OwnerSeed("boss12", "노은비", "271-64-83350", "010-6672-1149", "승인"),
            new OwnerSeed("boss13", "황시우", "356-29-11607", "010-4403-8875", "승인"),
            new OwnerSeed("boss14", "백서진", "740-58-92214", "010-9917-6640", "승인"),
            new OwnerSeed("boss15", "유하람", "085-33-70468", "010-3326-5591", "승인"),
    };

    private static final MemberSeed[] MEMBERS = {
            new MemberSeed("user01", "김도현", "도현", "010-2451-8830", LocalDate.of(1996, 4, 12),
                    "남성", 3200L, "Welcome", "인천광역시 미추홀구 인하로 67, 101동 1203호"),
            new MemberSeed("user02", "박서연", "서연", "010-7712-6094", LocalDate.of(1993, 11, 3),
                    "여성", 15400L, "VIP", "인천광역시 미추홀구 경인로 320, 502호"),
            new MemberSeed("user03", "이준서", "준서", "010-3308-2277", LocalDate.of(1990, 2, 17),
                    "남성", 8100L, "Family", "인천광역시 미추홀구 미추홀대로 716, 303호"),
            new MemberSeed("user04", "최유나", "유나", "010-5541-9903", LocalDate.of(1998, 8, 25),
                    "여성", 42600L, "VVIP", "인천광역시 미추홀구 매소홀로 488, 1801호"),
            new MemberSeed("user05", "정하람", "하람", "010-8827-4416", LocalDate.of(1988, 12, 9),
                    "남성", 6700L, "Family", "인천광역시 미추홀구 독배로 215, 201호"),
            new MemberSeed("user06", "강민지", "민지", "010-6690-3352", LocalDate.of(2000, 5, 30),
                    "여성", 1500L, "Welcome", "인천광역시 미추홀구 인주대로 402, 705호"),
            new MemberSeed("user07", "윤태현", "태현", "010-4472-7719", LocalDate.of(1995, 1, 14),
                    "남성", 19800L, "VIP", "인천광역시 미추홀구 소성로 128, 902호"),
            new MemberSeed("user08", "임소율", "소율", "010-2263-5580", LocalDate.of(1992, 6, 21),
                    "여성", 36200L, "VVIP", "인천광역시 미추홀구 한나루로 331, 604호"),
            new MemberSeed("user09", "오지훈", "지훈", "010-9914-6608", LocalDate.of(1999, 9, 5),
                    "남성", 900L, "Welcome", "인천광역시 미추홀구 낙섬중로 44, 108호"),
            new MemberSeed("user10", "배수아", "수아", "010-7735-2240", LocalDate.of(1991, 3, 28),
                    "여성", 11300L, "Family", "인천광역시 미추홀구 학익소로 76, 1502호"),
    };

    private static final RiderSeed[] RIDERS = {
            new RiderSeed("rider01", "최준영", "010-8820-4471", "오토바이", "남성", "1994-07-21", 312, "승인",
                    126.675113, 37.438894),
            new RiderSeed("rider02", "이가온", "010-4417-9902", "자전거", "여성", "1999-02-08", 0, "보류",
                    126.641820, 37.462310),
            new RiderSeed("rider03", "박시현", "010-3352-6614", "오토바이", "남성", "1991-05-16", 874, "승인",
                    126.688470, 37.427650),
            new RiderSeed("rider04", "김도윤", "010-7708-1193", "오토바이", "남성", "1997-10-02", 526, "승인",
                    126.653290, 37.471180),
            new RiderSeed("rider05", "한예승", "010-2249-8837", "자전거", "여성", "2001-01-23", 141, "승인",
                    126.629740, 37.436520),
            new RiderSeed("rider06", "신재훈", "010-9963-4402", "오토바이", "남성", "1989-03-11", 1208, "승인",
                    126.694110, 37.455930),
            new RiderSeed("rider07", "문가온", "010-5518-7726", "도보", "여성", "2002-11-19", 0, "보류",
                    126.662580, 37.419470),
            new RiderSeed("rider08", "조은결", "010-6640-3319", "오토바이", "남성", "1996-08-07", 349, "승인",
                    126.636050, 37.448860),
    };

    private static final AddressSeed[] ADDRESSES = {
            new AddressSeed(0, "집", "인천광역시 미추홀구 인하로 67, 101동 1203호", "메인"),
            new AddressSeed(0, "회사", "인천광역시 남동구 정각로 29, 12층", "서브"),
            new AddressSeed(1, "회사", "인천광역시 미추홀구 경인로 320, 502호", "메인"),
            new AddressSeed(1, "본가", "인천광역시 연수구 컨벤시아대로 165, 803호", "서브"),
            new AddressSeed(2, "집", "인천광역시 미추홀구 미추홀대로 716, 303호", "메인"),
            new AddressSeed(3, "집", "인천광역시 미추홀구 매소홀로 488, 1801호", "메인"),
            new AddressSeed(3, "기숙사", "인천광역시 미추홀구 인하로 100, 생활관 4동", "서브"),
            new AddressSeed(4, "집", "인천광역시 미추홀구 독배로 215, 201호", "메인"),
            new AddressSeed(5, "집", "인천광역시 미추홀구 인주대로 402, 705호", "메인"),
            new AddressSeed(6, "집", "인천광역시 미추홀구 소성로 128, 902호", "메인"),
            new AddressSeed(6, "회사", "인천광역시 미추홀구 경원대로 881, 6층", "서브"),
            new AddressSeed(7, "집", "인천광역시 미추홀구 한나루로 331, 604호", "메인"),
            new AddressSeed(8, "집", "인천광역시 미추홀구 낙섬중로 44, 108호", "메인"),
            new AddressSeed(9, "집", "인천광역시 미추홀구 학익소로 76, 1502호", "메인"),
            new AddressSeed(9, "친구집", "인천광역시 미추홀구 용현로 55, 302호", "서브"),
    };

    private static final BoardSeed[] BOARDS = {
            new BoardSeed(0, "배달이 지연되면 어디로 문의하나요",
                    "주문 후 예상 시간이 20분 넘게 지났는데 라이더 연락이 없습니다. 문의 창구를 알려주세요.", "미답변", 1),
            new BoardSeed(1, "결제 수단을 변경하고 싶습니다",
                    "간편결제로 등록한 카드를 다른 카드로 바꾸려는데 마이페이지에서 찾지 못했습니다.", "답변완료", 4),
            new BoardSeed(0, "리뷰에 올린 사진을 지우고 싶습니다",
                    "작성한 리뷰는 남기고 첨부한 사진만 삭제할 수 있는지 알고 싶습니다.", "미답변", 6),
            new BoardSeed(2, "쿠폰이 장바구니에서 적용되지 않습니다",
                    "최소 주문 금액을 넘겼는데도 쿠폰 목록에 뜨지 않습니다. 사용 조건을 확인해 주세요.", "답변완료", 2),
            new BoardSeed(3, "등급 산정 기준이 궁금합니다",
                    "최근 석 달 주문 금액이 늘었는데 등급이 그대로입니다. 갱신 주기를 알려주세요.", "미답변", 3),
            new BoardSeed(4, "배달 주소를 여러 개 저장할 수 있나요",
                    "집과 회사를 번갈아 쓰는데 매번 새로 입력하고 있습니다. 기본 배송지 변경 방법을 알고 싶습니다.", "답변완료", 5),
            new BoardSeed(5, "주문 취소는 언제까지 가능한가요",
                    "결제 직후 취소를 눌렀는데 이미 조리가 시작됐다는 안내를 받았습니다. 기준 시각이 어떻게 되나요.", "미답변", 7),
            new BoardSeed(6, "영수증을 다시 받고 싶습니다",
                    "지난주 주문 두 건의 현금영수증을 발급받지 못했습니다. 재발급 절차를 알려주세요.", "답변완료", 8),
            new BoardSeed(7, "묶음배달과 한집배달 차이가 무엇인가요",
                    "묶음배달이 더 싸다고 들었는데 도착 시간 차이가 어느 정도인지 궁금합니다.", "미답변", 10),
            new BoardSeed(9, "포인트 유효기간을 알려주세요",
                    "적립된 포인트가 줄어든 것 같습니다. 소멸 시점과 확인 방법을 알고 싶습니다.", "답변완료", 12),
    };

    /** 별점별 리뷰 문구. 인덱스는 별점에서 1을 뺀 값이다. */
    private static final String[][] REVIEW_TEXTS = {
            {
                    "주문한 메뉴가 아예 다른 것으로 왔습니다. 연락도 닿지 않았습니다.",
                    "한 시간 반이 걸렸고 음식은 완전히 식어 있었습니다.",
                    "국물이 절반 넘게 새어 봉투가 젖은 채로 도착했습니다.",
                    "요청한 사항이 하나도 반영되지 않았습니다.",
            },
            {
                    "양이 사진과 많이 다릅니다. 가격을 생각하면 아쉽습니다.",
                    "면이 불어서 왔습니다. 조리 후 대기 시간이 길었던 것 같습니다.",
                    "간이 너무 짜서 절반만 먹었습니다.",
                    "포장이 기울어져 소스가 흘렀습니다.",
            },
            {
                    "맛은 무난했지만 예상 시간보다 15분 정도 늦었습니다.",
                    "기본은 하는 맛입니다. 반찬 구성이 조금 단조롭습니다.",
                    "적당했습니다. 다음에는 다른 메뉴를 시켜보겠습니다.",
                    "가격 대비 평범합니다. 배달은 무난했습니다.",
            },
            {
                    "재료가 신선하고 간도 적당합니다. 배달도 제시간에 왔습니다.",
                    "포장이 꼼꼼해서 흐르지 않았습니다. 양은 조금 아쉽습니다.",
                    "요청 사항을 그대로 반영해 주셨습니다. 재주문 의사 있습니다.",
                    "따뜻하게 도착했고 맛도 안정적입니다.",
            },
            {
                    "예상 시간보다 빨랐고 음식도 뜨거운 상태로 왔습니다.",
                    "이 동네에서 이 가격에 이 정도 퀄리티는 드뭅니다. 계속 시킬 것 같습니다.",
                    "반찬까지 정갈했습니다. 가족 모두 만족했습니다.",
                    "재료 손질이 눈에 보일 만큼 깔끔합니다. 추천합니다.",
            },
    };

    private static final int[] RATING_CYCLE = {5, 4, 5, 3, 4, 5, 2, 4, 5, 4, 3, 5, 1, 4, 5, 4, 5, 3, 4, 2};

    private static final String[] CUSTOMER_MESSAGES = {
            "덜 맵게 부탁드립니다.",
            "일회용 수저는 빼주세요.",
            "소스는 따로 담아주세요.",
            "튀김은 바삭하게 부탁드립니다.",
            "아이가 먹어서 간을 조금만 해주세요.",
            "피클은 빼주세요.",
            "밥은 공기밥으로 하나 더 넣어주세요.",
            "포장 꼼꼼하게 부탁드립니다.",
    };

    private static final String[] DELIVERY_MESSAGES = {
            "공동현관 비밀번호 1204#",
            "문 앞에 놓고 연락 주세요.",
            "경비실에 맡겨 주세요.",
            "엘리베이터가 점검 중입니다. 계단으로 올라와 주세요.",
            "벨은 누르지 말아 주세요. 아이가 자고 있습니다.",
            "정문 말고 후문으로 와주세요.",
            "도착 5분 전에 전화 부탁드립니다.",
            "주차는 건물 뒤편에 가능합니다.",
    };

    private static final String[] PAYMENT_METHODS = {"카드", "현금", "온라인"};
    private static final String[] DELIVERY_TYPES = {"한집배달", "묶음배달"};

    private static final CategorySeed[] CATEGORIES = {
            new CategorySeed("패스트푸드", ICON + "음식_01_패스트푸드.png",
                    new StoreSeed[]{
                            new StoreSeed("주안역 버거하우스", "boss09", "인천광역시 미추홀구 미추홀대로 605", "032-421-3308",
                                    "패티는 주문이 들어온 뒤에 굽습니다. 냉동 패티를 쓰지 않습니다.",
                                    12000, 2500, 20, 35, "승인"),
                            new StoreSeed("인하대 버거스탠드", "boss09", "인천광역시 미추홀구 인하로 92", "032-873-5520",
                                    "번은 매일 아침 인근 제빵소에서 받아 씁니다.",
                                    13000, 2000, 20, 30, "승인"),
                                                new StoreSeed("학익동 버거공방", "boss02", "인천광역시 미추홀구 학익소로 76", "032-864-2210",
                                    "패티를 매장에서 직접 갈아 굽습니다. 번은 매일 아침 들여옵니다.",
                                    12000, 2500, 25, 40, "승인"),
                            new StoreSeed("용현 샌드위치바", "boss10", "인천광역시 미추홀구 용현동 412-9", "032-885-6634",
                                    "주문 즉시 만들어 포장합니다. 채소는 당일 손질분만 씁니다.",
                                    10000, 2500, 20, 35, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("버거", "불고기 버거 세트", 7500L, "버거에 감자튀김과 음료를 묶은 세트"),
                            new MenuSeed("버거", "치즈버거 세트", 8000L, "체다 두 장을 겹친 버거 세트"),
                            new MenuSeed("버거", "더블 패티 버거", 9500L, "패티를 두 장 넣은 단품"),
                            new MenuSeed("버거", "새우 버거", 6500L, "통새우살을 뭉쳐 튀긴 패티 단품"),
                            new MenuSeed("사이드", "치킨 텐더 다섯 조각", 6000L, "닭안심을 튀겨 소스 두 종을 곁들인 사이드"),
                            new MenuSeed("사이드", "어니언링", 3500L, "양파를 두툼하게 썰어 튀긴 사이드"),
                            new MenuSeed("사이드", "감자튀김 라지", 3000L, "굵게 썰어 두 번 튀긴 사이드"),
                            new MenuSeed("음료", "콜라 라지", 2500L, "얼음을 채워 담는 음료"),
                    }),
            new CategorySeed("치킨", ICON + "음식_02_치킨.png",
                    new StoreSeed[]{
                            new StoreSeed("주안통닭 본점", "boss01", "인천광역시 미추홀구 미추홀대로 716", "032-421-7788",
                                    "매일 아침 국내산 생닭만 손질해 씁니다. 튀김옷은 얇게, 기름은 하루 두 번 갈아 씁니다.",
                                    16000, 3000, 30, 45, "승인"),
                            new StoreSeed("주안통닭 용현점", "boss01", "인천광역시 미추홀구 용현동 627-14", "032-882-7789",
                                    "본점과 같은 염지 배합을 씁니다. 주문 후 튀기므로 대기가 있습니다.",
                                    16000, 3000, 30, 50, "승인"),
                            new StoreSeed("주안통닭 도화점", "boss01", "인천광역시 미추홀구 도화동 429-8", "032-765-7790",
                                    "세 번째 지점입니다. 개점 준비 중이라 메뉴가 일부만 열려 있습니다.",
                                    15000, 3000, 30, 45, "보류"),
                                                new StoreSeed("도화 옛날통닭", "boss05", "인천광역시 미추홀구 도화로 78", "032-772-4417",
                                    "가마솥에 한 번에 두 마리만 튀깁니다. 대기가 깁니다.",
                                    17000, 3000, 35, 55, "승인"),
                            new StoreSeed("숭의 치킨공방", "boss11", "인천광역시 미추홀구 매소홀로 320", "032-891-3325",
                                    "염지를 하루 두고 씁니다. 순살은 다리살만 씁니다.",
                                    18000, 3500, 30, 50, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("치킨", "후라이드 치킨", 18000L, "얇은 튀김옷에 소금 간만 해서 튀긴 기본 메뉴"),
                            new MenuSeed("치킨", "양념 치킨", 19000L, "고추장 양념을 두 번 바르고 한 번 더 구워낸 메뉴"),
                            new MenuSeed("치킨", "반반 치킨", 19500L, "후라이드와 양념을 반씩 담은 구성"),
                            new MenuSeed("치킨", "간장 마늘 치킨", 20000L, "간장 베이스에 구운 마늘을 얹은 메뉴"),
                            new MenuSeed("치킨", "순살 크리스피", 21000L, "닭다리살만 발라 튀긴 뼈 없는 메뉴"),
                            new MenuSeed("사이드", "치즈볼", 5000L, "모차렐라를 넣어 튀긴 사이드 다섯 개"),
                            new MenuSeed("사이드", "감자튀김", 4000L, "치즈 시즈닝을 따로 담아 보내는 사이드"),
                            new MenuSeed("음료", "콜라 1.25리터", 2500L, "얼음컵을 함께 담아 보내는 음료"),
                    }),
            new CategorySeed("피자", ICON + "음식_03_피자.png",
                    new StoreSeed[]{
                            new StoreSeed("숭의동 화덕피자", "boss03", "인천광역시 미추홀구 매소홀로 488", "032-765-2093",
                                    "장작 화덕에서 400도로 90초간 굽습니다. 도우는 저온에서 48시간 숙성합니다.",
                                    18000, 3500, 35, 50, "승인"),
                            new StoreSeed("도화동 피자공방", "boss03", "인천광역시 미추홀구 도화로 55", "032-765-2094",
                                    "치즈는 자연치즈만 씁니다. 라지 사이즈는 14인치입니다.",
                                    17000, 3000, 30, 45, "승인"),
                                                new StoreSeed("주안 나폴리피자", "boss07", "인천광역시 미추홀구 미추홀대로 682", "032-427-8890",
                                    "도우를 48시간 저온 숙성합니다. 화덕은 참나무만 씁니다.",
                                    19000, 3000, 30, 45, "승인"),
                            new StoreSeed("인하대 슬라이스", "boss12", "인천광역시 미추홀구 인하로 67", "032-874-1102",
                                    "조각 단위로도 팝니다. 학생 할인은 매장 방문만 됩니다.",
                                    13000, 2500, 25, 40, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("피자", "마르게리타 피자", 15900L, "토마토 소스와 생모차렐라, 바질만 올린 메뉴"),
                            new MenuSeed("피자", "페퍼로니 피자", 17900L, "페퍼로니를 두 겹으로 덮은 메뉴"),
                            new MenuSeed("피자", "고구마 피자", 18900L, "구운 고구마 무스를 테두리까지 채운 메뉴"),
                            new MenuSeed("피자", "불고기 피자", 19900L, "직접 재운 불고기와 양파를 올린 메뉴"),
                            new MenuSeed("피자", "하와이안 피자", 18500L, "파인애플과 햄을 함께 올린 메뉴"),
                            new MenuSeed("파스타", "갈릭 크림 파스타", 12900L, "구운 마늘을 갈아 넣은 크림 파스타"),
                            new MenuSeed("파스타", "치즈 오븐 스파게티", 13900L, "토마토 소스에 치즈를 덮어 구운 메뉴"),
                            new MenuSeed("사이드", "갈릭 브레드", 4500L, "마늘 버터를 발라 구운 사이드"),
                    }),
            new CategorySeed("양식", ICON + "음식_04_양식.png",
                    new StoreSeed[]{
                            new StoreSeed("미추홀 스테이크하우스", "boss12", "인천광역시 미추홀구 경인로 288", "032-433-1180",
                                    "고기는 숙성 28일 이상만 씁니다. 굽기는 미디엄을 기본으로 합니다.",
                                    20000, 3500, 35, 55, "승인"),
                            new StoreSeed("용현동 파스타공방", "boss12", "인천광역시 미추홀구 용현로 88", "032-882-1181",
                                    "생면을 매일 뽑습니다. 소스는 당일 만든 것만 나갑니다.",
                                    15000, 3000, 30, 45, "승인"),
                                                new StoreSeed("학익 파스타공방", "boss03", "인천광역시 미추홀구 학익동 654-2", "032-866-7741",
                                    "면을 매일 아침 뽑습니다. 소스는 주문 후 조립합니다.",
                                    16000, 3000, 30, 45, "승인"),
                            new StoreSeed("용현 스테이크하우스", "boss13", "인천광역시 미추홀구 독배로 215", "032-883-9920",
                                    "숙성고를 직접 돌립니다. 굽기는 미디엄까지만 받습니다.",
                                    25000, 3500, 35, 55, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("스테이크", "안심 스테이크", 27000L, "180그램 안심을 미디엄으로 구워 낸 메뉴"),
                            new MenuSeed("스테이크", "등심 스테이크", 24000L, "채끝 등심에 통후추 소스를 곁들인 메뉴"),
                            new MenuSeed("리조또", "크림 리조또", 14000L, "생크림과 파르메산으로 마무리한 리조또"),
                            new MenuSeed("파스타", "토마토 파스타", 13000L, "홀토마토를 졸여 만든 소스 파스타"),
                            new MenuSeed("파스타", "로제 파스타", 14000L, "토마토와 크림을 반씩 섞은 파스타"),
                            new MenuSeed("안주", "감바스 알 아히요", 16000L, "새우와 마늘을 올리브유에 익힌 메뉴"),
                            new MenuSeed("샐러드", "시저 샐러드", 9000L, "로메인에 앤초비 드레싱을 얹은 메뉴"),
                            new MenuSeed("사이드", "트러플 감자튀김", 7000L, "트러플 오일을 두른 사이드"),
                    }),
            new CategorySeed("중식", ICON + "음식_05_중식.png",
                    new StoreSeed[]{
                            new StoreSeed("신포동 짬뽕관", "boss05", "인천광역시 미추홀구 인주대로 371", "032-772-4410",
                                    "육수는 매일 아침 사골과 해물을 따로 끓여 씁니다. 면은 주문 후 삶습니다.",
                                    14000, 2500, 25, 40, "승인"),
                            new StoreSeed("용현 반점", "boss05", "인천광역시 미추홀구 독배로 190", "032-882-4411",
                                    "춘장을 세 번 볶아 씁니다. 탕수육은 등심만 씁니다.",
                                    13000, 2000, 25, 40, "승인"),
                                                new StoreSeed("도화 홍콩반점", "boss06", "인천광역시 미추홀구 인주대로 402", "032-763-5518",
                                    "불맛은 센 불에서만 납니다. 짬뽕 육수는 매일 새로 냅니다.",
                                    14000, 3000, 25, 45, "승인"),
                            new StoreSeed("주안 사천각", "boss14", "인천광역시 미추홀구 주안동 1130", "032-429-6672",
                                    "고추기름을 직접 내려 씁니다. 매운 정도는 세 단계입니다.",
                                    15000, 3000, 30, 50, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("면", "짜장면", 7000L, "춘장을 세 번 볶아 낸 소스에 비빈 면"),
                            new MenuSeed("면", "짬뽕", 9000L, "불맛을 낸 해물 육수에 면을 말아 낸 메뉴"),
                            new MenuSeed("면", "간짜장", 8000L, "소스를 따로 담아 보내는 면"),
                            new MenuSeed("요리", "탕수육 소자", 16000L, "등심을 두 번 튀겨 소스를 따로 담은 요리"),
                            new MenuSeed("요리", "유린기", 19000L, "튀긴 닭에 간장 소스를 부어 낸 요리"),
                            new MenuSeed("밥", "볶음밥", 8000L, "계란과 대파를 센 불에 볶은 밥"),
                            new MenuSeed("밥", "마파두부밥", 9000L, "두반장으로 볶은 두부를 밥에 올린 메뉴"),
                            new MenuSeed("사이드", "군만두", 5000L, "여섯 개를 팬에 지져 낸 사이드"),
                    }),
            new CategorySeed("한식", ICON + "음식_06_한식.png",
                    new StoreSeed[]{
                            new StoreSeed("주안 순두부집", "boss14", "인천광역시 미추홀구 주안로 142", "032-422-6612",
                                    "손두부를 매일 아침 받아 씁니다. 국물은 다시마와 멸치로만 냅니다.",
                                    12000, 2500, 25, 40, "승인"),
                            new StoreSeed("미추홀 한상차림", "boss15", "인천광역시 미추홀구 소성로 214", "032-433-6613",
                                    "반찬 다섯 가지를 매일 아침에 새로 만듭니다.",
                                    13000, 3000, 30, 45, "승인"),
                            new StoreSeed("용현동 갈비탕집", "boss04", "인천광역시 미추홀구 용현동 627-31", "032-882-4417",
                                    "소갈비를 세 시간 고아 냅니다. 개점 서류를 심사받고 있습니다.",
                                    14000, 3000, 30, 45, "보류"),
                                                new StoreSeed("숭의 청국장집", "boss08", "인천광역시 미추홀구 한나루로 331", "032-892-4408",
                                    "콩은 국내산만 씁니다. 청국장은 사흘에 한 번 띄웁니다.",
                                    12000, 2500, 25, 40, "승인"),
                            new StoreSeed("학익 순대국밥", "boss15", "인천광역시 미추홀구 소성로 128", "032-867-3390",
                                    "사골을 열두 시간 고아 냅니다. 새벽 여섯 시에 엽니다.",
                                    11000, 2500, 20, 35, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("찌개", "김치찌개", 9000L, "6개월 묵은지에 앞다리살을 넣고 끓인 찌개"),
                            new MenuSeed("찌개", "된장찌개", 8500L, "국산 콩 된장에 두부와 애호박을 넣은 찌개"),
                            new MenuSeed("찌개", "순두부찌개", 9000L, "손두부를 그날 받아 끓이는 찌개"),
                            new MenuSeed("볶음", "제육볶음", 11000L, "앞다리살을 고추장 양념에 재워 센 불에 볶은 메뉴"),
                            new MenuSeed("백반", "불고기 백반", 12000L, "간장에 재운 불고기와 반찬 다섯 가지 구성"),
                            new MenuSeed("탕", "갈비탕", 13000L, "소갈비를 세 시간 고아 낸 탕"),
                            new MenuSeed("백반", "비빔밥", 9500L, "나물 다섯 가지와 약고추장을 곁들인 메뉴"),
                            new MenuSeed("사이드", "계란찜", 4000L, "뚝배기에 부풀려 낸 사이드"),
                    }),
            new CategorySeed("일식", ICON + "음식_12_일식.png",
                    new StoreSeed[]{
                            new StoreSeed("주안 초밥연구소", "boss06", "인천광역시 미추홀구 미추홀대로 640", "032-421-9930",
                                    "생선은 새벽 경매분만 씁니다. 밥은 30분 단위로 새로 짓습니다.",
                                    18000, 3000, 30, 45, "승인"),
                            new StoreSeed("도화동 라멘공방", "boss06", "인천광역시 미추홀구 도화로 122", "032-765-9931",
                                    "돼지뼈 육수를 12시간 끓입니다. 면은 저가수분 중면을 씁니다.",
                                    13000, 2500, 25, 40, "승인"),
                                                new StoreSeed("용현 스시하나", "boss04", "인천광역시 미추홀구 용현동 598-7", "032-887-2214",
                                    "생선은 새벽 경매분만 씁니다. 밥은 30분 단위로 새로 짓습니다.",
                                    22000, 3500, 30, 50, "승인"),
                            new StoreSeed("주안 돈카츠공방", "boss09", "인천광역시 미추홀구 미추홀대로 540", "032-424-7715",
                                    "등심은 두께를 두 가지로 냅니다. 빵가루는 직접 갈아 씁니다.",
                                    14000, 3000, 25, 40, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("초밥", "연어 초밥 세트", 18000L, "연어 여덟 점을 담은 구성"),
                            new MenuSeed("초밥", "모둠 초밥 세트", 21000L, "흰살생선과 붉은살을 섞어 담은 열 점 구성"),
                            new MenuSeed("덮밥", "사케동", 15000L, "생연어를 밥 위에 올린 덮밥"),
                            new MenuSeed("덮밥", "가츠동", 11000L, "등심 돈가스를 계란에 익혀 올린 덮밥"),
                            new MenuSeed("라멘", "돈코츠 라멘", 10000L, "돼지뼈를 열두 시간 고아 낸 국물 라멘"),
                            new MenuSeed("라멘", "미소 라멘", 9500L, "된장 육수에 숙주와 차슈를 올린 라멘"),
                            new MenuSeed("면", "우동", 8000L, "가다랑어포 육수에 튀김 부스러기를 얹은 메뉴"),
                            new MenuSeed("사이드", "새우튀김", 7000L, "대하 세 마리를 튀긴 사이드"),
                    }),
            new CategorySeed("족발·보쌈", ICON + "음식_11_족발_보쌈.png",
                    new StoreSeed[]{
                            new StoreSeed("주안골 족발", "boss07", "인천광역시 미추홀구 주안로 88", "032-422-2280",
                                    "여덟 시간 삶아 기름을 걷어 냅니다. 잡내를 잡으려고 한약재를 씁니다.",
                                    20000, 3000, 35, 50, "승인"),
                            new StoreSeed("숭의동 보쌈정식", "boss07", "인천광역시 미추홀구 매소홀로 320", "032-765-2281",
                                    "김치는 매장에서 담급니다. 1인분 정식도 배달합니다.",
                                    15000, 2500, 30, 45, "승인"),
                                                new StoreSeed("도화 족발상회", "boss01", "인천광역시 미추홀구 도화동 511-3", "032-771-8823",
                                    "앞다리만 씁니다. 삶은 뒤 한 번 식혀 썰어 냅니다.",
                                    20000, 3000, 35, 55, "승인"),
                            new StoreSeed("숭의 보쌈정식", "boss11", "인천광역시 미추홀구 매소홀로 415", "032-894-1176",
                                    "김치는 매장에서 담급니다. 수육은 주문 후 썹니다.",
                                    19000, 3000, 30, 50, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("족발", "앞다리 족발 중자", 29000L, "여덟 시간 삶아 기름을 걷어 낸 족발"),
                            new MenuSeed("족발", "불족발 중자", 31000L, "매운 양념을 발라 초벌한 족발"),
                            new MenuSeed("보쌈", "보쌈 중자", 28000L, "삼겹 부위를 통으로 삶아 썰어 낸 보쌈"),
                            new MenuSeed("보쌈", "보쌈 정식", 15000L, "1인분 보쌈에 김치와 국을 곁들인 구성"),
                            new MenuSeed("면", "막국수", 8000L, "메밀면에 동치미 국물을 부어 낸 메뉴"),
                            new MenuSeed("면", "쟁반국수", 12000L, "채소와 면을 새콤한 양념에 버무린 메뉴"),
                            new MenuSeed("탕", "순대국", 9000L, "사골 육수에 순대와 머릿고기를 담은 탕"),
                            new MenuSeed("사이드", "계란찜", 4000L, "뚝배기에 부풀려 낸 사이드"),
                    }),
            new CategorySeed("분식", ICON + "음식_10_분식.png",
                    new StoreSeed[]{
                            new StoreSeed("미추홀 분식당", "boss02", "인천광역시 미추홀구 인하로 100", "032-873-1140",
                                    "떡은 당일 뽑은 쌀떡만 씁니다. 매운맛은 세 단계로 조절됩니다.",
                                    12000, 2500, 20, 35, "승인"),
                            new StoreSeed("인하로 떡볶이집", "boss02", "인천광역시 미추홀구 인하로 143", "032-873-1141",
                                    "국물떡볶이만 합니다. 어묵 국물은 무료로 담아 드립니다.",
                                    11000, 2000, 20, 30, "승인"),
                            new StoreSeed("주안 김밥천지", "boss13", "인천광역시 미추홀구 주안로 51", "032-422-1142",
                                    "김밥은 주문 후 말아서 보냅니다. 밥에 참기름을 적게 씁니다.",
                                    10000, 2000, 15, 30, "승인"),
                                                new StoreSeed("인하대 떡볶이집", "boss02", "인천광역시 미추홀구 인하로 92", "032-875-3308",
                                    "밀떡과 쌀떡을 고를 수 있습니다. 국물은 매일 아침 새로 냅니다.",
                                    9000, 2000, 20, 35, "승인"),
                            new StoreSeed("주안 김밥천국", "boss10", "인천광역시 미추홀구 주안동 875-4", "032-428-5540",
                                    "김밥은 주문 후 맙니다. 밥은 두 시간마다 새로 짓습니다.",
                                    8000, 2000, 15, 30, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("분식", "떡볶이", 5500L, "당일 뽑은 쌀떡에 어묵과 대파를 넣고 끓인 기본 메뉴"),
                            new MenuSeed("분식", "로제 떡볶이", 7000L, "생크림을 넣어 매운맛을 눌러낸 메뉴"),
                            new MenuSeed("분식", "순대", 5500L, "찰순대에 간과 허파를 곁들인 메뉴"),
                            new MenuSeed("튀김", "튀김 모둠", 5000L, "오징어, 고구마, 김말이를 두 개씩 담은 구성"),
                            new MenuSeed("김밥", "김밥", 4000L, "단무지와 우엉을 넣은 기본 김밥"),
                            new MenuSeed("김밥", "참치김밥", 5000L, "참치와 마요네즈를 따로 버무려 넣은 김밥"),
                            new MenuSeed("분식", "라면", 4500L, "계란과 대파를 넣고 끓인 냄비 라면"),
                            new MenuSeed("분식", "쫄면", 6000L, "새콤한 양념에 채썬 채소를 올린 메뉴"),
                    }),
            new CategorySeed("샐러드", ICON + "음식_09_샐러드.png",
                    new StoreSeed[]{
                            new StoreSeed("미추홀 샐러드바", "boss10", "인천광역시 미추홀구 경인로 401", "032-433-7720",
                                    "채소는 매일 아침 두 번 받습니다. 드레싱은 따로 담아 보냅니다.",
                                    12000, 2500, 20, 35, "승인"),
                            new StoreSeed("인하대 포케볼", "boss10", "인천광역시 미추홀구 인하로 78", "032-873-7721",
                                    "연어는 당일 손질분만 씁니다. 밥은 현미와 백미 중에 고를 수 있습니다.",
                                    13000, 2500, 20, 35, "승인"),
                                                new StoreSeed("학익 그린볼", "boss07", "인천광역시 미추홀구 학익동 703-1", "032-869-2247",
                                    "채소는 당일 입고분만 씁니다. 드레싱은 다섯 종류입니다.",
                                    11000, 2500, 20, 35, "승인"),
                            new StoreSeed("용현 포케하우스", "boss13", "인천광역시 미추홀구 용현동 445-6", "032-886-9931",
                                    "연어는 당일 손질분만 냅니다. 밥은 현미로 바꿀 수 있습니다.",
                                    13000, 2500, 25, 40, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("샐러드", "닭가슴살 샐러드", 9500L, "훈제 닭가슴살을 올린 기본 샐러드"),
                            new MenuSeed("포케", "연어 포케", 13000L, "생연어와 현미밥을 담은 한 그릇 메뉴"),
                            new MenuSeed("샐러드", "리코타 치즈 샐러드", 11000L, "매장에서 굳힌 리코타를 올린 샐러드"),
                            new MenuSeed("샐러드", "아보카도 샐러드", 12000L, "아보카도 한 개를 통째로 올린 샐러드"),
                            new MenuSeed("샐러드", "수비드 스테이크 샐러드", 14500L, "저온 조리한 등심을 썰어 올린 샐러드"),
                            new MenuSeed("볼", "그릭 요거트 볼", 8500L, "그릭 요거트에 견과와 과일을 얹은 메뉴"),
                            new MenuSeed("랩", "통밀 치킨 랩", 7500L, "통밀 또띠아에 채소와 닭가슴살을 만 메뉴"),
                            new MenuSeed("음료", "사과 오이 주스", 5000L, "사과와 오이만 갈아 낸 착즙 음료"),
                    }),
            new CategorySeed("카페·디저트", ICON + "음식_08_카페_디저트.png",
                    new StoreSeed[]{
                            new StoreSeed("주안 로스터리", "boss08", "인천광역시 미추홀구 미추홀대로 682", "032-421-5560",
                                    "원두는 매장에서 주 2회 볶습니다. 산미가 낮은 배합을 기본으로 씁니다.",
                                    9000, 2000, 15, 30, "승인"),
                            new StoreSeed("도화동 디저트카페", "boss08", "인천광역시 미추홀구 도화로 78", "032-765-5561",
                                    "케이크는 당일 생산분만 판매합니다. 생크림은 동물성만 씁니다.",
                                    10000, 2500, 20, 35, "승인"),
                            new StoreSeed("용현동 커피공방", "boss08", "인천광역시 미추홀구 용현로 133", "032-882-5562",
                                    "세 번째 지점입니다. 위생 서류를 제출하고 심사를 기다리고 있습니다.",
                                    9000, 2000, 15, 30, "보류"),
                                                new StoreSeed("도화 로스터리", "boss06", "인천광역시 미추홀구 도화로 152", "032-766-4413",
                                    "주 2회 직접 볶습니다. 원두는 볶은 날짜를 적어 냅니다.",
                                    9000, 2000, 20, 35, "승인"),
                            new StoreSeed("주안 케이크공방", "boss14", "인천광역시 미추홀구 미추홀대로 611", "032-425-7780",
                                    "생크림은 당일분만 씁니다. 홀케이크는 하루 전에 받습니다.",
                                    15000, 2500, 25, 40, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("커피", "아메리카노", 3000L, "산미를 낮춘 원두를 두 샷 내린 커피"),
                            new MenuSeed("커피", "카페라떼", 3800L, "우유 거품을 얇게 올린 커피"),
                            new MenuSeed("커피", "바닐라 라떼", 4300L, "바닐라 시럽을 넣어 단맛을 더한 커피"),
                            new MenuSeed("디저트", "딸기 생크림 케이크", 6500L, "생딸기를 층마다 넣은 조각 케이크"),
                            new MenuSeed("디저트", "티라미수", 6000L, "마스카포네를 두 층으로 쌓은 조각 디저트"),
                            new MenuSeed("디저트", "크로플", 5500L, "크루아상 반죽을 눌러 구운 디저트"),
                            new MenuSeed("음료", "자몽 에이드", 5000L, "자몽 과육을 그대로 넣은 음료"),
                            new MenuSeed("음료", "흑임자 스무디", 5800L, "볶은 검은깨를 갈아 넣은 음료"),
                    }),
            new CategorySeed("도시락", ICON + "음식_07_도시락.png",
                    new StoreSeed[]{
                            new StoreSeed("미추홀 도시락공장", "boss11", "인천광역시 미추홀구 학익소로 33", "032-873-3340",
                                    "반찬은 조리 후 두 시간이 지나면 폐기합니다. 단체 주문은 전날까지 받습니다.",
                                    10000, 2000, 20, 35, "승인"),
                            new StoreSeed("주안역 반찬도시락", "boss11", "인천광역시 미추홀구 주안로 24", "032-422-3341",
                                    "밥은 30분 단위로 새로 짓습니다. 국은 따로 밀봉해 보냅니다.",
                                    11000, 2500, 20, 35, "승인"),
                                                new StoreSeed("숭의 반찬도시락", "boss08", "인천광역시 미추홀구 한나루로 208", "032-895-6614",
                                    "반찬은 매일 아침 다섯 가지를 새로 만듭니다.",
                                    8000, 2000, 20, 35, "승인"),
                            new StoreSeed("학익 한상도시락", "boss15", "인천광역시 미추홀구 소성로 244", "032-868-1129",
                                    "단체 주문은 전날까지 받습니다. 국은 따로 담아 보냅니다.",
                                    9000, 2000, 25, 40, "승인"),
},
                    new MenuSeed[]{
                            new MenuSeed("도시락", "제육 도시락", 7500L, "제육볶음에 반찬 세 가지를 담은 구성"),
                            new MenuSeed("도시락", "불고기 도시락", 8000L, "간장 불고기에 계란말이를 곁들인 구성"),
                            new MenuSeed("도시락", "돈까스 도시락", 7800L, "등심 돈까스에 소스를 따로 담은 구성"),
                            new MenuSeed("도시락", "치킨마요 도시락", 6500L, "튀긴 닭에 마요 소스를 올린 덮밥형 구성"),
                            new MenuSeed("덮밥", "김치제육 덮밥", 7000L, "묵은지와 앞다리살을 함께 볶은 덮밥"),
                            new MenuSeed("덮밥", "스팸 김치볶음밥", 7200L, "스팸을 깍둑 썰어 볶은 밥"),
                            new MenuSeed("국", "미역국", 2500L, "소고기를 넣고 끓인 국"),
                            new MenuSeed("사이드", "계란말이", 3500L, "대파를 넣어 도톰하게 말아 낸 사이드"),
                    }),
    };

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
    private final DeliveryGroupRepository deliveryGroupRepository;
    private final DeliveryGroupItemRepository deliveryGroupItemRepository;

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

        List<Long> memberIds = seedMembers(now);
        seedAddresses(memberIds);
        Map<String, Long> ownerIds = seedOwners(now);
        List<Long> riderIds = seedRiders(now);

        List<SeededStore> openStores = seedStoresAndMenus(ownerIds, now);

        seedOrdersAndReviews(openStores, memberIds, now);
        applyReviewAggregates(openStores);

        seedBoards(memberIds, now);
        seedNotifications(memberIds, riderIds, openStores, ownerIds, now);

        log.info("데모 데이터 시딩 완료. 회원 {}, 사장님 {}, 가게 {}, 메뉴 {}, 라이더 {}, 주문 {}, 리뷰 {}, 배달그룹 {}",
                memberRepository.count(), preMemRepository.count(), storeRepository.count(),
                storeMenuRepository.count(), riderRepository.count(),
                orderRepository.count(), reviewRepository.count(), deliveryGroupRepository.count());
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

    /** adv_order 가 unique 라 1부터 5까지만 쓸 수 있다. */
    private void seedAdvertisements(LocalDateTime now) {
        String[][] banners = {
                {"도미노피자 신메뉴", "/admin/img/광고/광고_1번사진_도미노피자.jpg"},
                {"메가커피 여름 음료", "/admin/img/광고/광고_3번사진_메가커피.jpg"},
                {"롯데리아 세트 할인", "/admin/img/광고/광고_5번사진_롯데리아.jpg"},
                {"서브웨이 신규 매장", "/admin/img/광고/광고_4번사진_서브웨이.jpg"},
                {"코카콜라 여름 기획전", "/admin/img/광고/광고_2번_코카콜라.jpg"},
        };
        for (int i = 0; i < banners.length; i++) {
            AdvertisementEntity entity = new AdvertisementEntity();
            entity.setAdvOrder(i + 1);
            entity.setAdvTitle(banners[i][0]);
            entity.setAdvImageUrl(banners[i][1]);
            entity.setAdvCreatedAt(now.minusDays(10L + i));
            entity.setAdvUpdatedAt(now.minusDays(10L + i));
            advertisementRepository.save(entity);
        }
    }

    private void seedCoupons(LocalDateTime now) {
        Object[][] coupons = {
                {"WELCOME3000", "신규 가입 축하 쿠폰", "첫 주문에 쓸 수 있는 3천원 할인 쿠폰", 3000L, 12000L, 30, "배달", "Y"},
                {"RAINY2000", "우천 배달비 지원", "비 오는 날 배달비 2천원 지원", 2000L, 15000L, 14, "배달", "Y"},
                {"FIRSTORDER5000", "첫 주문 5천원 할인", "가입 후 7일 안에 쓰는 5천원 할인 쿠폰", 5000L, 20000L, 7, "배달", "Y"},
                {"LUNCH1500", "점심 시간 할인", "오전 11시부터 오후 2시까지 쓰는 1천5백원 할인", 1500L, 10000L, 21, "배달", "Y"},
                {"NIGHT2500", "심야 배달 할인", "밤 10시 이후 주문에 쓰는 2천5백원 할인", 2500L, 16000L, 45, "배달", "Y"},
                {"REVIEW1000", "리뷰 감사 쿠폰", "사진 리뷰를 남긴 회원에게 주는 1천원 할인", 1000L, 8000L, 60, "배달", "N"},
        };
        for (Object[] row : coupons) {
            CouponEntity entity = new CouponEntity();
            entity.setCode((String) row[0]);
            entity.setName((String) row[1]);
            entity.setContent((String) row[2]);
            entity.setDeductPrice((Long) row[3]);
            entity.setMinPrice((Long) row[4]);
            entity.setOrderType((String) row[6]);
            entity.setStatus((String) row[7]);
            entity.setExpiredDate(now.plusDays((Integer) row[5]));
            couponRepository.save(entity);
        }
    }

    private List<Long> seedMembers(LocalDateTime now) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < MEMBERS.length; i++) {
            MemberSeed seed = MEMBERS[i];
            MemberEntity entity = new MemberEntity();
            entity.setUserId(seed.userId());
            entity.setPassword(passwordEncoder.encode("user1234"));
            entity.setUsername(seed.username());
            entity.setNickname(seed.nickname());
            entity.setEmail(seed.userId() + "@example.com");
            entity.setPhone(seed.phone());
            entity.setBirthday(seed.birthday());
            entity.setGender(seed.gender());
            entity.setPoint(seed.point());
            entity.setGrade(seed.grade());
            entity.setAddress(seed.address());
            entity.setRegisterIp(DEMO_IP);
            entity.setLastLoginIp(DEMO_IP);
            entity.setRegisterDate(now.minusMonths(2L + i % 6).minusDays(i * 3L));
            entity.setLastLoginDate(now.minusHours(1L + i * 4L));
            entity.setStatus("활성");
            entity.setReceiveEmail(i % 4 == 3 ? "거절" : "승인");
            entity.setOpenProfile(i % 5 == 4 ? "거절" : "승인");
            entity.setReceiveNotify("승인");
            entity.setLoginType("LOCAL");
            ids.add(memberRepository.save(entity).getMId());
        }
        return ids;
    }

    private void seedAddresses(List<Long> memberIds) {
        for (AddressSeed seed : ADDRESSES) {
            DeliveryAddressEntity entity = new DeliveryAddressEntity();
            entity.setAddrMemberId(memberIds.get(seed.memberIndex()));
            entity.setAddrName(seed.name());
            entity.setAddrAddress(seed.address());
            entity.setAddrIsMain(seed.isMain());
            deliveryAddressRepository.save(entity);
        }
    }

    private Map<String, Long> seedOwners(LocalDateTime now) {
        Map<String, Long> ids = new LinkedHashMap<>();
        for (int i = 0; i < OWNERS.length; i++) {
            OwnerSeed seed = OWNERS[i];
            PreMemberEntity entity = new PreMemberEntity();
            entity.setPreMemUserId(seed.userId());
            entity.setPreMemPassword(passwordEncoder.encode("boss1234"));
            entity.setPreMemCeoName(seed.ceoName());
            entity.setPreMemBizRegNo(seed.bizRegNo());
            entity.setPreMemEmail(seed.userId() + "@example.com");
            entity.setPreMemPhone(seed.phone());
            entity.setPreMemCreatedAt(now.minusMonths(4L + i % 8));
            entity.setPreMemUpdatedAt(now.minusDays(3L + i));
            entity.setPreMemStatus("정상");
            entity.setPreMemApprovalStatus(seed.approvalStatus());
            ids.put(seed.userId(), preMemRepository.save(entity).getPreMemId());
        }
        return ids;
    }

    private List<Long> seedRiders(LocalDateTime now) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < RIDERS.length; i++) {
            RiderSeed seed = RIDERS[i];
            RiderEntity entity = new RiderEntity();
            entity.setRiderId(seed.riderId());
            entity.setRiderPw(passwordEncoder.encode("rider1234"));
            entity.setRiderName(seed.name());
            entity.setRiderPhone(seed.phone());
            entity.setVehicleType(seed.vehicleType());
            entity.setRiderGender(seed.gender());
            entity.setRiderBirth(seed.birth());
            entity.setTotalDeliveries(seed.totalDeliveries());
            entity.setIsAvailable(seed.isAvailable());
            entity.setRiderLon(seed.lon());
            entity.setRiderLat(seed.lat());
            entity.setRiderCreatedAt(now.minusMonths(1L + i % 9));
            ids.add(riderRepository.save(entity).getRiderNo());
        }
        return ids;
    }

    /**
     * 가게와 메뉴를 카테고리 단위로 만든다.
     * 별점과 리뷰 수는 0으로 두고, 리뷰를 넣은 뒤 집계로 덮는다.
     */
    private List<SeededStore> seedStoresAndMenus(Map<String, Long> ownerIds, LocalDateTime now) {
        List<SeededStore> openStores = new ArrayList<>();
        for (int c = 0; c < CATEGORIES.length; c++) {
            CategorySeed category = CATEGORIES[c];
            StoreSeed[] stores = category.stores();
            for (int s = 0; s < stores.length; s++) {
                StoreSeed store = stores[s];
                Long storeId = saveStore(store, category, ownerIds.get(store.ownerUserId()), now);
                SeededStore seeded = new SeededStore(storeId, store.name(), store.address(), store.deliveryFee());
                int menuCount = 5 + ((c + s) % 4);
                // 같은 사장님의 지점끼리 메뉴 구성과 가격을 조금씩 달리한다.
                int priceOffset = s * 500;
                for (int m = 0; m < menuCount; m++) {
                    MenuSeed menu = category.menus()[(m + s * 2) % category.menus().length];
                    long price = menu.price() + priceOffset;
                    boolean soldOut = m == menuCount - 1 && (c + s) % 3 == 0;
                    Long menuId = saveMenu(storeId, category.icon(), menu, price,
                            m < 2 ? 1L : 0L, soldOut ? "품절" : "판매중", now);
                    seeded.menuIds.add(menuId);
                    seeded.menuNames.add(menu.name());
                    seeded.menuPrices.add(price);
                }
                if ("승인".equals(store.status())) {
                    openStores.add(seeded);
                }
            }
        }
        return openStores;
    }

    private Long saveStore(StoreSeed seed, CategorySeed category, Long ownerId, LocalDateTime now) {
        PreStoreEntity entity = new PreStoreEntity();
        entity.setPreStoPreMemId(ownerId);
        entity.setPreStoName(seed.name());
        entity.setPreStoCategory(category.name());
        entity.setPreStoAddress(seed.address());
        entity.setPreStoPhoto(category.icon());
        entity.setPreStoPhone(seed.phone());
        entity.setPreStoIntro(seed.intro());
        entity.setPreStoMinOrderAmount(seed.minOrderAmount());
        entity.setPreStoDeliveryFee(seed.deliveryFee());
        entity.setPreStoDeliveryTimeMin(seed.deliveryTimeMin());
        entity.setPreStoDeliveryTimeMax(seed.deliveryTimeMax());
        entity.setPreStoRating(0.0f);
        entity.setPreStoReviewCount(0);
        entity.setPreStoCreatedAt(now.minusMonths(7));
        entity.setPreStoUpdatedAt(now.minusDays(2));
        entity.setPreStoStatus(seed.status());
        entity.setPreStoOperatingDays(OPEN_EVERY_DAY);
        entity.setPreStoOpeningHours(OPEN_ALL_DAY);
        entity.setPreStoDayOff("연중무휴");
        entity.setPreStoDeliveryArea(DELIVERY_AREA);
        return storeRepository.save(entity).getPreStoId();
    }

    private Long saveMenu(Long storeId, String icon, MenuSeed seed, long price,
                          long popularity, String status, LocalDateTime now) {
        PreStoreMenuEntity entity = new PreStoreMenuEntity();
        entity.setPreStoId(storeId);
        entity.setMenuCategory(seed.menuCategory());
        entity.setMenuName(seed.name());
        entity.setMenuPrice(price);
        entity.setMenuPictureUrl(icon);
        entity.setMenuPopularity(popularity);
        entity.setMenuStatus(status);
        entity.setMenuDescription(seed.description());
        entity.setMenuCreatedDate(now.minusMonths(7));
        entity.setMenuModifiedDate(now.minusDays(2));
        return storeMenuRepository.save(entity).getMenuId();
    }

    /**
     * 주문을 최근 2주에 흩뿌리고, 배달완료 건에서 리뷰를 만든다.
     * 별점은 만든 리뷰를 가게별로 모아 두었다가 집계에 쓴다.
     */
    private void seedOrdersAndReviews(List<SeededStore> openStores, List<Long> memberIds, LocalDateTime now) {
        List<DispatchOrder> dispatched = new ArrayList<>();
        for (int i = 0; i < ORDER_COUNT; i++) {
            boolean groupDemo = i >= DISPATCH_FROM && i < DISPATCH_FROM + GROUP_DEMO_SIZE;
            SeededStore store = openStores.get(groupDemo ? GROUP_DEMO_STORE_INDEX : i % openStores.size());
            int memberIndex = (i * 3 + 1) % memberIds.size();
            Long memberId = memberIds.get(memberIndex);

            int itemCount = 1 + (i % 2);
            List<Integer> pickedMenus = new ArrayList<>();
            List<Long> quantities = new ArrayList<>();
            long itemsSum = 0;
            for (int k = 0; k < itemCount; k++) {
                int index = (i + k * 2) % store.menuIds.size();
                long quantity = 1 + ((i + k) % 2);
                pickedMenus.add(index);
                quantities.add(quantity);
                itemsSum += store.menuPrices.get(index) * quantity;
            }

            String orderStatus = "주문접수";
            String deliveryStatus;
            LocalDateTime createdAt;
            if (i < 28) {
                deliveryStatus = "배달완료";
                createdAt = now.minusDays(1L + (i % 13)).minusHours(i % 10).minusMinutes((i * 7) % 60);
            } else if (i < 32) {
                // 가게가 거절한 주문에 배차중을 달면 라이더 배차 목록에 섞여 들어간다.
                // 실제로는 거절 시점에 배차가 끊기므로 배차취소로 둔다.
                orderStatus = "거절";
                deliveryStatus = "배차취소";
                createdAt = now.minusDays(2L + (i % 6)).minusHours(i % 5);
            } else if (i < 37) {
                deliveryStatus = "배달중";
                createdAt = now.minusMinutes(20L + (i * 6) % 90);
            } else if (i < DISPATCH_FROM) {
                deliveryStatus = "픽업중";
                createdAt = now.minusMinutes(12L + (i * 5) % 40);
            } else {
                deliveryStatus = "배차중";
                createdAt = now.minusMinutes(5L + (i * 3) % 25);
            }

            // 배차 대기 구간에서 묶음 데모 밖의 주문까지 묶음배달로 두면 목적지가 하나뿐인
            // 묶음 카드가 생겨 방문 순서 계산이 무의미해 보인다. 그 구간은 한집배달로 고정한다.
            String deliveryType = groupDemo ? "묶음배달"
                    : i >= DISPATCH_FROM ? "한집배달"
                    : DELIVERY_TYPES[i % DELIVERY_TYPES.length];
            int discount = i % 5 == 0 ? 2000 : 0;
            String customerMessage = CUSTOMER_MESSAGES[i % CUSTOMER_MESSAGES.length];
            Long orderId = saveOrder(store.id, memberId, store.menuIds.get(pickedMenus.get(0)),
                    orderStatus, deliveryStatus, deliveryType,
                    PAYMENT_METHODS[i % PAYMENT_METHODS.length],
                    itemsSum + store.deliveryFee - discount, store.deliveryFee, discount,
                    customerMessage,
                    DELIVERY_MESSAGES[i % DELIVERY_MESSAGES.length], createdAt);

            if ("배차중".equals(deliveryStatus)) {
                dispatched.add(new DispatchOrder(orderId, store.id, store.address,
                        MEMBERS[memberIndex].address(), deliveryType, store.deliveryFee, customerMessage));
            }

            for (int k = 0; k < itemCount; k++) {
                int index = pickedMenus.get(k);
                long quantity = quantities.get(k);
                saveOrderItem(orderId, store.menuIds.get(index), store.menuNames.get(index),
                        quantity, store.menuPrices.get(index),
                        store.menuPrices.get(index) * quantity, createdAt);
            }

            if (!"배달완료".equals(deliveryStatus)) {
                continue;
            }
            saveReview(orderId, store, memberId, pickedMenus.get(0), i, createdAt.plusHours(2));
            if (itemCount == 2 && i % 4 == 1) {
                saveReview(orderId, store, memberId, pickedMenus.get(1), i + 7, createdAt.plusHours(5));
            }
        }
        seedDeliveryGroups(dispatched, now);
    }

    /**
     * 배차 대기 주문에 배달 그룹을 붙인다.
     *
     * <p>라이더 화면은 그룹이 있어야 호출 시각과 배달 그룹 번호를 받는다. 그룹이 없으면
     * 카드에 수락할 대상이 없어 경로 계산까지 가지 못한다. 같은 가게의 묶음배달은 한 그룹으로,
     * 한집배달은 주문마다 그룹을 따로 만든다. 라이더가 아직 안 잡았으므로 rider_no 는 비운다.
     */
    private void seedDeliveryGroups(List<DispatchOrder> dispatched, LocalDateTime now) {
        Map<String, List<DispatchOrder>> grouped = new LinkedHashMap<>();
        for (DispatchOrder order : dispatched) {
            String key = "묶음배달".equals(order.deliveryType())
                    ? "묶음-" + order.storeId()
                    : "한집-" + order.orderId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(order);
        }

        int index = 0;
        for (List<DispatchOrder> members : grouped.values()) {
            DispatchOrder head = members.get(0);
            LocalDateTime calledAt = now.minusMinutes(6L + index * 3L);

            DeliveryGroupEntity group = new DeliveryGroupEntity();
            group.setStoreId(head.storeId());
            group.setDeliveryType(head.deliveryType());
            group.setDeliveryStatus("배차중");
            group.setCustomerRequest(head.customerRequest());
            group.setDeliveryFee(members.stream().mapToInt(DispatchOrder::deliveryFee).sum());
            group.setCreatedAt(calledAt);
            group.setCallTime(calledAt);
            Long deliveryId = deliveryGroupRepository.save(group).getDeliveryId();

            int sequence = 1;
            for (DispatchOrder member : members) {
                DeliveryGroupItemEntity item = new DeliveryGroupItemEntity();
                item.setDeliveryId(deliveryId);
                item.setOrderId(member.orderId());
                item.setOrderSequence(sequence++);
                item.setStoreAddress(member.storeAddress());
                item.setDestinationAddress(member.userAddress());
                deliveryGroupItemRepository.save(item);
            }
            index++;
        }
    }

    private void saveReview(Long orderId, SeededStore store, Long memberId, int menuIndex,
                            int cycleIndex, LocalDateTime createdAt) {
        int rating = RATING_CYCLE[cycleIndex % RATING_CYCLE.length];
        String[] texts = REVIEW_TEXTS[rating - 1];
        ReviewEntity entity = new ReviewEntity();
        entity.setOrderId(orderId);
        entity.setPreStoId(store.id);
        entity.setMemId(memberId);
        entity.setMenuId(store.menuIds.get(menuIndex));
        entity.setReviewRating(rating);
        entity.setReviewContent(texts[cycleIndex % texts.length]);
        entity.setReviewCreatedAt(createdAt);
        reviewRepository.save(entity);
        store.ratings.add(rating);
    }

    /** 목록 화면이 별점과 리뷰 수를 그대로 그리므로 실제 리뷰와 어긋나면 안 된다. */
    private void applyReviewAggregates(List<SeededStore> stores) {
        for (SeededStore store : stores) {
            if (store.ratings.isEmpty()) {
                continue;
            }
            double average = store.ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            PreStoreEntity entity = storeRepository.findById(store.id).orElseThrow();
            entity.setPreStoRating(Math.round(average * 10) / 10.0f);
            entity.setPreStoReviewCount(store.ratings.size());
            storeRepository.save(entity);
        }
    }

    private Long saveOrder(Long storeId, Long memberId, Long menuId, String orderStatus,
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

    private void saveOrderItem(Long orderId, Long menuId, String itemName, Long quantity,
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

    private void seedBoards(List<Long> memberIds, LocalDateTime now) {
        for (BoardSeed seed : BOARDS) {
            BoardEntity entity = new BoardEntity();
            entity.setMemId(memberIds.get(seed.memberIndex()));
            entity.setBoardTitle(seed.title());
            entity.setBoardContent(seed.content());
            entity.setBoardAnswerStatus(seed.answerStatus());
            entity.setBoardCreatedAt(now.minusDays(seed.daysAgo()));
            entity.setBoardUpdatedAt(now.minusDays(seed.daysAgo()));
            boardRepository.save(entity);
        }
    }

    private void seedNotifications(List<Long> memberIds, List<Long> riderIds, List<SeededStore> openStores,
                                   Map<String, Long> ownerIds, LocalDateTime now) {
        for (int i = 0; i < 8; i++) {
            SeededStore store = openStores.get(i % openStores.size());
            Long memberId = memberIds.get(i % memberIds.size());
            saveNotification("STORE", store.id, "MEMBER", memberId, "주문",
                    store.name + " 신규 주문이 접수됐습니다.", now.minusMinutes(9L + i * 7));
        }
        for (int i = 0; i < 5; i++) {
            saveNotification("MEMBER", memberIds.get((i * 2) % memberIds.size()),
                    "RIDER", riderIds.get(i % riderIds.size()), "배달",
                    "주문하신 음식이 배달을 시작했습니다.", now.minusMinutes(4L + i * 11));
        }
        saveNotification("ADMIN", 1L, "STORE", ownerIds.get("boss04"), "가입",
                "신규 사장님 가입 승인 요청이 있습니다.", now.minusHours(3));
        saveNotification("ADMIN", 1L, "STORE", ownerIds.get("boss01"), "입점",
                "지점 개설 승인 요청이 있습니다.", now.minusHours(6));
        saveNotification("ADMIN", 1L, "RIDER", riderIds.get(1), "가입",
                "신규 라이더 승인 요청이 있습니다.", now.minusHours(9));
        saveNotification("MEMBER", memberIds.get(3), "ADMIN", 1L, "쿠폰",
                "이번 달 등급 쿠폰이 발급됐습니다.", now.minusHours(20));
    }

    private void saveNotification(String recipientType, Long recipientId, String senderType,
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
