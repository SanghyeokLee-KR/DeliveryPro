// 탭 전환 기능
document.querySelectorAll(".user-info-control-bar li").forEach((item) => {
    item.addEventListener("click", function (event) {
        if (this.getAttribute("data-target") !== "user-info-info-list") {
            event.preventDefault();
        }

        // 모든 탭에서 active 클래스 제거
        document.querySelectorAll(".user-info-control-bar li").forEach((i) => i.classList.remove("active"));
        this.classList.add("active"); // 클릭한 탭에 active 클래스 추가

        // 모든 리스트 숨기기
        document.querySelectorAll(
            "#user-info-order-list, #user-info-review-list, #user-info-info-list, #user-info-address-list, #user-info-reward-list"
        ).forEach((list) => {
            list.style.display = "none";
        });

        // 선택된 리스트만 표시
        const targetListId = this.getAttribute("data-target");
        const targetList = document.getElementById(targetListId);
        if (targetList) {
            targetList.style.display = "block";
        }
    });
});

// 리워드 미리보기 링크 클릭 시 user-info-reward-list 표시
document.getElementById("rewardLink").addEventListener("click", function (event) {
    event.preventDefault(); // 기본 동작을 방지 (링크 클릭 시 페이지 이동 방지)

    // 모든 리스트 숨기기
    document.querySelectorAll(
        "#user-info-order-list, #user-info-review-list, #user-info-info-list, #user-info-address-list, #user-info-reward-list"
    ).forEach((list) => {
        list.style.display = "none";
    });

    // user-info-reward-list만 표시
    const rewardList = document.getElementById("user-info-reward-list");
    if (rewardList) {
        rewardList.style.display = "block";
    }

    // 리워드 탭을 활성화
    document.querySelectorAll(".user-info-control-bar li").forEach((item) => {
        item.classList.remove("active");
    });
    const rewardTab = document.querySelector('.user-info-control-bar li[data-target="user-info-reward-list"]');
    if (rewardTab) {
        rewardTab.classList.add("active");
    }
});


document.addEventListener("DOMContentLoaded", () => {
    const activeTab = localStorage.getItem("activeTab") || "order";

    // 기본으로 모든 섹션 숨기기
    const sections = document.querySelectorAll(
        "#user-info-order-list, #user-info-review-list, #user-info-info-list, #user-info-address-list, #user-info-reward-list"
    );
    sections.forEach((section) => {
        section.style.display = "none";
    });

    // 모든 탭에서 active 클래스 제거
    const tabs = document.querySelectorAll(".user-info-control-bar li");
    tabs.forEach((tab) => tab.classList.remove("active"));

    // 선택된 탭 및 섹션 처리
    document.querySelector(`[data-target='user-info-${activeTab}-list']`).classList.add("active");
    document.getElementById(`user-info-${activeTab}-list`).style.display = "block";

    // 상태 초기화 (optional: 새로고침 후에도 유지하고 싶다면 이 부분 삭제)
    localStorage.removeItem("activeTab");
});

// 모달 열기
function openModal(fieldName) {
    const modal = document.getElementById(`modal-${fieldName}`);
    if (modal) {
        modal.style.display = "flex"; // Flexbox로 설정
    }
}

// 모달 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = "none"; // 숨기기
    }
}

// AJAX를 사용하여 필드 업데이트
function updateFieldAjax(field) {
    let value;

    if (field === 'birthday') {
        // 생년월일 값 가져오기
        const year = document.getElementById("year-select").value;
        const month = document.getElementById("month-select").value;
        const day = document.getElementById("day-select").value;

        if (!year || !month || !day) {
            alert("생년월일을 모두 선택해주세요.");
            return;
        }
        // YYYY-MM-DD 형식으로 값 결합
        value = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;

    } else if (field === 'gender') {
        const selectedGender = document.querySelector('input[name="gender-field"]:checked');
        value = selectedGender ? selectedGender.value : null;
    } else {
        const inputElement = document.getElementById(`${field}-field`);
        value = inputElement ? inputElement.value.trim() : null;
    }

    if (!value) {
        alert("수정할 값을 입력해주세요.");
        return;
    }

    $.ajax({
        url: "/api/member/update-modal",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({field, value}),
        success: function (response) {
            if (response.success) {
                alert(response.message || "업데이트가 성공적으로 완료되었습니다.");
                closeModal(`modal-${field}`);
                location.reload();
            } else {
                alert(response.message || "업데이트에 실패했습니다. 다시 시도해주세요.");
            }
        },
        error: function (xhr) {
            console.error("업데이트 중 오류 발생:", xhr.responseText);
            alert("업데이트 중 오류가 발생했습니다.");
        },
    });
}

