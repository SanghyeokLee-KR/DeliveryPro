document.addEventListener("DOMContentLoaded", function () {
    // URL에서 쿼리 매개변수 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const selectedCategory = urlParams.get("category");

    if (selectedCategory) {
        activateTabAndLoadData(selectedCategory);
    } else {
        console.error("카테고리가 선택되지 않았습니다.");
    }

    // 카테고리 탭 클릭 이벤트 추가
    document.querySelectorAll('.storelist-control-bar li').forEach(tab => {
        tab.addEventListener('click', function () {
            const category = this.getAttribute('data-category');
            if (category) {
                activateTabAndLoadData(category);
                window.location.href = `/storeList?category=${category}`;
            } else {
                console.error("카테고리가 설정되지 않았습니다.");
            }
        });
    });

    // 가게 클릭 이벤트 위임
    document.querySelector("#storelist").addEventListener("click", function (e) {
        const item = e.target.closest(".item");
        if (item) {
            const category = item.getAttribute("data-category");
            const storeId = item.getAttribute("data-id");
            if (category && storeId) {
                window.location.href = `/storeView?category=${encodeURIComponent(category)}&storeId=${encodeURIComponent(storeId)}`;
            } else {
                console.error("카테고리 또는 Store ID가 누락되었습니다.");
            }
        }
    });

    // 탭 활성화 및 데이터 로드
    function activateTabAndLoadData(category) {
        document.querySelectorAll('.storelist-control-bar li').forEach(li => li.classList.remove('active'));
        const activeTab = document.querySelector(`.storelist-control-bar li[data-category="${category}"]`);
        if (activeTab) activeTab.classList.add('active');
        loadCategoryData(category);
    }

    // AJAX 데이터 로드 함수
    function loadCategoryData(category) {
        $.ajax({
            url: '/api/stores/category',
            method: 'POST',
            data: { category: category },
            dataType: 'json',
            success: function (data) {
                document.querySelector("#storelist").innerHTML = data.map((restaurant) => `
                    <div class="item clearfix" style="cursor: pointer;" 
                         data-category="${restaurant.preStoCategory}" 
                         data-id="${restaurant.preStoId}">
                        <table>
                            <tbody>
                                <tr>
                                    <td>
                                        <div class="rlogo" 
                                             style="background-image: url('/store-img/store-main-img/${restaurant.preStoPhoto || '/img/store.png'}');">
                                             <div class="storeOverlay" style="display: none;">영업이 아닙니다</div>
                                        </div>     
                                    </td>
                                    <td>
                                        <div class="restaurants-info">
                                            <div class="restaurant-name" title="${restaurant.preStoName}">
                                                ${restaurant.preStoName}
                                            </div>
                                            <div class="stars">
                                                <span>
                                                    <span class="ico-star1">
                                                        ★ ${restaurant.preStoRating.toFixed(1)}
                                                    </span>
                                                </span>
                                                <span class="review_num">
                                                    리뷰 ${restaurant.preStoReviewCount}
                                                </span>
                                            </div>
                                            <li class="payment">결제</li>
                                            <li class="min-price">${restaurant.preStoMinOrderAmount.toLocaleString()}원 이상 배달</li>
                                            <li class="delivery-time">${restaurant.preStoDeliveryTimeMin}~${restaurant.preStoDeliveryTimeMax}분</li>             
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                `).join("");

                // 각 스토어 항목마다 개별적으로 상태 확인 AJAX 호출 실행
                document.querySelectorAll("#storelist .item").forEach(item => {
                    const storeId = item.getAttribute("data-id");
                    check(storeId, item);
                });
            },
            error: function () {
                document.querySelector("#storelist").innerHTML = "<p>데이터를 가져오는 중 오류가 발생했습니다.</p>";
            }
        });
    }

    // 각 스토어 항목에 대해 상태를 확인하는 함수 (개선됨)
    function check(preStoId, storeItem) {
        $.ajax({
            url: `/api/stores/check/${preStoId}`,
            type: "POST",
            success: function (status) {
                // 해당 스토어 항목 내부의 버튼과 오버레이 선택
                const $storeOverlay = $(storeItem).find(".storeOverlay");

                if (status === "중지") {
                    $storeOverlay.show();
                } else if (status === "승인") {
                    $storeOverlay.hide();
                } else {
                    console.warn("알 수 없는 상태:", status);
                }
            },
            error: function (xhr, status, error) {
                console.error("요청 에러:", error);
            }
        });
    }
});