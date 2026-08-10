/* =========================================================
   ADMIN
========================================================= */

COMMENT ON TABLE admin IS '관리자';

COMMENT ON COLUMN admin.admin_id IS '관리자 PK';
COMMENT ON COLUMN admin.admin_username IS '관리자 아이디';
COMMENT ON COLUMN admin.admin_password IS '관리자 비밀번호';
COMMENT ON COLUMN admin.admin_email IS '관리자 이메일';
COMMENT ON COLUMN admin.admin_role IS '관리자 권한';
COMMENT ON COLUMN admin.admin_created_at IS '생성일';
COMMENT ON COLUMN admin.admin_last_login IS '마지막 로그인';
COMMENT ON COLUMN admin.admin_status IS '상태';


/* =========================================================
   ADVERTISEMENTS
========================================================= */

COMMENT ON TABLE advertisements IS '광고';

COMMENT ON COLUMN advertisements.adv_id IS '광고 PK';
COMMENT ON COLUMN advertisements.adv_order IS '광고 순서';
COMMENT ON COLUMN advertisements.adv_title IS '광고 제목';
COMMENT ON COLUMN advertisements.adv_image_url IS '광고 이미지';
COMMENT ON COLUMN advertisements.adv_created_at IS '생성일';
COMMENT ON COLUMN advertisements.adv_updated_at IS '수정일';


/* =========================================================
   MEMBER
========================================================= */

COMMENT ON TABLE member IS '회원';

COMMENT ON COLUMN member.mem_id IS '회원 PK';
COMMENT ON COLUMN member.mem_userid IS '회원 아이디';
COMMENT ON COLUMN member.mem_email IS '회원 이메일';
COMMENT ON COLUMN member.mem_password IS '회원 비밀번호';
COMMENT ON COLUMN member.mem_username IS '회원 이름';
COMMENT ON COLUMN member.mem_nickname IS '닉네임';
COMMENT ON COLUMN member.mem_phone IS '전화번호';
COMMENT ON COLUMN member.mem_birthday IS '생년월일';
COMMENT ON COLUMN member.mem_gender IS '성별';
COMMENT ON COLUMN member.mem_point IS '포인트';
COMMENT ON COLUMN member.mem_grade IS '회원 등급';
COMMENT ON COLUMN member.mem_address IS '주소';
COMMENT ON COLUMN member.mem_register_ip IS '가입 IP';
COMMENT ON COLUMN member.mem_lastlogin_ip IS '최근 로그인 IP';
COMMENT ON COLUMN member.mem_register_date IS '가입일';
COMMENT ON COLUMN member.mem_lastlogin_date IS '최근 로그인일';
COMMENT ON COLUMN member.mem_status IS '회원 상태';
COMMENT ON COLUMN member.mem_receive_email IS '이메일 수신 여부';
COMMENT ON COLUMN member.mem_open_profile IS '프로필 공개 여부';
COMMENT ON COLUMN member.mem_receive_notify IS '알림 수신 여부';
COMMENT ON COLUMN member.mem_login_type IS '로그인 타입';


/* =========================================================
   PRE_MEMBER
========================================================= */

COMMENT ON TABLE pre_member IS '사장 회원';

COMMENT ON COLUMN pre_member.pre_mem_id IS '사장 회원 PK';
COMMENT ON COLUMN pre_member.pre_mem_biz_reg_no IS '사업자 등록번호';
COMMENT ON COLUMN pre_member.pre_mem_ceo_name IS '대표자명';
COMMENT ON COLUMN pre_member.pre_mem_phone IS '전화번호';
COMMENT ON COLUMN pre_member.pre_mem_user_id IS '아이디';
COMMENT ON COLUMN pre_member.pre_mem_password IS '비밀번호';
COMMENT ON COLUMN pre_member.pre_mem_email IS '이메일';
COMMENT ON COLUMN pre_member.pre_mem_biz_license_photo IS '사업자 등록증';
COMMENT ON COLUMN pre_member.pre_mem_created_at IS '생성일';
COMMENT ON COLUMN pre_member.pre_mem_updated_at IS '수정일';
COMMENT ON COLUMN pre_member.pre_mem_status IS '상태';
COMMENT ON COLUMN pre_member.pre_mem_approval_status IS '승인 상태';


