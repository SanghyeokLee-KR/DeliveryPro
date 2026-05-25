/* =========================================================
   FK CONSTRAINT
========================================================= */

/* LOGIN_HISTORY */
ALTER TABLE login_history
    ADD CONSTRAINT fk_login_history_member
        FOREIGN KEY (his_mid)
            REFERENCES member(mem_id);

/* REWARD */
ALTER TABLE reward
    ADD CONSTRAINT fk_reward_member
        FOREIGN KEY (mem_id)
            REFERENCES member(mem_id);

/* DELIVERY_ADDRESS */
ALTER TABLE delivery_address
    ADD CONSTRAINT fk_delivery_address_member
        FOREIGN KEY (addr_member_id)
            REFERENCES member(mem_id);

/* PRE_STORE */
ALTER TABLE pre_store
    ADD CONSTRAINT fk_pre_store_pre_member
        FOREIGN KEY (pre_sto_pre_mem_id)
            REFERENCES pre_member(pre_mem_id);

/* PRE_STORE_PHOTO */
ALTER TABLE pre_store_photo
    ADD CONSTRAINT fk_pre_store_photo_store
        FOREIGN KEY (pre_sto_photo_store_id)
            REFERENCES pre_store(pre_sto_id);

/* PRE_STORE_MENU */
ALTER TABLE pre_store_menu
    ADD CONSTRAINT fk_pre_store_menu_store
        FOREIGN KEY (pre_sto_id)
            REFERENCES pre_store(pre_sto_id);

/* CART */
ALTER TABLE cart
    ADD CONSTRAINT fk_cart_member
        FOREIGN KEY (mem_id)
            REFERENCES member(mem_id);

ALTER TABLE cart
    ADD CONSTRAINT fk_cart_menu
        FOREIGN KEY (menu_id)
            REFERENCES pre_store_menu(menu_id);

