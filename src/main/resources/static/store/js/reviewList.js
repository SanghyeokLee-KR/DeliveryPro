$(document).ready(function () {
// memId를 동적으로 설정하거나 서버에서 가져옵니다.
    const memId = $("#member-id").val(); /* 여기에 memId 값을 삽입하세요 */

    $.ajax({
        url: `/api/reviews/member/${memId}`, // 실제 API 엔드포인트로 변경하세요.
        method: 'POST', // 또는 'POST' 등 API에 맞게 설정
        dataType: 'json',
        success: function (data) {
            const $reviewContainer = $("#reviewList");

            data.forEach(review => {
// 별점에 따른 별 문자 생성
                let stars = '';
                for (let i = 0; i < Math.floor(review.reviewRating); i++) {
                    stars += '★';
                }
                for (let i = Math.floor(review.reviewRating); i < 5; i++) {
                    stars += '☆';
                }

// 리뷰 카드 HTML 생성
                const reviewCard = `
<div class="reviewList-card">
  <h3 class="reviewList-category">${review.nickname}</h3>
  <div class="reviewList-all">
    <div class="reviewList-left">
      <div class="spot">
        <strong class="reviewList-store-name">${review.preStoName}</strong>
      </div>
      <div class="reviewList-Order-details">
        <span class="reviewList-menu-info">${review.menuName}</span>
      </div>
      <div class="reviewList-rating">
        <span class="reviewList-score">${review.reviewRating}</span>
        <div class="reviewList-stars">${stars}</div>
      </div>
      <div class="reviewList-date-day">${review.reviewCreatedAt}</div>
    </div>
    <div class="reviewList-right">
      <p class="reviewList-text">${review.reviewContent}</p>
      <div class="review-images">
      <img src="/review-img/${review.reviewImage}" alt="리뷰 사진" width="150px">
      </div>
    </div>
  </div>
</div>
`;
                $reviewContainer.append(reviewCard);
            });
        },
        error: function (xhr, status, error) {
            console.error("리뷰 데이터를 가져오는 중 오류 발생:", error);
        }
    });
});