/* =========================================================
   RIDERS
========================================================= */

COMMENT ON TABLE riders IS '라이더';

COMMENT ON COLUMN riders.rider_No IS '라이더 PK';
COMMENT ON COLUMN riders.rider_id IS '라이더 아이디';
COMMENT ON COLUMN riders.rider_pw IS '비밀번호';
COMMENT ON COLUMN riders.rider_name IS '이름';
COMMENT ON COLUMN riders.rider_phone IS '전화번호';
COMMENT ON COLUMN riders.vehicle_type IS '차량 종류';
COMMENT ON COLUMN riders.rider_gender IS '성별';
COMMENT ON COLUMN riders.rider_birth IS '생년월일';
COMMENT ON COLUMN riders.rider_created_at IS '생성일';
COMMENT ON COLUMN riders.total_deliveries IS '총 배달 수';
COMMENT ON COLUMN riders.is_available IS '배달 가능 여부';
COMMENT ON COLUMN riders.rider_lon IS '마지막 보고 경도';
COMMENT ON COLUMN riders.rider_lat IS '마지막 보고 위도';


/* =========================================================
   RIDER_ACCOUNT
========================================================= */

COMMENT ON TABLE rider_account IS '라이더 계좌';

COMMENT ON COLUMN rider_account.rider_aid IS '계좌 PK';
COMMENT ON COLUMN rider_account.rider_No IS '라이더 FK';
COMMENT ON COLUMN rider_account.rider_bank_name IS '은행명';
COMMENT ON COLUMN rider_account.rider_account_number IS '계좌번호';
COMMENT ON COLUMN rider_account.rider_account_holder IS '예금주';


/* =========================================================
   PRE_STORE
========================================================= */

COMMENT ON TABLE pre_store IS '가게';

COMMENT ON COLUMN pre_store.pre_sto_id IS '가게 PK';
COMMENT ON COLUMN pre_store.pre_sto_pre_mem_id IS '사장 회원 FK';
COMMENT ON COLUMN pre_store.pre_sto_name IS '가게명';
COMMENT ON COLUMN pre_store.pre_sto_category IS '카테고리';
COMMENT ON COLUMN pre_store.pre_sto_address IS '주소';
COMMENT ON COLUMN pre_store.pre_sto_photo IS '대표 사진';
COMMENT ON COLUMN pre_store.pre_sto_phone IS '전화번호';
COMMENT ON COLUMN pre_store.pre_sto_intro IS '소개';
COMMENT ON COLUMN pre_store.pre_sto_min_order_amount IS '최소 주문 금액';
COMMENT ON COLUMN pre_store.pre_sto_delivery_fee IS '배달비';
COMMENT ON COLUMN pre_store.pre_sto_delivery_time_min IS '최소 배달 시간';
COMMENT ON COLUMN pre_store.pre_sto_delivery_time_max IS '최대 배달 시간';
COMMENT ON COLUMN pre_store.pre_sto_rating IS '평점';
COMMENT ON COLUMN pre_store.pre_sto_review_count IS '리뷰 수';
COMMENT ON COLUMN pre_store.pre_sto_created_at IS '생성일';
COMMENT ON COLUMN pre_store.pre_sto_updated_at IS '수정일';
COMMENT ON COLUMN pre_store.pre_sto_status IS '상태';


/* =========================================================
   PRE_STORE_PHOTO
========================================================= */

COMMENT ON TABLE pre_store_photo IS '가게 사진';

