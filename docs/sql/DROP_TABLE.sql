/* =========================
   DROP TRIGGER
========================= */

DROP TRIGGER trg_pre_store_update;
DROP TRIGGER trg_orders_update;
DROP TRIGGER trg_pre_store_menu_update;
DROP TRIGGER trg_reviews_update;
DROP TRIGGER trg_pre_member_update;
DROP TRIGGER trg_pre_store_photo_update;
DROP TRIGGER trg_delivery_group_update;
DROP TRIGGER trg_advertisements_update;
DROP TRIGGER trg_coupons_update;


/* =========================
   DROP INDEX
========================= */

DROP INDEX idx_orders_mem_id;
DROP INDEX idx_orders_pre_sto_id;
DROP INDEX idx_orders_menu_id;

DROP INDEX idx_cart_mem_id;
DROP INDEX idx_cart_menu_id;

DROP INDEX idx_reviews_mem_id;
DROP INDEX idx_reviews_pre_sto_id;
DROP INDEX idx_reviews_order_id;

DROP INDEX idx_pre_store_menu_store_id;

DROP INDEX idx_login_history_his_mid;

DROP INDEX idx_delivery_group_rider_no;
DROP INDEX idx_delivery_group_item_order_id;

DROP INDEX idx_coupon_usages_mem_id;

DROP INDEX idx_comments_board_id;
DROP INDEX idx_comments_review_id;


/* =========================
   DROP TABLE - 삭제 순서
   자식 테이블 먼저 삭제
========================= */

DROP TABLE delivery_group_item CASCADE CONSTRAINTS;
DROP TABLE rider_account CASCADE CONSTRAINTS;
DROP TABLE coupon_usages CASCADE CONSTRAINTS;
DROP TABLE comments CASCADE CONSTRAINTS;
DROP TABLE reviews CASCADE CONSTRAINTS;
DROP TABLE orderitem CASCADE CONSTRAINTS;
DROP TABLE orders CASCADE CONSTRAINTS;
DROP TABLE cart CASCADE CONSTRAINTS;
DROP TABLE delivery_group CASCADE CONSTRAINTS;
DROP TABLE delivery_address CASCADE CONSTRAINTS;
DROP TABLE login_history CASCADE CONSTRAINTS;
DROP TABLE reward CASCADE CONSTRAINTS;
DROP TABLE pre_store_menu CASCADE CONSTRAINTS;
DROP TABLE pre_store_photo CASCADE CONSTRAINTS;
DROP TABLE pre_store CASCADE CONSTRAINTS;
DROP TABLE coupons CASCADE CONSTRAINTS;
DROP TABLE boards CASCADE CONSTRAINTS;
DROP TABLE notifications CASCADE CONSTRAINTS;
DROP TABLE riders CASCADE CONSTRAINTS;
DROP TABLE pre_member CASCADE CONSTRAINTS;
DROP TABLE member CASCADE CONSTRAINTS;
DROP TABLE advertisements CASCADE CONSTRAINTS;
DROP TABLE admin CASCADE CONSTRAINTS;