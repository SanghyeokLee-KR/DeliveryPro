document.addEventListener("DOMContentLoaded", function () {
    const reviewButtons = document.querySelectorAll('.user-info-o-review');
    reviewButtons.forEach((button) => {
        button.addEventListener('click', function () {
            reviewOpenModal('modal','${order.orderId}','${order.prestoId}');
        });
    });
});

// 모달 열기 함수
function reviewOpenModal(modalId, orderId,preStoId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex'; // 모달을 보이게 설정
        // 숨겨진 필드에 주문 ID 설정
        const hiddenOrderId = document.getElementById('modal-order-id');
        const hiddenPreStoId = document.getElementById('modal-preSto-id');
        if (hiddenOrderId && hiddenPreStoId) {
            hiddenOrderId.value = orderId;
            hiddenPreStoId.value = preStoId;

        } else {
            console.error(`모달 내에 숨겨진 필드를 찾을 수 없습니다.`);
        }
    } else {
        console.error(`모달 ID '${modalId}'를 찾을 수 없습니다.`);
    }
}
// 모달 닫기 함수
function reviewCloseModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none'; // 모달을 숨김
    } else {
        console.error(`모달 ID '${modalId}'를 찾을 수 없습니다.`);
    }
}


// 별점 클릭 이벤트
document.querySelectorAll('#star-rating .star').forEach((star) => {
    star.addEventListener('click', function () {
        const value = this.getAttribute('data-value');
        document.getElementById('rating-value').value = value; // 평점 설정

        // 모든 별 초기화
        document.querySelectorAll('#star-rating .star').forEach((s) => s.classList.remove('selected'));

        // 클릭한 별까지 활성화
        for (let i = 0; i < value; i++) {
            document.querySelectorAll('#star-rating .star')[i].classList.add('selected');
        }

    });
});