COMMENT ON COLUMN pre_store_photo.pre_sto_photo_id IS '사진 PK';
COMMENT ON COLUMN pre_store_photo.pre_sto_photo_store_id IS '가게 FK';
COMMENT ON COLUMN pre_store_photo.pre_sto_photo_url IS '사진 URL';
COMMENT ON COLUMN pre_store_photo.pre_sto_photo_created_at IS '생성일';
COMMENT ON COLUMN pre_store_photo.pre_sto_photo_updated_at IS '수정일';


/* =========================================================
   PRE_STORE_MENU
========================================================= */

COMMENT ON TABLE pre_store_menu IS '가게 메뉴';

COMMENT ON COLUMN pre_store_menu.menu_id IS '메뉴 PK';
COMMENT ON COLUMN pre_store_menu.pre_sto_id IS '가게 FK';
COMMENT ON COLUMN pre_store_menu.menu_category IS '메뉴 카테고리';
COMMENT ON COLUMN pre_store_menu.menu_name IS '메뉴명';
COMMENT ON COLUMN pre_store_menu.menu_price IS '가격';
COMMENT ON COLUMN pre_store_menu.menu_picture_url IS '메뉴 사진';
COMMENT ON COLUMN pre_store_menu.menu_popularity IS '인기 여부';
COMMENT ON COLUMN pre_store_menu.menu_created_date IS '생성일';
COMMENT ON COLUMN pre_store_menu.menu_modified_date IS '수정일';
COMMENT ON COLUMN pre_store_menu.menu_status IS '상태';
COMMENT ON COLUMN pre_store_menu.menu_description IS '메뉴 설명';


/* =========================================================
   CART
========================================================= */

COMMENT ON TABLE cart IS '장바구니';

COMMENT ON COLUMN cart.cart_id IS '장바구니 PK';
COMMENT ON COLUMN cart.mem_id IS '회원 FK';
COMMENT ON COLUMN cart.menu_id IS '메뉴 FK';
COMMENT ON COLUMN cart.cart_name IS '메뉴명';
COMMENT ON COLUMN cart.cart_img_url IS '메뉴 이미지';
COMMENT ON COLUMN cart.cart_item_price IS '가격';
COMMENT ON COLUMN cart.cart_quantity IS '수량';


/* =========================================================
   ORDERS
========================================================= */

COMMENT ON TABLE orders IS '주문';

COMMENT ON COLUMN orders.order_id IS '주문 PK';
COMMENT ON COLUMN orders.pre_sto_id IS '가게 FK';
COMMENT ON COLUMN orders.mem_id IS '회원 FK';
COMMENT ON COLUMN orders.menu_id IS '메뉴 FK';
COMMENT ON COLUMN orders.order_status IS '주문 상태';
COMMENT ON COLUMN orders.order_total_price IS '총 금액';
COMMENT ON COLUMN orders.customer_message IS '고객 요청사항';
COMMENT ON COLUMN orders.delivery_message IS '배달 요청사항';
COMMENT ON COLUMN orders.payment_method IS '결제 방식';
COMMENT ON COLUMN orders.delivery_type IS '배달 유형';
COMMENT ON COLUMN orders.delivery_status IS '배달 상태';
COMMENT ON COLUMN orders.delivery_fee IS '배달비';
COMMENT ON COLUMN orders.order_created_at IS '생성일';
COMMENT ON COLUMN orders.order_updated_at IS '수정일';
COMMENT ON COLUMN orders.discount_amount IS '할인 금액';


/* =========================================================
   ORDERITEM
========================================================= */

COMMENT ON TABLE orderitem IS '주문 상품';

COMMENT ON COLUMN orderitem.order_item_id IS '주문 상품 PK';
COMMENT ON COLUMN orderitem.order_id IS '주문 FK';
COMMENT ON COLUMN orderitem.menu_id IS '메뉴 FK';
COMMENT ON COLUMN orderitem.item_name IS '상품명';
COMMENT ON COLUMN orderitem.quantity IS '수량';
COMMENT ON COLUMN orderitem.item_price IS '상품 가격';
COMMENT ON COLUMN orderitem.total_price IS '총 가격';
COMMENT ON COLUMN orderitem.order_date IS '주문일';


