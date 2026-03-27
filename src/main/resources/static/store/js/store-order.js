$(document).ready(function () {
    const preStoId = document.getElementById('preSto-id').value;
    let groupOrderIds = [];

    /***************************************
     * 1. 현재 시각(또는 전달받은 날짜 문자열)을 "YYYY-MM-DD HH:mm:ss" 형식으로 변환하는 함수
     ***************************************/
    function formatDateToSecond(dateInput) {
        // dateInput이 Date 객체가 아니라면 Date 객체로 변환
        const date = dateInput instanceof Date ? dateInput : new Date(dateInput);
        const year = date.getFullYear();
        const month = ("0" + (date.getMonth() + 1)).slice(-2);
        const day = ("0" + date.getDate()).slice(-2);
        const hours = ("0" + date.getHours()).slice(-2);
        const minutes = ("0" + date.getMinutes()).slice(-2);
        const seconds = ("0" + date.getSeconds()).slice(-2);
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }

    /***************************************
     * [1] 가게용 주문 목록 렌더링 (배달관리 화면)
     * - 한집배달 주문은 기존대로 렌더링
     * - 묶음배달 주문은 화면에는 기존대로 표시(주문 생성 시각에 관계없이 모두 표시)
     ***************************************/
    function renderStoreOrderList(orders) {
        let singleDeliveryOutput = "";
        let groupDeliveryOutput = "";
        groupOrderIds = [];
        //  "픽업중"인 주문만 필터링
        const filteredOrders = orders.filter(order =>
            order.deliveryStatus === '픽업중'
        );
        filteredOrders.forEach(order => {
            const orderHTML = `
                <div class="status-content-all-order">
                    <div class="status-content-order">
                        <div class="status-content-info-order" data-order-id="${order.orderId}">
                            <p>주문번호: ${order.orderId}</p>
                            <div class="badge">${order.deliveryType}</div>
                        </div>
                        ${order.deliveryType.trim() === "한집배달"
                ? `<a href="#" class="rider-call" data-order-id="${order.orderId}">라이더 호출</a>`
                : ""}
                    </div>
                    <div class="status-content-address-order">
                    <img src="/store/img/icons/green.svg" alt="greenpoint" style="width: 20px;">
                    <span class="order-address" data-order-id="${order.orderId}"></span>
                    </div>
                </div>
                `;
            if (order.deliveryType.trim() === "한집배달") {
                console.log(`주문 ID ${order.orderId}: 한집 배달로 singleDeliveryBox에 추가`);
                singleDeliveryOutput += orderHTML;
            } else if (order.deliveryType.trim() === "묶음배달") {
                // 단순히 화면에 묶음 주문들을 나열(원래 디자인대로)
                console.log(`주문 ID ${order.orderId}: 묶음 배달로 groupDeliveryBox에 추가`);
                groupDeliveryOutput += orderHTML;
                groupOrderIds.push(order.orderId);
            } else {
                console.error(`주문 ID ${order.orderId}: 알 수 없는 배달 타입 (${order.deliveryType})`);
            }
        });
        $("#singleDeliveryBox").html(singleDeliveryOutput);
        if (groupDeliveryOutput.trim() !== "") {
            // 묶음 주문 영역 상단에 하나의 "묶음 라이더 호출" 버튼을 표시함
            const groupCallButton = `
                <div class="group-call-container" style="margin-bottom: 10px;">
                    <button id="groupRiderCallBtn" class="group-rider-button">묶음 라이더 호출</button>
                </div>
            `;
            $("#groupDeliveryBox").html(groupCallButton + groupDeliveryOutput);
        } else {
            $("#groupDeliveryBox").html(groupDeliveryOutput);
        }
    }

    /***************************************
     * [3] 주문 상세정보 렌더링
     ***************************************/

    function renderOrderDetails(orderData, menuItems) {
        // "배달전" 상태의 주문 필터링
        const filteredOrderData = orderData.filter(order =>
            order.deliveryStatus && order.deliveryStatus.trim() === "배달전"
        );

        const orderDetailsHTML = filteredOrderData.map(order => {
            let deliveryFee = 0;
            if (order.deliveryType.trim() === "한집배달") {
                deliveryFee = 3000;
            } else if (order.deliveryType.trim() === "묶음배달") {
                deliveryFee = 2000;
            }


            const discountPrice = order.discountAmount || 0; // 주문 객체에 할인 금액이 있다고 가정
            const finalTotalPrice = order.orderTotalPrice || 0;


            return `
        <div class="order-details">
            <h3>
                <span class="badge">${order.deliveryType}</span> 
                주문번호: ${order.orderId}
            </h3>
            <p>총 금액: ${finalTotalPrice.toLocaleString()}원 (결제완료)</p>
            <hr>
            <h4>요청사항</h4>
            <p><strong>가게:</strong> ${order.customerMessage || "없음"}</p>
            <p><strong>배달:</strong> ${order.deliveryMessage || "없음"}</p>
            <hr>
            <h4>메뉴정보</h4>
            <table class="menu-info">
                <thead>
                    <tr>
                        <th>메뉴명</th>
                        <th>수량</th>
                        <th>금액</th>
                    </tr>
                </thead>
                <tbody>
                    ${menuItems.map(item => `
                        <tr>
                            <td>${item.itemName}</td>
                            <td>${item.quantity}</td>
                            <td>${item.totalPrice.toLocaleString()}원</td>
                        </tr>
                    `).join('')}
                    
                    <tr>
                        <td colspan="2"><strong>배달비</strong></td>
                        <td>${deliveryFee.toLocaleString()}원</td>
                    </tr>
                        <td colspan="2"><strong>할인</strong></td>
                        <td>-${discountPrice.toLocaleString()}원</td>
                    <tr>
                        <td colspan="2"><strong>합계</strong></td>
                        <td>${finalTotalPrice.toLocaleString()}원</td>
                    </tr>
                </tbody>
            </table>
        </div>
        `;
        }).join('');

        $("#details").html(orderDetailsHTML).show();
        renderDeliveryInfo(filteredOrderData);
    }

    /***************************************
     * [2] 고객용 주문서 렌더링 (주문 상세보기 전용)
     ***************************************/
    function renderOrderSheet(orders) {
        let orderListHTML = '';

        // "배달전" 상태의 주문만 필터링
        const filteredOrders = orders.filter(order =>
            order.deliveryStatus && order.deliveryStatus.trim() === "배달전"
        );

        filteredOrders.forEach(order => {
            // data-delivery-status 속성을 추가하여 이벤트 핸들러에서도 활용 가능하게 함
            orderListHTML += `
            <div class="status-content" data-delivery-status="${order.deliveryStatus}">
                <p><strong>신규 1건</strong></p>
                <p data-order-id="${order.orderId}">
                    주문번호: <strong>${order.orderId}</strong>
                    <span class="badge">${order.deliveryType}</span>
                </p>
                <hr>
            </div>
        `;
        });
        $("#orderStatus").html(orderListHTML);
    }


    /***************************************
     * [4] 주문 상세 하단의 배달정보 렌더링
     ***************************************/
    function renderDeliveryInfo(orderData) {
        // "배달전" 상태의 주문만 필터링
        const filteredOrderData = orderData.filter(order =>
            order.deliveryStatus && order.deliveryStatus.trim() === "배달전"
        );

        const deliveryHTML = filteredOrderData.map(order => {
            return `
            <div class="delivery-info-header" id="deliveryHeader">
                <p class="delivery-first-text">주소</p>
            </div>
            <div class="delivery-info-content">
                <div class="delivery-info-left-section">
                    <img src="/store/img/icons/위치.svg" alt="지도 아이콘" id="deliveryIcon">
                    <span id="mem-address"></span>
                </div>
                <div id="order-reciept">
                    <div class="delivery-info-right-section">
                        <div class="delivery-info-right-top-section">
                            <div class="delivery-actions">
                                <div class="time-adjust">
                                    <button>-</button>
                                    <span>30분 전</span>
                                    <button>+</button>
                                </div>
                                <button class="accept" value="${order.orderId}">접수</button>
                                <button class="reject" value="${order.orderId}">거부</button>
                            </div>
                        </div>
                        <div class="delivery-info-right-bottom-section">
                            <p>고객에게 표시되는 배달시간: <strong>38분</strong></p>
                        </div>
                    </div>
                </div>
            </div>
        `;
        }).join('');

        $("#deliveryInfo").html(deliveryHTML);
    }

    /***************************************
     * [5] 이벤트 핸들러
     ***************************************/
    $(document).on("click", ".status-content", function () {
        // data-order-id와 함께 data-delivery-status를 읽어옴
        const orderId = $(this).find("p[data-order-id]").data("order-id");
        const deliveryStatus = $(this).data("delivery-status");

        // 유효성 및 "배달전" 상태인지 확인
        if (!orderId || isNaN(orderId)) {
            alert("🚨 주문 번호를 가져오는 데 실패했습니다.");
            return;
        }
        if (deliveryStatus !== "배달전") {
            console.warn(`주문 ID ${orderId}는 배달전 상태가 아닙니다.`);
            return;
        }

        console.log("🔍 클릭된 주문 ID:", orderId);

        $.when(
            $.ajax({url: `/orders/detail/${orderId}`, method: "POST"}),
            $.ajax({url: `/orderItem/${orderId}`, method: "POST"}),
            $.ajax({url: `/api/member/address/${orderId}`, method: "POST", dataType: 'json'})
        ).done(function (orderData, menuResponse, addressResponse) {
            renderOrderDetails(orderData[0], menuResponse[0]);
            if (addressResponse[0].status === 'success') {
                $("#mem-address").text(addressResponse[0].address);
            } else {
                console.warn("🚨 주소 응답에 문제가 있습니다:", addressResponse[0]);
                $("#mem-address").text("주소 정보를 가져올 수 없습니다.");
            }
        }).fail(function () {
            console.error("🚨 주문 상세 정보 또는 메뉴 정보를 가져오는 데 실패했습니다.");
            alert("🚨 주문 상세 정보를 가져오는 데 실패했습니다.");
        });
    });


    // 개별 주문의 라이더 호출 (한집배달)
    $(document).on('click', '.rider-call', function (event) {
        event.preventDefault();
        const orderId = $(this).data('order-id');
        $.ajax({
            url: '/orders/riderCall',
            type: 'POST',
            data: {orderId: orderId},
            dataType: "text",
            success: function (response) {
                console.log('배달원 ' + response + " 중!");
                alert('배달원 ' + response + " 중!");
                // 처리 완료 내역 테이블에 추가
                const now = new Date().toLocaleString();
                const row = `
                <tr>
                    <td>${orderId}</td>
                    <td>${now}</td>
                    <td>호출 완료</td>
                </tr>
            `;
                $("#processedSingleOrders tbody").append(row);
                // 화면에서 해당 주문 항목 제거
                $(`.status-content-all-order [data-order-id="${orderId}"]`)
                    .closest('.status-content-all-order')
                    .remove();
            },
            error: function (xhr, status, error) {
                console.error(`오류 발생: 상태 코드 = ${xhr.status}, 주문 ID = ${orderId}`);
            }
        });
    });

    // 묶음 배달 라이더 호출 (그룹 호출)
    $(document).on('click', '#groupRiderCallBtn', function (event) {
        event.preventDefault();
        if (groupOrderIds.length === 0) {
            alert("묶음 배달 주문이 없습니다.");
            return;
        }
        const callTime = formatDateToSecond(new Date());  // 버튼 클릭 시의 현재 시각
        $.ajax({
            url: '/orders/groupRiderCall',
            type: 'POST',
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify({orderIds: groupOrderIds, callTime: callTime}),
            dataType: "text",
            success: function (response) {
                console.log('묶음 배달 라이더 호출 성공: ' + response);
                alert('묶음 배달 라이더 호출 성공: ' + response);
                // 호출된 주문들을 처리 완료 테이블에 추가
                const now = new Date().toLocaleString();
                groupOrderIds.forEach(function (orderId) {
                    const row = `
                    <tr>
                        <td>${orderId}</td>
                        <td>${now}</td>
                        <td>호출 완료</td>
                    </tr>
                `;
                    $("#processedGroupOrders tbody").append(row);
                });
                // 그룹 영역에서 처리한 주문들을 제거
                $("#groupDeliveryBox").empty();
                // groupOrderIds 초기화
                groupOrderIds = [];
            },
            error: function (xhr, status, error) {
                console.error('묶음 배달 라이더 호출 오류:', error);
                alert('묶음 배달 라이더 호출 오류: ' + xhr.responseText);
            }
        });
    });

    // 접수 / 거부 버튼 클릭 이벤트
    $(document).on('click', '.accept, .reject', function () {
        const orderId = $(this).val();
        const action = $(this).hasClass('accept') ? 'accept' : 'reject';
        if (!orderId) {
            console.error("유효하지 않은 주문 ID입니다.");
            return;
        }
        $.ajax({
            url: `/orders/${orderId}/${action}`,
            type: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log("주문 처리 성공");
                alert('주문을 ' + (action === 'accept' ? '수락' : '거부') + '하였습니다.');
                // 필요 시 추가 UI 업데이트 로직 추가
            },
            error: function (xhr, status, error) {
                console.error(`오류 발생: 상태 코드 = ${xhr.status}, 주문 ID = ${orderId}`);
            }
        });
    });

    /***************************************
     * [6] 초기 AJAX 호출
     ***************************************/
    // 가게 주문 목록 (배달관리 화면)
    $.ajax({
        url: '/orders/storeOrderList',
        type: 'POST',
        dataType: 'json',
        success: function (result) {
            console.log("storeOrderList 결과:", result);
            renderStoreOrderList(result);
            loadStoreOrderAddresses();
        },
        error: function () {
            console.error("오류 발생: 상태 코드 = z, 주문 ID = x");
        }
    });

    // 고객용 주문서 렌더링 (필요 시)
    $.ajax({
        url: `/orders/orderList/${preStoId}`,
        method: 'POST',
        dataType: 'json',
        success: function (response) {
            // 데이터가 없거나 빈 객체인 경우
            if (!response || Object.keys(response).length === 0) {
                $('#details').html('<p class="no-data">주문서를 선택해주세요.</p>');
                $('#orderStatus').html('<p class="no-data">처리중인 주문이 없습니다.</p>');
            } else {
                // 주문 상태 업데이트
                if (response.orderStatus && response.orderStatus.trim() !== "") {
                    $('#orderStatus').html('<p>' + response.orderStatus + '</p>');
                } else {
                    $('#orderStatus').html('<p class="no-data">처리중인 주문이 없습니다.</p>');
                }

                // 주문 상세 정보 업데이트
                if (response.orderDetails && response.orderDetails.trim() !== "") {
                    $('#details').html('<p>' + response.orderDetails + '</p>');
                } else {
                    $('#details').html('<p class="no-data">주문서를 선택해주세요.</p>');
                }
                renderOrderSheet(response);
            }
        },

        error: function (error) {
            console.error("Error retrieving order sheet:", error);
            console.log('주문 정보를 불러오는 데 실패했습니다.');
        }
    });


    function loadStoreOrderAddresses() {
        $(".order-address").each(function(){
            const $addressSpan = $(this);
            const orderId = $addressSpan.data("order-id");

            $.ajax({
                url: `/api/member/address/${orderId}`,
                type: 'POST',
                dataType: 'json',
                success: function(response) {
                    if(response.status === 'success') {
                        $addressSpan.text(response.address);
                    } else {
                        $addressSpan.text("주소 정보를 가져올 수 없습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error(`오류 발생: 주문 ${orderId} 주소 요청 실패`, error);
                    $addressSpan.text("주소 요청 실패");
                }
            });
        });
    }
});


