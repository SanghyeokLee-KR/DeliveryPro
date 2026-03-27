$(document).ready(function () {
    // Event delegation for quantity increase
    $(document).on('click', '.increase', function () {
        const cartItem = $(this).closest('.cart-item');
        const quantityElement = cartItem.find('.quantity');
        const cartId = cartItem.data('id'); // data-id 속성에서 cartId 가져오기
        let quantity = parseInt(quantityElement.text());

        quantity += 1;

        // 서버로 수량 업데이트 요청
        $.ajax({
            url: '/updateQuantity', // 엔드포인트 URL
            type: 'POST',
            data: {
                cartId: cartId,
                cartQuantity: quantity
            }, // 데이터를 전달
            success: function () {
                // 성공 시 UI 업데이트
                quantityElement.text(quantity);
                updatePriceAndSummary(cartItem, quantity);
            },
            error: function () {
                alert('수량 업데이트 실패!');
            }
        });
    });

// 수량 감소
    $(document).on('click', '.decrease', function () {
        const cartItem = $(this).closest('.cart-item');
        const quantityElement = cartItem.find('.quantity');
        const cartId = cartItem.data('id'); // data-id 속성에서 cartId 가져오기
        let quantity = parseInt(quantityElement.text());

        if (quantity > 1) {
            quantity -= 1;

            // 서버로 수량 업데이트 요청
            $.ajax({
                url: '/updateQuantity', // 엔드포인트 URL
                type: 'POST',
                data: {
                    cartId: cartId,
                    cartQuantity: quantity
                }, // 데이터를 전달
                success: function () {
                    // 성공 시 UI 업데이트
                    quantityElement.text(quantity);
                    updatePriceAndSummary(cartItem, quantity);
                },
                error: function () {
                    alert('수량 업데이트 실패!');
                }
            });
        }
    });

    // Event delegation for remove button
    $(document).on('click', '.remove-btn', function () {
        const cartItem = $(this).closest('.cart-item');
        const cartId = cartItem.data("id"); // cart ID 가져오기

        // 서버에 DELETE 요청 보내기
        $.ajax({
            url: `/removeCart`,
            type: 'DELETE',
            data: {cartId: cartId},
            success: function () {
                // UI에서 항목 제거
                cartItem.remove();
                updateCartSummary();
            },
            error: function (xhr) {
                console.error(`Error: ${xhr.status} - ${xhr.statusText}`);
                console.error(`Response: ${xhr.responseText}`);
                alert(`항목 삭제 실패! 상태 코드: ${xhr.status}`);
            }
        });
    })

    // Update item price and summary
    function updatePriceAndSummary(cartItem, quantity) {
        const priceElement = cartItem.find('.price');
        const cartPrice = parseFloat(cartItem.data('price')); // 각 항목의 기본 가격을 data-price에서 가져오기

        if (isNaN(cartPrice)) {
            console.error(`Invalid price for cart item with ID ${cartItem.data('id')}`);
            return; // 가격이 유효하지 않으면 함수 종료
        }

        // 가격 계산
        const newPrice = cartPrice * quantity;

        // 가격 UI 업데이트
        priceElement.text(newPrice.toLocaleString() + ' 원');

        // 장바구니 총합 업데이트
        updateCartSummary();
    }


// 장바구니 총합 업데이트
    function updateCartSummary() {
        let total = 0;

        // 각 장바구니 항목의 금액 합산
        $('.cart-item').each(function () {
            const quantity = parseInt($(this).find('.quantity').text());
            const price = parseFloat($(this).data('price')); // 각 항목의 기본 가격
            total += price * quantity; // 항목별 금액 합산
        });

        // 총 결제 금액 UI 업데이트
        const totalPriceElement = $('.pay-all-price'); // 총 결제 금액 요소 선택
        totalPriceElement.text(total.toLocaleString() + ' 원'); // 총 금액 표시

        // 상품 금액 UI 업데이트
        const itemPriceElement = $('.pay-body-price'); // 상품 금액 요소 선택
        itemPriceElement.text(total.toLocaleString() + ' 원'); // 상품 금액 표시
    }


    function loadCartItems() {
        // 서버에서 장바구니 항목을 가져옵니다.
        $.ajax({
            url: '/cartList', // 서버의 장바구니 항목 조회 엔드포인트
            type: 'POST', // 요청 메서드
            success: function (data) {
                let cartHtml = '';

                if (data.length === 0) {
                    // 장바구니에 항목이 없을 때 표시할 HTML
                    cartHtml = `
                <div class="cart-icon">
                    <img alt="장바구니 아이콘" src="/member/img/join/header_cart.svg"/>
                </div>
                <p class="empty-p">장바구니에 담긴 상품이 없습니다.</p>`;
                    // 결제 정보 섹션 숨기기
                    $('.payment-container').hide();
                } else {
                    $('.payment-container').show(); // 결제 정보 섹션 표시
                    // 장바구니 항목이 있을 때
                    data.forEach(item => {
                        cartHtml += `
    <div class="cart-item" data-id="${item.cartId}" data-price="${item.cartPrice}">
        <div class="item-info">
            <img src="/store-img/store-menu-img/${item.cartImgUrl}" alt="${item.cartName}">
            <div class="item-details">
                <div class="cartName">${item.cartName}</div>
                <div class="quantity-control">
                    <button class="decrease">-</button>
                    <span class="quantity">${item.cartQuantity}</span>
                    <button class="increase">+</button>
                </div>
            </div>
        </div>
        <div class="price">${(item.cartPrice * item.cartQuantity).toLocaleString()} 원</div>
        <button class="remove-btn">×</button>
    </div>`;
                    });
                }

                // 생성된 HTML을 #cart 요소에 삽입합니다.
                $('#cart').html(cartHtml);

                // 장바구니 항목이 있는 경우 총합 계산 및 업데이트
                if (data.length > 0) {
                    updateCartSummary();
                }
            },
            error: function () {
                alert('장바구니 항목 로드 실패!');
            }
        });
    }

    loadCartItems()
})
function pay() {
    // 장바구니 데이터 가져오기
    const cartItems = [];
    $('.cart-item').each(function () {
        const cartId = $(this).data('id'); // 장바구니 ID
        const name = $(this).find('.cartName').text(); // 상품명
        const quantity = parseInt($(this).find('.quantity').text()); // 수량
        const price = parseFloat($(this).data('price')); // 단가

        cartItems.push({
            cartId: cartId,
            name: name,
            quantity: quantity,
            price: price,
        });
    });

    if (cartItems.length === 0) {
        alert("장바구니가 비어 있습니다. 상품을 추가해주세요.");
        return;
    }

    // 총 결제 금액
    const totalAmount = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

    // 결제할 상품 이름 (첫 번째 상품명 + "외 X건" 형식)
    const productName =
        cartItems.length === 1
            ? cartItems[0].name
            : `${cartItems[0].name} 외 ${cartItems.length - 1}건`;

    // 아임포트 초기화
    let IMP = window.IMP;
    IMP.init("imp65742330");

    // 결제 요청
    IMP.request_pay(
        {
            pg: 'kakaopay',
            pay_method: 'card', // 또는 'trans' 등
            merchant_uid: `merchant_${new Date().getTime()}`,
            name: productName,
            amount: totalAmount,
            buyer_name: "홍길동", // 실제 사용자의 이름으로 변경 필요
            buyer_tel: "010-1234-5678", // 실제 사용자의 전화번호로 변경 필요
            buyer_addr: "서울특별시 강남구", // 실제 사용자의 주소로 변경 필요
        },
        function (rsp) {
            if (rsp.success) {
                // 결제 성공 시 장바구니 초기화 요청
                $.ajax({
                    url: '/deleteCart', // 서버의 전체 삭제 엔드포인트
                    type: 'POST',
                    success: function () {
                        // UI 업데이트: 전체 장바구니 초기화
                        $('.cart-item').remove(); // 모든 장바구니 항목 제거

                        $('.payment-container').hide(); // 결제 섹션 숨기기
                        alert("결제가 완료되었습니다!");
                        window.location.href = "/customer"; // 결제 완료 후 이동할 페이지
                    },
                    error: function () {
                        alert("결제는 완료되었지만 장바구니 초기화에 실패했습니다.");
                    },
                });
            } else {
                alert("결제에 실패했습니다. 다시 시도해주세요.");
            }
        })
}