/* =========================================================
   REVIEWS
========================================================= */

COMMENT ON TABLE reviews IS '리뷰';

COMMENT ON COLUMN reviews.review_id IS '리뷰 PK';
COMMENT ON COLUMN reviews.order_id IS '주문 FK';
COMMENT ON COLUMN reviews.pre_sto_id IS '가게 FK';
COMMENT ON COLUMN reviews.mem_id IS '회원 FK';
COMMENT ON COLUMN reviews.menu_id IS '메뉴 FK';
COMMENT ON COLUMN reviews.review_rating IS '평점';
COMMENT ON COLUMN reviews.review_content IS '리뷰 내용';
COMMENT ON COLUMN reviews.review_image IS '리뷰 이미지';
COMMENT ON COLUMN reviews.review_created_at IS '생성일';
COMMENT ON COLUMN reviews.review_update_at IS '수정일';


/* =========================================================
   COMMENTS
========================================================= */

COMMENT ON TABLE comments IS '댓글';

COMMENT ON COLUMN comments.comment_id IS '댓글 PK';
COMMENT ON COLUMN comments.pre_mem_id IS '사장 회원 FK';
COMMENT ON COLUMN comments.review_id IS '리뷰 FK';
COMMENT ON COLUMN comments.board_id IS '게시글 FK';
COMMENT ON COLUMN comments.admin_id IS '관리자 FK';
COMMENT ON COLUMN comments.comment_contents IS '댓글 내용';
COMMENT ON COLUMN comments.comment_date IS '작성일';


/* =========================================================
   COUPONS
========================================================= */

COMMENT ON TABLE coupons IS '쿠폰';

COMMENT ON COLUMN coupons.cpn_id IS '쿠폰 PK';
COMMENT ON COLUMN coupons.cpn_code IS '쿠폰 코드';
COMMENT ON COLUMN coupons.cpn_name IS '쿠폰명';
COMMENT ON COLUMN coupons.cpn_content IS '쿠폰 설명';
COMMENT ON COLUMN coupons.cpn_deduct_price IS '할인 금액';
COMMENT ON COLUMN coupons.cpn_min_price IS '최소 주문 금액';
COMMENT ON COLUMN coupons.cpn_order_type IS '주문 타입';
COMMENT ON COLUMN coupons.cpn_status IS '쿠폰 상태';
COMMENT ON COLUMN coupons.cpn_created IS '생성일';
COMMENT ON COLUMN coupons.cpn_modified IS '수정일';
COMMENT ON COLUMN coupons.cpn_expired IS '만료일';


/* =========================================================
   COUPON_USAGES
========================================================= */

COMMENT ON TABLE coupon_usages IS '쿠폰 사용 이력';

COMMENT ON COLUMN coupon_usages.usage_id IS '사용 이력 PK';
COMMENT ON COLUMN coupon_usages.cpn_id IS '쿠폰 FK';
COMMENT ON COLUMN coupon_usages.mem_id IS '회원 FK';
COMMENT ON COLUMN coupon_usages.used_at IS '사용일';


/* =========================================================
   LOGIN_HISTORY
========================================================= */

COMMENT ON TABLE login_history IS '로그인 기록';

COMMENT ON COLUMN login_history.his_login_id IS '로그인 기록 PK';
COMMENT ON COLUMN login_history.his_mid IS '회원 FK';
COMMENT ON COLUMN login_history.his_login_date IS '로그인 일시';
COMMENT ON COLUMN login_history.his_ip_address IS 'IP 주소';
COMMENT ON COLUMN login_history.his_device_os IS '운영체제';
COMMENT ON COLUMN login_history.his_browser IS '브라우저';


