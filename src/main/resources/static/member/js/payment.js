// ------------------------------
// 전역 변수
// ------------------------------
let productName = "";
let couponList = [];
let selectedCoupon = null;

// ------------------------------
// DOMContentLoaded
// ------------------------------
document.addEventListener("DOMContentLoaded", function () {
    // 1) 주문 데이터 불러오기
    const orderItems = JSON.parse(localStorage.getItem("orderItems"));
    let orderTotal = parseFloat(localStorage.getItem("totalAmount")) || 0;

    if (!orderItems) {
        alert("주문 데이터가 없습니다.");
        return;
    }

    // 주문내역 처리
    const orderSummary = document.querySelector(".order-details");
    const dynamicOrderItems = document.getElementById("dynamic-order-items");

    // 아임포트 결제 시 상품명(초과 길이 자르기)
    productName = orderItems.map(item => item.menuName).join(", ");
    if (productName.length > 100) {
        productName = productName.substring(0, 97) + '...';
    }

    // 주문 항목 추가 (hidden input & 화면 표시)
    orderItems.forEach((item, index) => {
        const orderDetail = document.createElement("p");
        orderDetail.innerHTML = ` 
            ${item.menuName}(${item.menuPrice}원)(${item.quantity}개)
            <span>${item.totalPrice.toLocaleString()}원</span>
        `;
        orderSummary.appendChild(orderDetail);

        dynamicOrderItems.innerHTML += `
            <input type="hidden" name="orderItems[${index}].itemName" value="${item.menuName}">
            <input type="hidden" name="orderItems[${index}].quantity" value="${item.quantity}">
            <input type="hidden" name="orderItems[${index}].itemPrice" value="${item.menuPrice}">
            <input type="hidden" name="orderItems[${index}].totalPrice" value="${item.totalPrice}">
        `;
    });

    // ------------------------------
    // 배달비 + 총 결제 금액
    // ------------------------------
    const totalAmountDisplay = document.getElementById("total_amount_display");
    const totalAmountInput = document.getElementById("total_amount");
    const deliveryTypeElement = document.getElementById("delivery_type");
    const deliveryFeeInput = document.getElementById("deliveryFee");

    // 배달비 초기값
    let deliveryFee = 0;
    if (deliveryTypeElement.value === "한집배달") {
        deliveryFee = 3000;
    } else if (deliveryTypeElement.value === "묶음배달") {
        deliveryFee = 2000;
    }

    // 기본 결제금액(쿠폰 전, 배달비 포함)
    let finalTotalAmount = orderTotal + deliveryFee;
    totalAmountDisplay.textContent = `${finalTotalAmount.toLocaleString()}원`;
    totalAmountInput.value = finalTotalAmount;
    deliveryFeeInput.value = deliveryFee;

    // 배달비 표시
    let deliveryFeeElement = document.createElement("p");
    deliveryFeeElement.id = "delivery_fee_display";
    deliveryFeeElement.innerHTML = `배달비: <span id="delivery_fee_value">${deliveryFee.toLocaleString()}원</span>`;
    orderSummary.appendChild(deliveryFeeElement);

    // 할인 표시 (처음에는 숨김)
    let discountElement = document.createElement("p");
    discountElement.id = "discount_display";
    discountElement.style.display = "none"; // 초기에는 숨김
    discountElement.innerHTML = `할인: <span id="discount_value">0원</span>`;
    orderSummary.appendChild(discountElement);

    // ------------------------------
    // 배달 타입 변경 시 → applyDiscount() 재호출
    // ------------------------------
    deliveryTypeElement.addEventListener("change", function() {
        if (deliveryTypeElement.value === "한집배달") {
            deliveryFee = 3000;
        } else if (deliveryTypeElement.value === "묶음배달")  {
            deliveryFee = 2000;
        }
        deliveryFeeElement.innerHTML = `배달비: <span id="delivery_fee_value">${deliveryFee.toLocaleString()}원</span>`;
        deliveryFeeInput.value = deliveryFee;
        applyDiscount();
    });

    // ------------------------------
    // 요청사항(글자수) 처리
    // ------------------------------
    const customerMessage = document.getElementById("customer_message");
    const charCountCustomer = document.querySelector(".char-count-customer");
    customerMessage.addEventListener("input", function () {
        charCountCustomer.textContent = `${customerMessage.value.length} / 100`;
    });

    const deliveryMessage = document.getElementById("delivery_message");
    const charCountDelivery = document.querySelector(".char-count-delivery");
    deliveryMessage.addEventListener("input", function () {
        charCountDelivery.textContent = `${deliveryMessage.value.length} / 100`;
    });

    // 2) 쿠폰 목록 불러오기 (AJAX)
    loadCoupons(orderTotal);
});

