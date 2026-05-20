// src/main/resources/static/admin/js/coupons.js

// 모달 열기
function openCouponModal() {
    console.log("JavaScript: openCouponModal called");
    // 폼 초기화
    document.querySelector('#couponForm').reset();
    document.querySelector('#couponModalTitle').innerText = '쿠폰 등록하기';
    document.querySelector('#couponForm').setAttribute('action', '/admin/coupons');
    document.querySelector('#couponModal').style.display = 'block';
    console.log("JavaScript: Coupon modal opened for creation");
}

// 모달 닫기
function closeCouponModal() {
    console.log("JavaScript: closeCouponModal called");
    document.querySelector('#couponModal').style.display = 'none';
    document.querySelector('#couponForm').reset();
    document.querySelector('#couponForm').setAttribute('action', '/admin/coupons');
    document.querySelector('#couponModalTitle').innerText = '쿠폰 등록/수정';
    console.log("JavaScript: Coupon modal closed");
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById('couponModal');
    if (event.target === modal) {
        console.log("JavaScript: Clicked outside modal - closing modal");
        modal.style.display = "none";
    }
}

// 수정 버튼 클릭 시 모달 열기 + 데이터 로드
function openEditModal(couponId) {
    console.log(`JavaScript: openEditModal called for coupon ID ${couponId}`);
    fetch(`/admin/coupons/${couponId}`)
        .then(response => {
            console.log("JavaScript: Fetch response received");
            if (!response.ok) {
                throw new Error('네트워크 응답이 정상이 아닙니다.');
            }
            return response.json();
        })
        .then(data => {
            console.log("JavaScript: Coupon data fetched:", data);
            if (data) {
                document.querySelector('#couponModalTitle').innerText = '쿠폰 수정하기';
                document.querySelector('#couponForm').setAttribute('action', '/admin/coupons/update');

                // 각 필드에 데이터 세팅
                const idField = document.querySelector('[name="id"]');
                if (idField) {
                    idField.value = data.id;
                } else {
                    console.warn('ID 필드를 찾을 수 없습니다.');
                }

                const codeField = document.querySelector('[name="code"]');
                if (codeField) {
                    codeField.value = data.code;
                } else {
                    console.warn('Code 필드를 찾을 수 없습니다.');
                }

                const nameField = document.querySelector('[name="name"]');
                if (nameField) {
                    nameField.value = data.name;
                } else {
                    console.warn('Name 필드를 찾을 수 없습니다.');
                }

                const contentField = document.querySelector('[name="content"]');
                if (contentField) {
                    contentField.value = data.content;
                } else {
                    console.warn('Content 필드를 찾을 수 없습니다.');
                }

                const deductPriceField = document.querySelector('[name="deductPrice"]');
                if (deductPriceField) {
                    deductPriceField.value = data.deductPrice;
                } else {
                    console.warn('Deduct Price 필드를 찾을 수 없습니다.');
                }

                const minPriceField = document.querySelector('[name="minPrice"]');
                if (minPriceField) {
                    minPriceField.value = data.minPrice;
                } else {
                    console.warn('Min Price 필드를 찾을 수 없습니다.');
                }

                const orderTypeField = document.querySelector('[name="orderType"]');
                if (orderTypeField) {
                    orderTypeField.value = data.orderType;
                } else {
                    console.warn('Order Type 필드를 찾을 수 없습니다.');
                }

                const expiredDateField = document.querySelector('[name="expiredDate"]');
                if (expiredDateField) {
                    expiredDateField.value = data.expiredDate ? data.expiredDate.substring(0, 10) : '';
                } else {
                    console.warn('Expired Date 필드를 찾을 수 없습니다.');
                }

                const statusField = document.querySelector('[name="status"]');
                if (statusField) {
                    statusField.value = data.status;
                } else {
                    console.warn('Status 필드를 찾을 수 없습니다.');
                }

                const modifiedDateField = document.querySelector('[name="modifiedDate"]');
                if (modifiedDateField) {
                    modifiedDateField.value = data.modifiedDate ? data.modifiedDate.substring(0, 10) : '';
                } else {
                    console.warn('Modified Date 필드를 찾을 수 없습니다.');
                }

                document.querySelector('#couponModal').style.display = 'block';
                console.log("JavaScript: Coupon modal opened for editing");
            } else {
                console.error("JavaScript: No data found for coupon ID", couponId);
                alert('쿠폰 데이터를 불러오는 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('JavaScript: Error fetching coupon data:', error);
            alert('쿠폰 데이터를 불러오는 중 오류가 발생했습니다.');
        });
}

// DOM 로드 후 수정 버튼 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    console.log("JavaScript: DOMContentLoaded event fired");

    // 수정 버튼 이벤트 리스너
    const editButtons = document.querySelectorAll('.coupon-edit-button');
    console.log(`JavaScript: Found ${editButtons.length} edit buttons`);
    editButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            const couponId = this.getAttribute('data-id');
            console.log(`JavaScript: Coupon ID from data-id attribute: ${couponId}`);
            openEditModal(couponId);
        });
    });
});
