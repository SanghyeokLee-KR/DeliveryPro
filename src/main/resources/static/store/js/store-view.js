document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category');
    const preStoId = urlParams.get('storeId');
    if (preStoId) {
        check(preStoId)
        loadReviewData(preStoId)
        loadStoreInfo(preStoId)
        if (category) {
            activateTabAndLoadData(category);
        }
        loadMenuData(preStoId);
        loadCategoryData(preStoId);

    } else {
        console.error("Store ID가 URL에 포함되어 있지 않습니다.");
    }

    function check(preStoId) {
        // 페이지 로드 시 자동으로 가게 ID를 읽어와서 상태 확인 AJAX 호출 실행
        $.ajax({
            url: `/api/stores/check/${preStoId}`,
            type: "POST",
            success: function (PreStoStatus) {
                // 가게 상태에 따라 주문하기 버튼 업데이트
                if (PreStoStatus === "중지") {
                    $(".btn-order").text("영업시간이 아닙니다.").prop("disabled", true).css("background-color", "grey"); // 회색으로 변경;
                    $(".overlay").show();
                } else if (PreStoStatus === "승인") {
                    $(".btn-order").text("주문하기").prop("disabled", false);
                    $(".overlay").hide();
                }
            },
            error: function (xhr, status, error) {
                console.error("요청 에러:", error);
            }
        });
    }

// Ajax 호출
// 전역 변수 선언
    let list = [];          // 모든 리뷰 데이터를 저장
    let page = 1;           // 현재 페이지 번호
    let limit = 5;          // 한 페이지에 표시할 리뷰 수
    const block = 5;        // 페이지 블록 크기 (예: 1~5, 6~10)
    let count = 0;          // 전체 리뷰 개수

    $(document).ready(function () {
        // 리뷰 데이터 로드
        loadReviewData(preStoId);
        loadStoreInfo(preStoId)

        check(preStoId)

        // 한 페이지당 출력 갯수 변경 이벤트 (선택사항)
        $('#limit').change(() => {
            page = 1;
            limit = parseInt($('#limit').val());
            pagingList(page, list);
        });
    });

// 리뷰 데이터 로드 및 리뷰 개수 업데이트 함수
    function loadReviewData() {

        $.ajax({
            url: `/api/reviews/${preStoId}`, // 실제 API 엔드포인트로 변경하세요.
            method: 'POST', // 또는 'GET', API에 맞게 설정
            contentType: 'application/json', // JSON 형식으로 요청
            dataType: 'json',
            data: JSON.stringify({}), // 필요한 경우 추가 데이터 전송
            success: function (data) {
                console.log("서버에서 받은 데이터:", data);
                const $reviewCount = $(".store-view-review_num"); // 리뷰 개수를 표시할 요소

                // 데이터 구조에 따라 리스트 설정
                let list = Array.isArray(data) ? data : data.reviews || [];

                count = list.length;  // 전체 리뷰 개수 저장

                // 페이지네이션 실행 (데이터 설정 후 호출)
                pagingList(page, list);

                // 리뷰 개수 업데이트를 서버에 전송
                $.ajax({
                    url: `/api/reviews/updateReviewCount/${preStoId}`,
                    method: 'POST',
                    // contentType: 'application/json', // 폼 데이터 전송 시 제거
                    data: {
                        reviewCount: count // 서버로 리뷰 개수를 전송
                    },
                    success: function (response) {
                        console.log("리뷰 개수 업데이트 성공:", response);
                        // 클라이언트 표시 로직...
                    },
                    error: function (xhr, status, error) {
                        console.error("리뷰 개수 업데이트 오류:", error);
                    }
                });

                // 리뷰 개수 표시
                if (count > 0) {
                    const reviewCountText = `리뷰 ${count}`;
                    $reviewCount.html(reviewCountText);
                } else {
                    $reviewCount.html("리뷰 0");
                }
            },
            error: function (xhr, status, error) {
                console.error("리뷰 데이터를 가져오는 중 오류 발생:", error);
                $("#reviews").append("<p>리뷰를 불러오는 데 실패했습니다.</p>");
            }
        });
    }