// ------------------------------
// 쿠폰 목록 불러오기
// ------------------------------
function loadCoupons(orderTotal) {
    $.ajax({
        url: '/api/coupons/getCoupons',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            couponList = data;
            const couponSelect = $('#couponSelect');
            $.each(data, function(index, coupon) {
                couponSelect.append(
                    $('<option>', {
                        value: coupon.id,
                        text: `${coupon.name} - ${coupon.deductPrice}원 할인 (최소 ${coupon.minPrice}원 주문)`
                    })
                );
            });
        },
        error: function(xhr, status, error) {
            console.error("쿠폰 데이터를 불러오는 중 오류:", error);
        }
    });

    // 쿠폰 선택 시 할인 재계산
    $('#couponSelect').on('change', function() {
        const selectedId = $(this).val();
        if (!selectedId) {
            selectedCoupon = null;
        } else {
            selectedCoupon = couponList.find(c => c.id == selectedId);
        }
        applyDiscount();
    });
}

// ------------------------------
// 할인 적용 함수(배달비 + 쿠폰)
// ------------------------------
function applyDiscount() {
    const orderTotal = parseFloat(localStorage.getItem("totalAmount")) || 0;
    let deliveryFee = parseFloat($('#deliveryFee').val()) || 0;

    let finalTotalAmount = orderTotal + deliveryFee;
    let discountApplied = 0; // 현재 할인액

    // 쿠폰 할인
    if (selectedCoupon) {
        if (orderTotal >= selectedCoupon.minPrice) {
            discountApplied = selectedCoupon.deductPrice;
            finalTotalAmount -= discountApplied;
        } else {
            alert(`해당 쿠폰은 최소 주문 금액 ${selectedCoupon.minPrice}원 이상이어야 적용됩니다.`);
            $('#couponSelect').val('');
            selectedCoupon = null;
        }
    }
    if (finalTotalAmount < 0) finalTotalAmount = 0;

    // 최종 결제 금액 표시
    $('#total_amount_display').text(`${finalTotalAmount.toLocaleString()}원`);
    $('#total_amount').val(finalTotalAmount);

    // ★ 할인 금액 표시
    if (discountApplied > 0) {
        $('#discount_value').text(discountApplied.toLocaleString() + "원");
        $('#discount_display').show();
    } else {
        $('#discount_value').text("0원");
        $('#discount_display').hide();
    }

    // ★ #discountPrice 값도 업데이트 (서버 전송용)
    // ※ id="discountPrice"가 HTML 상에 <input type="hidden" id="discountPrice" ...> 형태로 존재한다는 가정
    document.getElementById("discountPrice").value = discountApplied;
}

// ------------------------------
// 결제 함수
// ------------------------------
function pay() {
    const paymentForm = document.querySelector(".payment-form");
    const totalAmountInput = document.getElementById("total_amount");
    const finalTotalAmount = parseFloat(totalAmountInput.value);

    // 현재 discountPrice input에 들어있는 값(문자열)을 숫자로 변환
    const discountPriceInput = document.getElementById("discountPrice");
    const finalDiscountAmount = parseFloat(discountPriceInput.value) || 0;

    const paymentMethod = document.querySelector('select[name="paymentMethod"]').value;

    // (1) 총 결제 금액 hidden
    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "orderTotalPrice";
    hiddenInput.value = finalTotalAmount;
    paymentForm.appendChild(hiddenInput);

    // (2) 할인 금액 hidden
    const discountInput = document.createElement("input");
    discountInput.type = "hidden";
    discountInput.name = "discountAmount"; // 서버에서 받을 필드명
    discountInput.value = finalDiscountAmount;
    paymentForm.appendChild(discountInput);

    if (!finalTotalAmount || finalTotalAmount <= 0) {
        alert("총 결제 금액이 유효하지 않습니다.");
        return;
    }

    // 아임포트 결제
    let IMP = window.IMP;
    IMP.init("imp65742330");
    IMP.request_pay(
        {
            pg: 'kakaopay',
            pay_method: paymentMethod,
            merchant_uid: `merchant_${new Date().getTime()}`,
            name: productName,
            amount: finalTotalAmount,
            buyer_name: "홍길동",
            buyer_tel: "010-1234-5678",
            buyer_addr: "서울특별시 강남구",
        },
        function (rsp) {
            if (rsp.success) {
                // 쿠폰 상태 업데이트
                if (selectedCoupon) {
                    $.ajax({
                        url: '/api/coupons/useCoupon',
                        type: 'POST',
                        data: { couponId: selectedCoupon.id },
                        success: function(response) {
                            console.log("쿠폰 사용완료 업데이트");
                        },
                        error: function() {
                            console.error("쿠폰 상태 업데이트 중 오류");
                        }
                    });
                }
                alert("결제가 완료되었습니다!");
                paymentForm.submit();
            } else {
                alert("결제에 실패했습니다. 다시 시도해주세요.");
            }
        }
    );
}