// 회원탈퇴 섹션 토글
function toggleUserInfo() {
    const container = document.getElementById('user-info-info-list');
    const deleteForm = document.getElementById('delete');

    // 현재 섹션 상태 확인 및 토글
    if (deleteForm.style.display === 'block') {
        deleteForm.style.display = 'none';
        container.style.display = 'block';
    } else {
        deleteForm.style.display = 'block';
        container.style.display = 'none';
    }
}

// 탈퇴 취소
function cancelDeletion(event) {
    event.preventDefault();
    // 회원탈퇴 폼 숨기기
    document.getElementById('delete').style.display = 'none';
    // 개인정보 확인 섹션 보이기
    document.getElementById('user-info-info-list').style.display = 'block';
}

// 탈퇴하기
function Delete() {
    // 확인 체크박스 확인
    if (!document.getElementById("del_confirmation-checkbox").checked) {
        alert("탈퇴 전 모든 확인 사항에 동의해주세요.");
        return;
    }

    // AJAX 요청
    $.ajax({
        url: "/delete",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({}),
        success: function (response) {
            alert(response.message || "탈퇴가 완료되었습니다.");
            window.location.href = response.redirectUrl || "/index";
        },
        error: function (xhr) {
            console.error("탈퇴 중 오류 발생:", xhr.responseText);
            alert("탈퇴 중 오류가 발생했습니다. 다시 시도해주세요.");
        },
    });
}

// 로그인 기록 가져오기
function fetchLoginHistory() {
    $.ajax({
        url: "/api/login-history", // 서버에서 mId를 사용하여 로그인 기록을 가져오는 엔드포인트
        type: "GET",
        contentType: "application/json",
        success: function (data) {
            const tbody = document.querySelector(".user-info-login-container tbody");
            tbody.innerHTML = ""; // 기존 데이터 초기화
            if (data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="4">로그인 기록이 없습니다.</td></tr>`;
                return;
            }
            data.forEach(record => {
                const row = `
                <tr>
                    <td>${record.hisLoginDate || "N/A"}</td>
                    <td>${record.hisIpAddress || "N/A"}</td>
                    <td>${record.hisDeviceOs || "N/A"}</td>
                    <td>${record.hisBrowser || "N/A"}</td>
                </tr>`;
                tbody.insertAdjacentHTML("beforeend", row);
            });
        },
        error: function (xhr) {
            console.error("로그인 기록 조회 중 오류 발생:", xhr.responseText);
        },
    });
}

// 페이지 로드 시 자동 호출
document.addEventListener("DOMContentLoaded", () => {
    fetchLoginHistory();
});

function updateCharacterCount() {
    const inputField = document.getElementById('nickname-field');
    const charCount = document.getElementById('char-count');
    const currentLength = inputField.value.length;
    charCount.textContent = `${currentLength}/20`;
}

// 주문내역
const memId = $("#member-id").val();
$(document).ready(function() {
    $.ajax({
        url: `/orders/${memId}`, // API 엔드포인트
        method: 'POST',
        dataType: 'json',
        success: function (data) {
            // 성공 시 데이터 처리
            const $orderContainer = $(".user-info-order-container");
            data.forEach(order => {
                const orderItem = `
                    <div class="user-info-order-item">
                        <span class="user-info-order-date">${order.orderCreatedAt}</span>
                        <span class="user-info-order-status">배달완료</span>
                        <span class="user-info-order-detail">
                            <a href="#">상세주문내역 ></a>
                        </span>
                    </div>
                    <div class="user-info-oder-p-item">
                        <div class="user-info-order-img">
                        <img src="/store-img/store-menu-img/${order.menuImageUrl}" alt="메뉴 이미지" />
                        </div>
                        <div class="user-info-order-details">
                            <span class="user-info-order-store">${order.storeName}</span>
                            <span class="user-info-order-product">${order.menuName}</span>
                            <span class="user-info-order-p-date">주문일시 : ${order.orderCreatedAt}</span>
                            <span class="user-info-order-number">주문번호 : ${order.orderId}</span>
                     
                            <div class="user-info-order-review">   
                            <button class="user-info-o-review" onclick="reviewOpenModal('modal', '${order.orderId}', 
                            '${order.preStoId}','${order.menuId}');">리뷰작성하기</button>
                     
                          
                            <input type="hidden" class="order-id" value="${order.orderId}" />
                            <input type="hidden" class="preSto-id" value="${order.preStoId}" />
                            <input type="hidden" class="menu-id" value="${order.menuId}"/>
                            </div>
                        </div>
                        </div>
                    </div>
`;
                $orderContainer.append(orderItem);
            });
        },
        error: function (xhr, status, error) {
            console.error("주문 데이터를 가져오는 중 오류 발생:", error);
        }
    });
})