// 페이지네이션 및 리뷰 카드 렌더링 함수
    // 페이지네이션 및 리뷰 카드 렌더링 함수
    function pagingList(currentPage, list) {
        count = list.length;  // 전체 리뷰 개수 산출
        let maxPage = Math.ceil(count / limit);  // 최대 페이지 수

        // 최소 페이지 수를 1로 설정
        maxPage = maxPage === 0 ? 1 : maxPage;

        // 현재 페이지가 최대 페이지를 초과하면 최대 페이지로 설정
        currentPage = Math.max(1, Math.min(currentPage, maxPage));

        // 출력할 인덱스 계산
        let startRow = (currentPage - 1) * limit;      // 예: 0, 5, 10, ...
        let endRow = currentPage * limit - 1;         // 예: 4, 9, 14, ...
        if (endRow >= count) {
            endRow = count - 1;
        }

        // 현재 블록 내 시작 및 끝 페이지 번호 계산
        let startPage = (Math.ceil(currentPage / block) - 1) * block + 1;  // 예: 1, 6, 11, ...
        let endPage = startPage + block - 1;                        // 예: 5, 10, 15, ...
        if (endPage > maxPage) {
            endPage = maxPage;
        }

        // 페이지 번호가 1 미만이 되지 않도록 설정
        startPage = Math.max(startPage, 1);

        // 리뷰 카드 HTML 생성 (페이지에 해당하는 리뷰만)
        let output = "";
        const currentReviews = list.slice(startRow, endRow + 1); // 현재 페이지에 해당하는 리뷰 추출

        currentReviews.forEach(review => {
            // 리뷰가 존재하는지 확인
            if (!review) {
                console.warn(`리뷰 데이터가 없습니다. 리뷰 ID: ${review ? review.reviewId : 'Unknown'}`);
                return;
            }

            // reviewRating이 존재하는지 확인
            if (typeof review.reviewRating !== 'number') {
                console.warn(`리뷰에 reviewRating이 없습니다. 리뷰 ID: ${review.reviewId}`);
                review.reviewRating = 0; // 기본값 설정 (필요 시 조정)
            }

            // 별점에 따른 별 문자 생성 (★, ☆, 반 별)
            let stars = '';
            let fullStars = Math.floor(review.reviewRating);
            let halfStars = review.reviewRating % 1 >= 0.5 ? 1 : 0;

            for (let j = 0; j < fullStars; j++) {
                stars += '<span class="full-star">★</span>';
            }
            if (halfStars) {
                stars += '<span class="half-star">☆</span>'; // 반 별 추가
            }
            for (let j = fullStars + halfStars; j < 5; j++) {
                stars += '<span class="empty-star">☆</span>'; // 빈 별 추가
            }

            // 리뷰 카드 HTML 생성
            output += `
            <div class="review-card" data-review-id="${review.reviewId}">
                <div class="review-header-left">
                    <div class="review-header">
                        <div class="review-category">${review.nickname}</div>
                        <div class="review-rating">
                            <div class="review-score">${review.reviewRating.toFixed(1)}</div>
                            <div class="review-stars">${stars}</div>
                        </div>
                    </div>
                    <div class="review-date-right">
                        <div class="review-date-day">${review.reviewCreatedAt}</div>
                    </div>
                </div>
                <div class="review-body">
                    <div class="review-menu-info"> <p> 주문 메뉴 : </p><p>　${review.menuName}</p></div>
                </div>
                <div class="review-footer">
                    <div class="review-text">${review.reviewContent}</div>
                    <div class="review-images">
                        ${review.reviewImage ? `<img src="/review-img/${review.reviewImage}" alt="리뷰 사진" width="200px">` : ''}
                    </div>
                </div>
                <div class="comment-area" data-review-id="${review.reviewId}"></div>
            </div>`;
        });

        // 리뷰 컨테이너에 리뷰 카드 HTML 추가
        $(".store-review-section").empty().append(output);

        // 페이지 번호 HTML 생성
        let pageNum = "";

        // 이전 블록으로 이동 버튼 (필요시)
        if (startPage > 1) {
            pageNum += `<a href="#" class="store-review-page" data-page="${startPage - 1}">«</a>`;
        }

        // 페이지 번호 추가
        for (let i = startPage; i <= endPage; i++) {
            if (currentPage === i) {
                pageNum += `<span class="store-review-page active" data-page="${i}"> ${i} </span>`;
            } else {
                pageNum += `<a href="#" class="store-review-page" data-page="${i}"> ${i} </a>`;
            }
        }

        // 다음 블록으로 이동 버튼 (필요시)
        if (endPage < maxPage) {
            pageNum += `<a href="#" class="store-review-page" data-page="${endPage + 1}">»</a>`;
        }

        // 페이지 번호 컨테이너가 없으면 생성
        if ($('.store-review-section #numbering').length === 0) {
            $('.store-review-section').append('<div id="numbering"></div>');
        }

        // 페이지 번호 HTML 추가
        $('.store-review-section #numbering').empty().append(pageNum);

        // 페이지 버튼 클릭 이벤트 처리
        $('.store-review-section #numbering .store-review-page').off('click').on('click', function (e) {
            e.preventDefault();
            // 숫자 버튼이 클릭된 경우 data-page 값 가져오기
            const selectedPage = parseInt($(this).data('page'));
            pagingList(selectedPage, list);
        });
        currentReviews.forEach(review => {
            loadComments(review.reviewId);
        });
    }

    function loadComments(reviewId) {
        $.ajax({
            type: "GET", // GET 방식
            url: `/api/comments/cList`, // 쿼리 파라미터로 변경
            data: {reviewId: reviewId}, // 쿼리 파라미터로 전달
            dataType: "json",
            success: function (comments) {
                console.log("받은 댓글 데이터:", comments);

                // 댓글 데이터가 배열인지 객체인지 확인
                if (Array.isArray(comments)) {
                    if (comments.length > 0) {
                        renderComments(comments, reviewId);
                    }
                } else if (typeof comments === 'object' && comments !== null) {
                    // 단일 댓글 객체일 경우 배열로 감싸서 처리
                    renderComments([comments], reviewId);
                }
            },
            error: function (xhr, status, error) {
                console.error("댓글 데이터를 불러오지 못했습니다:", xhr.responseText);
                $(`.review-card[data-review-id="${reviewId}"] .comment-area`).html('<p>댓글을 불러오는 데 실패했습니다.</p>');
            }
        });
    }

    // 댓글 렌더링 함수
    function renderComments(comments, reviewId) {
        let output = "";
        comments.forEach(comment => {
            output += `
                        <div class="comment-block-store-review" data-comment-id="${comment.commentId}">
                        <div class="comment-block-store-review-in">
                        <div class="comment-block-store-review-header">
                            <p class="comment-author">사장님</p>
                            <p class="comment-date">${escapeHtml(comment.commentDate)}</p> 
                            </div>
                            <p class="comment-content">${escapeHtml(comment.commentContents)}</p>   
                        </div>
                        </div>
                    `;
        });

        // 댓글 영역에 댓글 삽입
        $(`.review-card[data-review-id="${reviewId}"] .comment-area`).html(output);
    }

    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function (m) {
            return map[m];
        });
    }

    document.addEventListener("click", function (event) {
        if (event.target.closest(".minus") || event.target.closest(".plus")) {
            const button = event.target.closest(".minus") ? "minus" : "plus";
            const modalContent = document.querySelector(".modal-content");
            if (!modalContent) {
                console.error("모달 내용이 없습니다.");
                return;
            }

            const quantityElement = modalContent.querySelector(".quantity");
            const totalPriceElement = modalContent.querySelector(".total-price");
            const priceElement = modalContent.querySelector(".modal-menu-price");

            let currentQuantity = parseInt(quantityElement.textContent, 10);
            const price = parseFloat(priceElement.textContent.replace(/[^0-9.-]+/g, ""));

            if (button === "minus" && currentQuantity > 1) {
                currentQuantity--;
            } else if (button === "plus") {
                currentQuantity++;
            }

            // 수량 및 총 금액 갱신
            quantityElement.textContent = currentQuantity;
            totalPriceElement.textContent = (currentQuantity * price).toLocaleString() + "원";
        }
    });


    // 이벤트 위임을 사용하여 탭 클릭 이벤트 처리
    document.addEventListener('click', function (event) {
        // Store 탭 클릭 처리
        if (event.target.closest('.store-tabs .tab')) {
            const tab = event.target.closest('.store-tabs .tab');
            const tabType = tab.getAttribute('data-tab'); // 클릭된 탭의 타입 (menu, review, info 등)

            // 모든 탭의 active 클래스 제거 및 섹션 숨기기
            document.querySelectorAll('.store-tabs .tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.store-menu-section, .store-review-section, .store-info-section').forEach(section => {
                section.style.display = 'none';
            });

            // 클릭된 탭 활성화 및 연결된 섹션 표시
            tab.classList.add('active');
            const targetSection = document.querySelector(`.store-${tabType}-section`);
            if (targetSection) {
                targetSection.style.display = 'block';
            } else {
                console.error(`.store-${tabType}-section 요소를 찾을 수 없습니다.`);
            }
        }

        // 카테고리 탭 클릭 처리
        if (event.target.closest('.storelist-control-bar li')) {
            const tab = event.target.closest('.storelist-control-bar li');
            const category = tab.getAttribute('data-category');
            if (category) {
                const preStoId = urlParams.get('storeId');
                if (preStoId) {
                    window.location.href = `/storeList?category=${category}`;
                } else {
                    console.error("Store ID가 없습니다.");
                }
            } else {
                console.error("카테고리가 설정되지 않았습니다.");
            }
        }
    });

    // 카테고리 활성화 및 데이터 로드
    function activateTabAndLoadData(category) {
        document.querySelectorAll('.storelist-control-bar li').forEach(li => li.classList.remove('active'));

        const activeTab = document.querySelector(`.storelist-control-bar li[data-category="${category}"]`);
        if (activeTab) {
            activeTab.classList.add('active');
        } else {
            console.error("Category에 해당하는 탭을 찾을 수 없습니다.");
        }

        loadMenuDataByCategory(preStoId, category);
    }

    // 메뉴 데이터 로드 (전체)
    function loadMenuData(preStoId) {
        $.ajax({
            url: `/api/menu/${preStoId}`,
            method: "POST",
            success: function (data) {
                renderMenu(data, preStoId);

            },
            error: function () {
                alert("메뉴 데이터를 불러오지 못했습니다.");
            }
        });
    }

    // 특정 카테고리의 메뉴 데이터 로드
    function loadMenuDataByCategory(preStoId, category) {
        $.ajax({
            url: `/api/menu/${preStoId}`,
            method: "POST",
            data: {category: category},
            success: function (data) {
                renderMenu(data, preStoId);
            },
            error: function () {
                alert("선택한 카테고리의 메뉴 데이터를 불러오지 못했습니다.");
            }
        });
    }

    function setupModalEvents() {
        const menuItems = document.querySelectorAll(".menu-item");
        menuItems.forEach(item => {
            item.addEventListener("click", function () {
                openModal(this);
            });
        });

        const menuInfos = document.querySelectorAll(".menu-info");
        menuInfos.forEach(info => {
            info.addEventListener("click", function () {
                openModal(this);
            });
        });
    }

    function openModal(element, menuId) {
        const modal = document.getElementById("modal");
        const modalContent = document.getElementById("modal-menu-details");

        // 모달 내용을 업데이트
        const menuName = element.querySelector(".menu-name, .main-menu-name").textContent;
        const menuPrice = parseFloat(
            element.querySelector(".menu-price, .main-menu-price").textContent.replace(/[^0-9.-]+/g, "")
        );
        const menuImage = element.querySelector(".preview-image, .menu-image").src;

        modalContent.innerHTML = `
        <img src="${menuImage}" alt="${menuName}" class="modal-menu-image"/>
        <div class="modal-menu-name">${menuName}</div>
        <div class="modal-price">
            <strong>가격</strong>
            <div class="modal-menu-price">${menuPrice.toLocaleString()}원</div>
        </div>
        <div class="modal-menu-quantity-control">
            <strong>수량</strong>
            <a class="minus"></a>
            <div class="quantity">1</div>
            <a class="plus"></a>
        </div>
        <div class="modal-total-wrap">
            <strong>총 주문금액</strong>
            <div class="total">
                <strong class="total-price">${menuPrice.toLocaleString()}원</strong>
            </div>
        </div>
        <div class="modal-btn-wrap">
            <button type="button" class="cart" data-menu-id="${menuId}">장바구니 추가</button>
            <button type="button" class="add-cart" data-id="${menuId}">주문표에 추가</button>
        </div>`;

        // 모달 열기
        if (modal) {
            modal.classList.remove("hidden");
        }
    }


    // 모달 닫기 이벤트
    document.addEventListener("click", function (event) {
        const modal = document.getElementById("modal");
        const closeButton = event.target.closest(".close-button");
        if (closeButton || (modal && event.target === modal)) {
            closeModalFunction();
        }
    })

    // renderMenu 호출 후 모달 이벤트 연결
    function renderMenu(menuItems) {
        const menuContainer = document.querySelector("#menu-container");
        if (!menuContainer) {
            console.error("#menu-container 요소를 찾을 수 없습니다.");
            return;
        }

        menuContainer.innerHTML = "";

        const categorizedMenus = {
            메인메뉴: menuItems.filter(item => item.menuCategory === "메인메뉴"),
            사이드메뉴: menuItems.filter(item => item.menuCategory === "사이드메뉴"),
            소스: menuItems.filter(item => item.menuCategory === "소스"),
            음료: menuItems.filter(item => item.menuCategory === "음료")
        };

        const categoryHtml = Object.entries(categorizedMenus).map(([category, items]) => {
            const itemsHtml = items.map(item => `
            <div class="menu-item" data-Id="${item.menuId}">
                <div class="menu-details">
                    <li class="main-menu-name">${item.menuName}</li>
                    <li class="main-menu-price">${item.menuPrice.toLocaleString()}원</li>
                </div>
                <img src="/store-img/store-menu-img/${item.menuPictureUrl}" alt="${item.menuName}" class="menu-image" />
            </div>`).join("");

            return `
            <div class="menu-section">
                <div class="store-other-menu-header" data-toggle="menu">${category}</div>
                <ul class="menu-item-list">
                    ${itemsHtml}
                </ul>
            </div>`;
        }).join("");

        const mainMenuPreview = categorizedMenus["메인메뉴"];
        // 인기메뉴(메뉴 인기 여부가 1인 경우)만 필터링하여 표시
        const previewHtml = mainMenuPreview
            .filter(item => item.menuPopularity === 1) // 인기메뉴만 필터링
            .map(item => `
        <div class="menu-info" data-Id="${item.menuId}">
            <img src="/store-img/store-menu-img/${item.menuPictureUrl}" alt="${item.menuName}" class="preview-image"/>
            <div class="menu-name">${item.menuName}</div>
            <div class="menu-price">${item.menuPrice.toLocaleString()}원</div>
        </div>
    `).join("");

        menuContainer.innerHTML = `
        <div class="store-menu-container">
            <div class="store-detail-container">
                <div class="store-tabs">
                    <div class="tab active" data-tab="menu">메뉴</div>
                    <div class="tab" data-tab="review">리뷰</div>
                    <div class="tab" data-tab="info">정보</div>
                </div>

                <div class="store-menu-section">
                    <div class="menu-preview-list">
                        <div class="menu-preview">
                            ${previewHtml}
                        </div>
                    </div>

                    <div class="store-other-menus">
                        ${categoryHtml}
                    </div>
                </div>
 
                <div class="store-review-section" style="display: none;">
                </div>
        
                <div class="store-info-section" style="display: none;">       
                </div>
            </div>  
        </div>`;

        setupModalEvents();
    }


    // 카테고리 데이터 로드
    function loadCategoryData(preStoId) {
        $.ajax({
            url: `/api/stores/${preStoId}`,
            method: 'POST',
            data: {storeId: preStoId},
            dataType: 'json',
            success: function (data) {
                data.forEach(restaurant => {
                    // 평균 별점 API 호출
                    $.ajax({
                        url: `api/stores/reviewStarCount`,
                        method: 'POST',
                        data: {storeId: restaurant.preStoId},
                        success: function (averageRating) {
                            restaurant.preStoRating = averageRating; // 평균 별점 업데이트
                        },
                        error: function () {
                            console.error(`별점 평균을 가져오는 데 실패했습니다. 가게 ID: ${restaurant.preStoId}`);
                        }
                    });
                });

                document.querySelector("#storelist").innerHTML = data.map((restaurant) => `
                    <div class="store-view-item clearfix" style="cursor: pointer;" 
                         data-category="${restaurant.preStoCategory}" 
                         data-id="${restaurant.preStoId}">
                        <table>
                            <tbody>
                                <tr>
                                    <td>
                                        <div class="store-view-rlogo" 
                                             style="background-image: url('/store-img/store-main-img/${restaurant.preStoPhoto || '/img/store.png'}');">
                                              <div class="overlay" style="display: none;">영업시간이 아닙니다</div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="store-view-restaurants-info">
                                            <div class="store-view-restaurant-name" title="${restaurant.preStoName}">
                                                ${restaurant.preStoName}
                                            </div>
                                           
                                            <div class="store-view-stars">
                                                <span>
                                                    <span class="store-view-ico-star1">
                                                      ★ ${restaurant.preStoRating}
                                                    </span>
                                                </span>
                                                <span class="store-view-review_num">
                                                    리뷰 ${restaurant.preStoReviewCount}
                                                </span>
                                            </div>
                                               <li class="min-price">${restaurant.preStoMinOrderAmount.toLocaleString()}원 이상 배달</li>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>`).join("");


            },
            error: function () {
                document.querySelector("#storelist").innerHTML = "<p>데이터를 가져오는 중 오류가 발생했습니다.</p>";
            }
        });
    }

    document.addEventListener("click", function (event) {
        if (event.target.closest(".add-cart")) {
            const modalContent = document.querySelector(".modal-content");
            if (!modalContent) {
                console.error("모달 내용이 없습니다.");
                return;
            }

            const menuId = modalContent.querySelector(".add-cart").getAttribute("data-id");
            const quantity = parseInt(modalContent.querySelector(".quantity").textContent, 10);

            if (!menuId) {
                console.error("data-id를 가져올 수 없습니다.");
                return;


            }

            // 주문표 업데이트
            renderOrder(menuId, quantity, true);

            closeModalFunction()
        }
    });

    function closeModalFunction() {
        const modal = document.getElementById("modal");
        if (modal) {
            modal.classList.add("hidden");
        }
    }

    document.addEventListener("click", function (event) {
        const menuItem = event.target.closest(".menu-item, .menu-info"); // 클릭된 menu-item 찾기

        if (menuItem) {
            const menuId = menuItem.getAttribute("data-id");// menu-item의 data-id 가져오기

            // 모달 열기 함수 호출
            openModal(menuItem, menuId);
        }
    });

    function renderOrder(menuId, quantity, updateMode = false) {
        const orderListContainer = document.querySelector("#order-list");

        if (!orderListContainer) {
            console.error("#order-list 요소를 찾을 수 없습니다.");
            return;
        }

        // 기존 항목 확인
        const existingOrderItem = orderListContainer.querySelector(`.order-item[data-id="${menuId}"]`);
        if (existingOrderItem) {
            const quantityElement = existingOrderItem.querySelector(".menu-quantity1");
            const priceElement = existingOrderItem.querySelector(".menu-price");

            const price = parseFloat(priceElement.textContent.replace(/[^0-9.-]+/g, ""));
            const currentQuantity = parseInt(quantityElement.textContent, 10);

            if (updateMode) {
                // 수량 누적
                const updatedQuantity = currentQuantity + quantity;
                quantityElement.textContent = updatedQuantity;
            }

            // 총 주문 가격 다시 계산
            calculateTotalPrice();
            return;
        }

        // 새로운 주문 항목 추가
        $.ajax({
            url: `/api/menu/menu/${menuId}`,
            method: 'POST',
            dataType: 'json',
            success: function (items) {
                items.forEach(item => {
                    const orderItem = document.createElement("div");
                    orderItem.classList.add("order-item");
                    orderItem.setAttribute("data-id", item.menuId);
                    orderItem.innerHTML = `
                <div class="menu-name">${item.menuName}</div>
                <div class="menu-garo">
                    <div class="menu-garo-1">
                        <button class="menu-cancel">✖</button>
                        <div class="menu-price">${item.menuPrice.toLocaleString()}원</div>
                    </div>
                    <div class="menu-quantity">
                        <button class="menu-minus">-</button>
                        <div class="menu-quantity1">${quantity}</div>
                        <button class="menu-plus">+</button>
                        
                    </div>
                </div>`;
                    orderListContainer.appendChild(orderItem);
                });

                // 총 주문 가격 다시 계산
                calculateTotalPrice();
                setupCancelButtons();
                setupQuantityButtons();
            },
            error: function (xhr, status, error) {
                console.error("데이터를 가져오는 중 오류가 발생했습니다:", error);
                alert("주문 데이터를 불러오는 중 오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }

    function setupCancelButtons() {
        const cancelButtons = document.querySelectorAll(".menu-cancel");

        cancelButtons.forEach(button => {
            button.addEventListener("click", () => {
                const orderItem = button.closest(".order-item");
                if (orderItem) {
                    orderItem.remove(); // 항목 삭제
                    calculateTotalPrice(); // 총 가격 갱신
                }
            });
        });
    }

    function setupQuantityButtons() {
        const orderItems = document.querySelectorAll(".order-item");

        orderItems.forEach(orderItem => {
            const minusButton = orderItem.querySelector(".menu-minus");
            const plusButton = orderItem.querySelector(".menu-plus");
            const quantityElement = orderItem.querySelector(".menu-quantity1");
            const priceElement = orderItem.querySelector(".menu-price");

            if (minusButton && plusButton && quantityElement && priceElement) {
                const unitPrice = parseFloat(priceElement.textContent.replace(/[^0-9.-]+/g, ""));

                minusButton.addEventListener("click", () => {
                    let quantity = parseInt(quantityElement.textContent, 10);
                    if (quantity > 1) {
                        quantity--;
                        quantityElement.textContent = quantity;
                        calculateTotalPrice(); // 총 가격 갱신
                    }
                });

                plusButton.addEventListener("click", () => {
                    let quantity = parseInt(quantityElement.textContent, 10);
                    quantity++;
                    quantityElement.textContent = quantity;
                    calculateTotalPrice(); // 총 가격 갱신
                });
            }
        });
    }

// 총 가격 계산 함수
    function calculateTotalPrice() {
        const orderListContainer = document.querySelector("#order-list");
        const totalPriceElement = document.querySelector(".total-price");
        let totalOrderPrice = 0;

        // 주문표 항목 순회
        orderListContainer.querySelectorAll(".order-item").forEach(orderItem => {
            const priceElement = orderItem.querySelector(".menu-price");
            const quantityElement = orderItem.querySelector(".menu-quantity1");

            if (priceElement && quantityElement) {
                const priceText = priceElement.textContent.replace(/[^0-9.-]+/g, "");
                const quantityText = quantityElement.textContent.replace(/[^0-9]+/g, "");

                const price = parseFloat(priceText);
                const quantity = parseInt(quantityText, 10);

                if (!isNaN(price) && !isNaN(quantity)) {
                    totalOrderPrice += price * quantity;
                } else {
                    console.error("유효하지 않은 값:", {priceText, quantityText});
                }
            } else {
                console.error("요소가 없습니다:", {priceElement, quantityElement});
            }
        });

        // 총 주문 가격 업데이트
        if (totalPriceElement) {
            totalPriceElement.textContent = `총 주문 가격: ${totalOrderPrice.toLocaleString()}원`;
        }
    }

    document.addEventListener('click', (event) => {
        const header = event.target.closest('.store-other-menu-header');
        if (header) {
            const menuList = header.nextElementSibling;
            if (menuList) {
                menuList.classList.toggle('active');
                document.querySelectorAll('.menu-item-list').forEach(list => {
                    if (list !== menuList) {
                        list.classList.remove('active');
                    }
                });
            } else {
                console.error('해당 헤더에 연결된 .menu-item-list를 찾을 수 없습니다.');
            }
        }
    });

    document.addEventListener("click", function (event) {
        if (event.target.closest(".btn-order")) {

            const memId = document.getElementById("memId")?.value;
            if (memId === "false") {
                alert("로그인 후 이용해주세요!");
                window.location.href = "/mLoginForm";
                return; // 여기서 함수 종료, 결제 진행 막음
            }

            const orderItems = [];
            const orderListContainer = document.querySelector("#order-list");

            if (!orderListContainer || !orderListContainer.querySelectorAll(".order-item").length) {
                alert("주문표가 비어 있습니다.");
                return;
            }

            let totalAmount = 0;
            orderListContainer.querySelectorAll(".order-item").forEach(orderItem => {
                // `order-item` 내부 데이터 추출
                const menuName = orderItem.querySelector(".menu-name")?.textContent || "";
                const quantity = parseInt(orderItem.querySelector(".menu-quantity1")?.textContent || "0", 10);
                const menuPrice = parseFloat(orderItem.querySelector(".menu-price")?.textContent.replace(/[^0-9.-]+/g, "") || "0");

                // 외부에 위치한 `.total-price`를 직접 계산
                const calculatedTotalPrice = menuPrice * quantity;

                // 유효한 데이터인지 확인 후 누적
                if (!isNaN(menuPrice) && !isNaN(quantity)) {
                    orderItems.push({menuName, quantity, totalPrice: calculatedTotalPrice, menuPrice});
                    totalAmount += calculatedTotalPrice;
                } else {
                    console.warn("잘못된 데이터:", {menuName, quantity, menuPrice});
                }
            });

            const minOrderElement = document.querySelector('.min-price');
            if (minOrderElement) {
                // 예: "20000원 이상 배달" → 숫자만 추출
                const minOrderText = minOrderElement.textContent;
                const minOrderAmount = parseInt(minOrderText.replace(/[^0-9]/g, ""), 10);
                if (totalAmount < minOrderAmount) {
                    alert(`최소 주문 금액은 ${minOrderAmount.toLocaleString()}원 이상입니다. 주문 금액을 확인해주세요.`);
                    return; // 주문 진행 중단
                }
            }

            // 데이터를 localStorage에 저장
            localStorage.setItem("orderItems", JSON.stringify(orderItems));
            localStorage.setItem("totalAmount", totalAmount);

            // 결제 페이지로 이동
            window.location.href = "/payment";
        }
    });

    function loadStoreInfo(preStoId) {
        $.ajax({
            url: `/api/stores/info/${preStoId}`,
            type: "POST",
            dataType: 'json',
            success: function (result) {
                const infoHtml = `
              <div class="main-info-container">
        <!-- 정보 전체 -->
        <div class="info-president-body">
            <div class="info-president-alarm">
                <div class="president-header">
                    <p>가게 소개</p>
                </div>
                <hr class="info-hr">
                <div class="info-president-body">
                    <a class="text-text">${result.preStoIntro}</a><br/><br/></br></br></br>
                </div>
            </div>
        </div>

    <!-- 업체 정보 -->
    <div class="info-store">
        <div class="store-header">
            <img alt="업체정보" src="/store/img/icons/정보-01.svg" class="info-img">
            <p>업체 정보</p>
        </div>
        <hr class="info-hr">
        <!-- 영업 시간 -->
        <div class="info-body">
        <div class="business-hour">
            <p class="info-left">영업시간</p>
            <p class="info-right">${result.preStoOpeningHours}</p>
        </div>
        <!-- 전화 번호-->
        <div class="business-phone">
            <p class="info-left">전화번호</p>
            <p class="info-right">${result.preStoPhone}</p>
        </div>
        <!-- 주소 -->
        <div class="business-addr">
            <p class="info-left">주소</p>
            <p class="info-right"> ${result.preStoAddress}</p>
        </div>
        </div>
    </div>

    <!-- 결제 정보 -->
    <div class="info-pay">
        <div class="pay-header">
            <img alt="업체정보" src="/store/img/icons//정보-02.svg" class="info-img">
            <p>결제 정보</p>
        </div>
        <hr class="info-hr">
        <div class="info-body">
        <!-- 최소 주문 금액 -->
        <div class="min-pay">
            <p class="info-left">최소주문금액</p>
            <p class="info-right">${result.preStoMinOrderAmount.toLocaleString()}원</p>
        </div>
        <!-- 결제 수단-->
        <div class="pay-method">
            <p class="info-left">결제수단</p>
            <p class="info-right">신용카드, 현금, 요기서결제</p>
        </div>
    </div>

    </div>

    <!-- 사업자 정보 -->
    <div class="info-business">
        <div class="business-header">
            <img alt="사업자정보" src="/store/img/icons//정보-03.svg" class="info-img">
            <p>사업자 정보</p>
        </div>
        <hr class="info-hr">
        <div class="info-body">
            <!-- 최소 주문 금액 -->
            <div class="info-store-name">
                <p class="info-left">상호명</p>
                <p class="info-right">${result.preStoName}</p>
            </div>
            <!-- 결제 수단-->
            <div class="info-business-number">
                <p class="info-left">사업자등록번호</p>
                <p class="info-right">${result.preMemBizRegNo}</p>
            </div>
        </div>
    </div>
            `;

                $(".store-info-section").html(infoHtml)
            },
            error: function (xhr, status, error) {
                console.error("가게 정보를 로드하는 중 오류 발생:", error);
            }
        });
    }


    $(document).on('click', '.cart', function () {
        const modalContent = document.getElementById("modal-menu-details");
        const menuId = modalContent.querySelector(".cart").getAttribute("data-menu-id");  // 메뉴 ID 가져오기
        const orderItems = JSON.parse(localStorage.getItem("orderItems"));  // 로컬 스토리지에서 주문 항목 가져오기

        // orderItems가 유효한지 체크
        if (!orderItems || orderItems.length === 0) {
            alert('장바구니에 아이템이 없습니다.');
            return;
        }


        // Add to cart request
        $.ajax({
            url: `/addCart/${menuId}`,  // 장바구니 추가 엔드포인트
            type: 'POST',
            contentType: 'application/json',  // JSON 형식으로 데이터 전송
            data: JSON.stringify({orderItems: orderItems}),  // JSON.stringify로 데이터 변환
            success: function (response) {
                alert('장바구니에 추가되었습니다!');
            },
            error: function () {
                alert('장바구니 추가에 실패했습니다.');
            }
        });
    });
});