/* ORDERS */
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_member
        FOREIGN KEY (mem_id)
            REFERENCES member(mem_id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_store
        FOREIGN KEY (pre_sto_id)
            REFERENCES pre_store(pre_sto_id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_menu
        FOREIGN KEY (menu_id)
            REFERENCES pre_store_menu(menu_id);

/* ORDERITEM */
ALTER TABLE orderitem
    ADD CONSTRAINT fk_orderitem_order
        FOREIGN KEY (order_id)
            REFERENCES orders(order_id);

ALTER TABLE orderitem
    ADD CONSTRAINT fk_orderitem_menu
        FOREIGN KEY (menu_id)
            REFERENCES pre_store_menu(menu_id);

/* REVIEWS */
ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_order
        FOREIGN KEY (order_id)
            REFERENCES orders(order_id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_member
        FOREIGN KEY (mem_id)
            REFERENCES member(mem_id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_store
        FOREIGN KEY (pre_sto_id)
            REFERENCES pre_store(pre_sto_id);

ALTER TABLE reviews
    ADD CONSTRAINT fk_reviews_menu
        FOREIGN KEY (menu_id)
            REFERENCES pre_store_menu(menu_id);

/* COMMENTS */
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_admin
        FOREIGN KEY (admin_id)
            REFERENCES admin(admin_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_board
        FOREIGN KEY (board_id)
            REFERENCES boards(board_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_review
        FOREIGN KEY (review_id)
            REFERENCES reviews(review_id);

ALTER TABLE comments
    ADD CONSTRAINT fk_comments_pre_member
        FOREIGN KEY (pre_mem_id)
            REFERENCES pre_member(pre_mem_id);

/* COUPON_USAGES */
ALTER TABLE coupon_usages
    ADD CONSTRAINT fk_coupon_usages_coupon
        FOREIGN KEY (cpn_id)
            REFERENCES coupons(cpn_id);

ALTER TABLE coupon_usages
    ADD CONSTRAINT fk_coupon_usages_member
        FOREIGN KEY (mem_id)
            REFERENCES member(mem_id);

/* DELIVERY_GROUP */
ALTER TABLE delivery_group
    ADD CONSTRAINT fk_delivery_group_rider
        FOREIGN KEY (rider_no)
            REFERENCES riders(rider_No);

ALTER TABLE delivery_group
    ADD CONSTRAINT fk_delivery_group_store
        FOREIGN KEY (store_id)
            REFERENCES pre_store(pre_sto_id);

/* DELIVERY_GROUP_ITEM */
ALTER TABLE delivery_group_item
    ADD CONSTRAINT fk_delivery_group_item_delivery
        FOREIGN KEY (delivery_id)
            REFERENCES delivery_group(delivery_id);

ALTER TABLE delivery_group_item
    ADD CONSTRAINT fk_delivery_group_item_order
        FOREIGN KEY (order_id)
            REFERENCES orders(order_id);

/* RIDER_ACCOUNT */
ALTER TABLE rider_account
    ADD CONSTRAINT fk_rider_account_rider
        FOREIGN KEY (rider_No)
            REFERENCES riders(rider_No);



/* =========================================================
   INDEX
========================================================= */

CREATE INDEX idx_orders_mem_id
    ON orders(mem_id);

CREATE INDEX idx_orders_pre_sto_id
    ON orders(pre_sto_id);

CREATE INDEX idx_orders_menu_id
    ON orders(menu_id);

CREATE INDEX idx_cart_mem_id
    ON cart(mem_id);

CREATE INDEX idx_cart_menu_id
    ON cart(menu_id);

CREATE INDEX idx_reviews_mem_id
    ON reviews(mem_id);

CREATE INDEX idx_reviews_pre_sto_id
    ON reviews(pre_sto_id);

CREATE INDEX idx_reviews_order_id
    ON reviews(order_id);

CREATE INDEX idx_pre_store_menu_store_id
    ON pre_store_menu(pre_sto_id);

CREATE INDEX idx_login_history_his_mid
    ON login_history(his_mid);

CREATE INDEX idx_delivery_group_rider_no
    ON delivery_group(rider_no);

CREATE INDEX idx_delivery_group_item_order_id
    ON delivery_group_item(order_id);

CREATE INDEX idx_coupon_usages_mem_id
    ON coupon_usages(mem_id);

CREATE INDEX idx_comments_board_id
    ON comments(board_id);

CREATE INDEX idx_comments_review_id
    ON comments(review_id);



/* =========================================================
   UPDATED_AT TRIGGER
========================================================= */

CREATE OR REPLACE TRIGGER trg_pre_store_update
BEFORE UPDATE ON pre_store
                  FOR EACH ROW
BEGIN
    :NEW.pre_sto_updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_orders_update
BEFORE UPDATE ON orders
                  FOR EACH ROW
BEGIN
    :NEW.order_updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_pre_store_menu_update
BEFORE UPDATE ON pre_store_menu
                  FOR EACH ROW
BEGIN
    :NEW.menu_modified_date := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_reviews_update
BEFORE UPDATE ON reviews
                  FOR EACH ROW
BEGIN
    :NEW.review_update_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_pre_member_update
BEFORE UPDATE ON pre_member
                  FOR EACH ROW
BEGIN
    :NEW.pre_mem_updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_pre_store_photo_update
BEFORE UPDATE ON pre_store_photo
                  FOR EACH ROW
BEGIN
    :NEW.pre_sto_photo_updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_delivery_group_update
BEFORE UPDATE ON delivery_group
                  FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_advertisements_update
BEFORE UPDATE ON advertisements
                  FOR EACH ROW
BEGIN
    :NEW.adv_updated_at := SYSDATE;
END;
/

CREATE OR REPLACE TRIGGER trg_coupons_update
BEFORE UPDATE ON coupons
                  FOR EACH ROW
BEGIN
    :NEW.cpn_modified := SYSDATE;
END;
/



/* =========================================================
   SAMPLE INSERT
========================================================= */

/* ADMIN */
INSERT INTO admin (
    admin_username,
    admin_password,
    admin_email
) VALUES (
    'admin',
    '1234',
    'admin@test.com'
);

/* MEMBER */
INSERT INTO member (
    mem_userid,
    mem_email,
    mem_password,
    mem_username,
    mem_register_ip,
    mem_grade,
    mem_status,
    mem_receive_email,
    mem_open_profile,
    mem_receive_notify
) VALUES (
             'user01',
             'user01@test.com',
             '1234',
             '홍길동',
             '127.0.0.1',
             '일반',
             '활성',
             'Y',
             'Y',
             'Y'
         );

/* PRE_MEMBER */
INSERT INTO pre_member (
    pre_mem_biz_reg_no,
    pre_mem_ceo_name,
    pre_mem_user_id,
    pre_mem_password,
    pre_mem_email
) VALUES (
             '123-45-67890',
             '김사장',
             'store01',
             '1234',
             'store01@test.com'
         );

/* RIDERS */
INSERT INTO riders (
    rider_id,
    rider_pw,
    rider_name,
    rider_phone,
    vehicle_type,
    is_available
) VALUES (
             'rider01',
             '1234',
             '박라이더',
             '010-1111-2222',
             '오토바이',
             'Y'
         );

/* PRE_STORE */
INSERT INTO pre_store (
    pre_sto_pre_mem_id,
    pre_sto_name,
    pre_sto_category,
    pre_sto_address,
    pre_sto_phone,
    pre_sto_min_order_amount,
    pre_sto_delivery_fee,
    pre_sto_rating,
    pre_sto_review_count,
    pre_sto_status
) VALUES (
             1,
             '교촌치킨',
             '치킨',
             '인천광역시',
             '032-111-2222',
             15000,
             3000,
             5.0,
             0,
             '운영중'
         );

/* MENU */
INSERT INTO pre_store_menu (
    pre_sto_id,
    menu_category,
    menu_name,
    menu_price,
    menu_picture_url,
    menu_popularity,
    menu_status
) VALUES (
             1,
             '메인메뉴',
             '허니콤보',
             23000,
             '/images/honeycombo.jpg',
             1,
             '판매중'
         );

/* ORDERS */
INSERT INTO orders (
    pre_sto_id,
    mem_id,
    menu_id,
    order_total_price,
    payment_method,
    delivery_fee
) VALUES (
             1,
             1,
             1,
             26000,
             'CARD',
             3000
         );

/* ORDERITEM */
INSERT INTO orderitem (
    order_id,
    menu_id,
    item_name,
    quantity,
    item_price,
    total_price
) VALUES (
             1,
             1,
             '허니콤보',
             1,
             23000,
             23000
         );

/* REVIEW */
INSERT INTO reviews (
    order_id,
    pre_sto_id,
    mem_id,
    menu_id,
    review_rating,
    review_content,
    review_created_at
) VALUES (
             1,
             1,
             1,
             1,
             5,
             '맛있어요',
             SYSDATE
         );

/* =========================================================
   BOARD SAMPLE
========================================================= */

INSERT INTO boards (
    mem_id,
    board_title,
    board_content,
    board_answer_status
) VALUES (
             1,
             '배달 문의',
             '배달이 아직 안왔어요.',
             '대기'
         );


/* =========================================================
   COMMENT SAMPLE
========================================================= */

INSERT INTO comments (
    pre_mem_id,
    review_id,
    board_id,
    admin_id,
    comment_contents
) VALUES (
             1,
             1,
             1,
             1,
             '확인 후 처리하겠습니다.'
         );


/* =========================================================
   COUPON SAMPLE
========================================================= */

INSERT INTO coupons (
    cpn_code,
    cpn_name,
    cpn_content,
    cpn_deduct_price,
    cpn_min_price,
    cpn_order_type,
    cpn_status,
    cpn_expired
) VALUES (
             'WELCOME1000',
             '신규 가입 쿠폰',
             '첫 주문 할인',
             1000,
             15000,
             '배달',
             '활성',
             SYSDATE + 30
         );


/* =========================================================
   COUPON_USAGE SAMPLE
========================================================= */

INSERT INTO coupon_usages (
    cpn_id,
    mem_id,
    used_at
) VALUES (
             1,
             1,
             SYSDATE
         );


/* =========================================================
   DELIVERY_ADDRESS SAMPLE
========================================================= */

INSERT INTO delivery_address (
    addr_member_id,
    addr_name,
    addr_address,
    addr_is_main
) VALUES (
             1,
             '집',
             '인천광역시 미추홀구',
             'Y'
         );


/* =========================================================
   DELIVERY_GROUP SAMPLE
========================================================= */

INSERT INTO delivery_group (
    rider_no,
    store_id,
    delivery_type,
    delivery_status,
    customer_request,
    delivery_fee,
    call_time
) VALUES (
             1,
             1,
             '배달',
             '배차완료',
             '문앞에 놔주세요',
             3000,
             SYSDATE
         );


/* =========================================================
   DELIVERY_GROUP_ITEM SAMPLE
========================================================= */

INSERT INTO delivery_group_item (
    delivery_id,
    order_id,
    order_sequence,
    store_address,
    destination_address
) VALUES (
             1,
             1,
             1,
             '인천광역시 치킨거리',
             '인천광역시 미추홀구'
         );


/* =========================================================
   RIDER_ACCOUNT SAMPLE
========================================================= */

INSERT INTO rider_account (
    rider_no,
    rider_bank_name,
    rider_account_number,
    rider_account_holder
) VALUES (
             1,
             '국민은행',
             '123456-78-999999',
             '박라이더'
         );


/* =========================================================
   ADVERTISEMENT SAMPLE
========================================================= */

INSERT INTO advertisements (
    adv_order,
    adv_title,
    adv_image_url
) VALUES (
             1,
             '치킨 할인 이벤트',
             '/images/event_banner.jpg'
         );


/* =========================================================
   NOTIFICATION SAMPLE
========================================================= */

INSERT INTO notifications (
    sender_type,
    sender_id,
    recipient_type,
    recipient_id,
    category,
    message
) VALUES (
             'SYSTEM',
             1,
             'MEMBER',
             1,
             '주문',
             '주문이 접수되었습니다.'
         );


/* =========================================================
   REWARD SAMPLE
========================================================= */

INSERT INTO reward (
    mem_id,
    reward_amount
) VALUES (
             1,
             3000
         );


/* =========================================================
   PRE_STORE_PHOTO SAMPLE
========================================================= */

INSERT INTO pre_store_photo (
    pre_sto_photo_store_id,
    pre_sto_photo_url
) VALUES (
             1,
             '/images/store_photo.jpg'
         );


/* =========================================================
   LOGIN_HISTORY SAMPLE
========================================================= */

INSERT INTO login_history (
    his_mid,
    his_ip_address,
    his_device_os,
    his_browser
) VALUES (
             1,
             '127.0.0.1',
             'Windows',
             'Chrome'
         );