/* =========================================================
   DELIVERY_ADDRESS
========================================================= */

COMMENT ON TABLE delivery_address IS '배송지';

COMMENT ON COLUMN delivery_address.addr_id IS '배송지 PK';
COMMENT ON COLUMN delivery_address.addr_member_id IS '회원 FK';
COMMENT ON COLUMN delivery_address.addr_name IS '배송지명';
COMMENT ON COLUMN delivery_address.addr_address IS '주소';
COMMENT ON COLUMN delivery_address.addr_is_main IS '기본 배송지 여부';
COMMENT ON COLUMN delivery_address.addr_register_date IS '등록일';


/* =========================================================
   DELIVERY_GROUP
========================================================= */

COMMENT ON TABLE delivery_group IS '배달 그룹';

COMMENT ON COLUMN delivery_group.delivery_id IS '배달 그룹 PK';
COMMENT ON COLUMN delivery_group.rider_no IS '라이더 FK';
COMMENT ON COLUMN delivery_group.store_id IS '가게 FK';
COMMENT ON COLUMN delivery_group.delivery_type IS '배달 유형';
COMMENT ON COLUMN delivery_group.delivery_status IS '배달 상태';
COMMENT ON COLUMN delivery_group.customer_request IS '고객 요청사항';
COMMENT ON COLUMN delivery_group.delivery_fee IS '배달비';
COMMENT ON COLUMN delivery_group.created_at IS '생성일';
COMMENT ON COLUMN delivery_group.updated_at IS '수정일';
COMMENT ON COLUMN delivery_group.call_time IS '배차 시간';


/* =========================================================
   DELIVERY_GROUP_ITEM
========================================================= */

COMMENT ON TABLE delivery_group_item IS '배달 그룹 상세';

COMMENT ON COLUMN delivery_group_item.delivery_id IS '배달 그룹 FK';
COMMENT ON COLUMN delivery_group_item.order_id IS '주문 FK';
COMMENT ON COLUMN delivery_group_item.order_sequence IS '배달 순서';
COMMENT ON COLUMN delivery_group_item.store_address IS '가게 주소';
COMMENT ON COLUMN delivery_group_item.destination_address IS '목적지 주소';


/* =========================================================
   NOTIFICATIONS
========================================================= */

COMMENT ON TABLE notifications IS '알림';

COMMENT ON COLUMN notifications.id IS '알림 PK';
COMMENT ON COLUMN notifications.sender_type IS '발신자 타입';
COMMENT ON COLUMN notifications.sender_id IS '발신자 ID';
COMMENT ON COLUMN notifications.recipient_type IS '수신자 타입';
COMMENT ON COLUMN notifications.recipient_id IS '수신자 ID';
COMMENT ON COLUMN notifications.category IS '알림 카테고리';
COMMENT ON COLUMN notifications.message IS '알림 메시지';
COMMENT ON COLUMN notifications.created_at IS '생성일';
COMMENT ON COLUMN notifications.status IS '읽음 상태';


/* =========================================================
   REWARD
========================================================= */

COMMENT ON TABLE reward IS '리워드';

COMMENT ON COLUMN reward.reward_id IS '리워드 PK';
COMMENT ON COLUMN reward.mem_id IS '회원 FK';
COMMENT ON COLUMN reward.reward_amount IS '리워드 금액';


/* =========================================================
   BOARDS
========================================================= */

COMMENT ON TABLE boards IS '게시판';

COMMENT ON COLUMN boards.board_id IS '게시글 PK';
COMMENT ON COLUMN boards.mem_id IS '회원 FK';
COMMENT ON COLUMN boards.board_title IS '제목';
COMMENT ON COLUMN boards.board_content IS '내용';
COMMENT ON COLUMN boards.board_createdAt IS '생성일';
COMMENT ON COLUMN boards.board_updatedAt IS '수정일';
COMMENT ON COLUMN boards.board_answer_status IS '답변 상태';