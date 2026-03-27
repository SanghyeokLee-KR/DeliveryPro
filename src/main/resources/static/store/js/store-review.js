// HTML 이스케이프 함수 (XSS 방지)
function escapeHtml(text) {
    if (!text) return "";
    return text.replace(/[&<>"']/g, match => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    })[match]);
}

// 댓글 렌더링 함수
function renderComments(comments, reviewId) {
    let output = "";
    comments.forEach(comment => {
        const commentId = comment.commentId; // 댓글 ID
        const commentContents = escapeHtml(comment.commentContents);
        const formattedDate = comment.commentDate.split(' ')[0];
        output += `
            <div class="comment-block owner-comment" data-comment-id="${commentId}">
                <p class="comment-author">사장님</p>
                <p class="comment-content">${escapeHtml(commentContents)}</p>
                <p class="comment-date">${formattedDate}</p>
                <div class="comment-actions">
                    <button class="cmodify" data-reviewid="${comment.reviewId}" data-commentid="${commentId}">수정</button>
                    <button class="cdelete" data-reviewid="${comment.reviewId}" data-commentid="${commentId}">삭제</button>
                </div>
            </div>

            <!-- 수정 모달 -->
            <div class="store-review-modify-modal" id="modal-replyModify-${commentId}" style="display: none;">
                <div class="store-review-modify-modal-content">
                    <span class="store-review-modify-close-btn" onclick="closeModal('modal-replyModify-${commentId}')">&times;</span>
                    <div class="store-review-modify-modal-all">
                        <div class="store-review-modify-modal-header">
                            <p>댓글 수정</p>
                        </div>
                        <div class="store-review-modify-modal-body">
                            <div class="store-review-modify-change-box">
                                <div class="store-review-input-con">
                                    <textarea rows="3" cols="40" class="commentContents" placeholder="수정할 내용을 입력해주세요.">${commentContents}</textarea>
                                </div>
                            </div>
                        </div>
                        <div class="store-review-modify-modal-check-btn">
                            <button class="submit-modify-btn" data-comment-id="${commentId}" data-review-id="${reviewId}">수정</button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    });
    $(`.review-card[data-review-id="${reviewId}"] .cmtArea`).html(output);
}

let list = [];
let page = 1;       // 현재 페이지 번호
let limit = 5;      // 한 페이지에 출력될 리뷰 카드 갯수
const block = 5;    // 한 블록에 출력될 페이지 번호 개수
let count = 0;      // 전체 리뷰 갯수

$(document).ready(function () {
    // preStoId 값 가져오기 (hidden input 혹은 다른 요소에서)
    const preStoIdElement = document.getElementById("preSto-id");
    if (!preStoIdElement) {
        console.error("preSto-id 요소를 찾을 수 없습니다.");
        return;
    }
    const preStoId = preStoIdElement.value;
    if (!preStoId) {
        console.error("preSto-id의 값이 비어 있습니다.");
        return;
    }

    // 리뷰 데이터 AJAX 호출 (검색 기능 제외)
    $.ajax({
        url: `/api/reviews/${preStoId}`,
        method: 'POST',  // API에 맞게 설정 (POST 혹은 GET)
        dataType: 'json',
        success: function (result) {
            // result: 리뷰 데이터 배열
            list = result;
            pagingList(page, list);  // 초기 페이지네이션 실행
            calculateAverageFromReviews();  // 평균 별점 및 리뷰별 카운트 계산
        },
        error: function (xhr, status, error) {
            console.error("리뷰 데이터를 가져오는 중 오류 발생:", error);
        }
    });

    // 한 페이지당 출력 갯수 변경 이벤트 (필요시)
    $('#limit').change(() => {
        page = 1;
        limit = parseInt($('#limit').val());
        pagingList(page, list);
    });

    // 모달 닫기 클릭 시 이벤트 위임
    $(document).on('click', '.store-review-modify-close-btn', function () {
        const modalId = $(this).closest('.store-review-modify-modal').attr('id');
        closeModal(modalId);
    });

    // 댓글 제출 버튼 클릭 이벤트 위임
    $(document).on('click', '.submit-comment-btn', function () {
        const reviewId = $(this).data('review-id');
        const modalId = `modal-replyWrite-${reviewId}`;
        const $modal = $(`#${modalId}`);
        const commentContent = $modal.find('.commentContents').val().trim();

        if (commentContent === "") {
            alert('댓글 내용을 입력해주세요.');
            return;
        }

        if (confirm('댓글을 입력하시겠습니까?')) {
            $.ajax({
                type: "POST",
                url: "/api/comments/cWrite",
                contentType: "application/json", // JSON 형식으로 요청
                data: JSON.stringify({
                    "reviewId": reviewId,                   // reviewId 전달
                    "commentContents": commentContent       // 댓글 내용 전달
                }),
                dataType: "json",
                success: (comment) => {
                    console.log("댓글 등록 성공: ", comment);

                    // 모달 닫기
                    closeModal(modalId);

                    // 댓글 표시 영역 업데이트
                    loadComments(reviewId); // 모든 댓글을 다시 로드하여 렌더링
                },
                error: (xhr, status, error) => {
                    console.error("AJAX 요청 실패: ", xhr.responseText);
                    alert('댓글 등록(cWrite) 통신 실패!');
                }
            });
        }
    });
    $(document).on('click', '.cmodify', function () {
        const commentId = $(this).data('commentid'); // 댓글 ID
        const reviewId = $(this).data('reviewid');  // 리뷰 ID
        const modalId = `modal-replyModify-${commentId}`; // 수정 모달 ID

        console.log("Comment ID:", commentId, "Review ID:", reviewId, "Modal ID:", modalId);

        // 모달 열기
        openModal(modalId);

        // 기존 댓글 내용 로드
        const existingContent = $(`.comment-block[data-comment-id="${commentId}"] .comment-content`).text();
        console.log("Existing Content:", existingContent);

        // 모달에 기존 댓글 내용 설정
        $(`#${modalId}`).find('.commentContents').val(existingContent.trim());
    });

// 수정 모달 내 수정 버튼 클릭 처리
    $(document).on('click', '.submit-modify-btn', function () {
        const commentId = $(this).data('comment-id'); // 댓글 ID
        const reviewId = $(this).data('review-id');  // 리뷰 ID
        const modalId = `modal-replyModify-${commentId}`; // 수정 모달 ID
        const newContent = $(`#${modalId}`).find('.commentContents').val().trim(); // 수정된 내용

        console.log("New Content:", newContent);

        if (newContent === "") {
            alert("수정할 내용을 입력해주세요.");
            return;
        }

        if (confirm("댓글을 수정하시겠습니까?")) {
            $.ajax({
                type: "POST",
                url: "/api/comments/cModify",
                contentType: "application/json",
                data: JSON.stringify({
                    "commentId": commentId,
                    "reviewId": reviewId,
                    "commentContents": newContent
                }),
                dataType: "json",
                success: (updatedComment) => {
                    console.log("댓글 수정 성공:", updatedComment);

                    // 모달 닫기
                    closeModal(modalId);

                    // 수정된 댓글 다시 로드
                    loadComments(reviewId);
                },
                error: (xhr, status, error) => {
                    console.error("AJAX 요청 실패:", xhr.responseText);
                    alert("댓글 수정(cModify) 통신 실패!");
                }
            });
        }
    });

    // 댓글 삭제 버튼 클릭 이벤트 위임
    $(document).on('click', '.cdelete', function () {
        const commentId = $(this).data('commentid');
        const reviewId = $(this).data('reviewid');
        const $commentBlock = $(this).closest('.comment-block');

        if (confirm('댓글을 삭제하시겠습니까?')) {
            $.ajax({
                type: "POST",
                url: "/api/comments/cDelete",
                contentType: "application/json",
                data: JSON.stringify({
                    "commentId": commentId,
                    "reviewId": reviewId
                }),
                dataType: "json",
                success: (response) => {
                    console.log("댓글 삭제 성공: ", response);
                    // 댓글 삭제 후 UI에서 제거
                    $commentBlock.remove();
                    // 답글 버튼 다시 표시
                    $(`.review-card[data-review-id="${reviewId}"] .review-reply-btn`).show();
                },
                error: (xhr, status, error) => {
                    console.error("AJAX 요청 실패: ", xhr.responseText);
                    alert('댓글 삭제(cDelete) 통신 실패!');
                }
            });
        }
    });
});

// 모달 열기 함수
function openModal(modalId) {
    $(`#${modalId}`).fadeIn();
    // 해당 리뷰의 댓글 로드
    // loadComments 모달 열기와는 별개로 댓글을 로드하는 것이므로 여기서는 호출하지 않습니다.
}

// 모달 닫기 함수
function closeModal(modalId) {
    $(`#${modalId}`).fadeOut();
}

// 페이지네이션 및 리뷰 카드 렌더링 함수
function pagingList(page, list) {
    count = list.length;  // 전체 리뷰 갯수 산출
    let maxPage = Math.ceil(count / limit);  // 최대 페이지 수

    if (page > maxPage) {
        page = maxPage;
    }

    // 출력할 인덱스 계산
    let startRow = (page - 1) * limit;      // 예: 0, 5, 10, ...
    let endRow = page * limit - 1;          // 예: 4, 9, 14, ...
    if (endRow >= count) {
        endRow = count - 1;
    }

    // 현재 블록 내 시작 및 끝 페이지 번호 계산
    let startPage = (Math.ceil(page / block) - 1) * block + 1;  // 예: 1, 6, 11, ...
    let endPage = startPage + block - 1;                     // 예: 5, 10, 15, ...
    if (endPage > maxPage) {
        endPage = maxPage;
    }

    // 리뷰 카드 HTML 생성 (페이지에 해당하는 리뷰만)
    let output = "";
    for (let i = startRow; i <= endRow; i++) {
        const review = list[i];

        // 별점에 따른 별 문자 생성 (★, ☆)
        let stars = "";
        for (let j = 0; j < Math.floor(review.reviewRating); j++) {
            stars += '★';
        }
        for (let j = Math.floor(review.reviewRating); j < 5; j++) {
            stars += '☆';
        }

        output += `
            <div class="review-card" data-review-id="${review.reviewId}">
              <div class="review-category">${escapeHtml(review.nickname)}
                               <button class="review-reply-btn" data-review-id="${review.reviewId}" onclick="openModal('modal-replyWrite-${review.reviewId}')">답글쓰기</button></div>
                               
              <!-- 답글쓰기 모달 -->
              <div class="store-review-modify-modal" id="modal-replyWrite-${review.reviewId}" style="display: none;">
                <div class="store-review-modify-modal-content">
                    <span class="store-review-modify-close-btn" onclick="closeModal('modal-replyWrite-${review.reviewId}')">&times;</span>
                    <div class="store-review-modify-modal-all">
                        <div class="store-review-modify-modal-header">
                            <p>답글 입력</p>
                        </div>
                        <div class="store-review-modify-modal-body">
                            <div class="store-review-modify-change-box">
                                <div class="store-review-input-con">
                                    <textarea rows="3" cols="40" class="commentContents" data-review-id="${review.reviewId}"></textarea> 
                                </div>
                            </div>
                        </div>
                        <div class="store-review-modify-modal-check-btn">
                            <button class="submit-comment-btn" data-review-id="${review.reviewId}">확인</button>
                        </div>
                    </div>
                </div> 
              </div>

              <div class="review-all">
                <div class="review-left">
                  <div class="spot">
                    <strong class="review-store-name">${escapeHtml(review.preStoName)}</strong>
                  </div>
                  <div class="review-Order-details">
                    <span class="review-menu-info">${escapeHtml(review.menuName)}</span>
                  </div>
                  <div class="review-rating">
                    <span class="review-score">${review.reviewRating}.0</span>
                    <div class="review-stars">${stars}</div>
                  </div>
                  <div class="review-date-day">${escapeHtml(review.reviewCreatedAt)}</div>
                </div>
                <div class="review-right">
                  <p class="review-text">${escapeHtml(review.reviewContent)}</p>
                  <div class="review-images">
                    <img src="/review-img/${escapeHtml(review.reviewImage)}" alt="리뷰 사진" width="150px">
                  </div>
                 <div class="review-interaction">
                 </div>
                </div>
              </div>
             <div class="cmtArea" data-review-id="${review.reviewId}"></div>
            </div>
        `;
    }
    $("#reviews").empty().append(output);

    let pageNum = "";

    // 첫 페이지 항상 추가 (현재 페이지가 1이면 active 클래스를 부여)
    if (page === 1) {
        pageNum += `<span class="review-page active" data-page="1"> 1 </span>`;
    } else {
        pageNum += `<a href="#" class="review-page" data-page="1"> 1 </a>`;
    }

    // 중간 페이지 번호 버튼 생성
    for (let i = startPage; i <= endPage; i++) {
        // 첫 페이지과 마지막 페이지는 이미 추가했으므로 건너뛰기
        if (i === 1 || i === maxPage) continue;
        if (page === i) {
            pageNum += `<span class="review-page active" data-page="${i}"> ${i} </span>`;
        } else {
            pageNum += `<a href="#" class="review-page" data-page="${i}"> ${i} </a>`;
        }
    }

    // 마지막 페이지 항상 추가 (현재 페이지가 마지막이면 active 클래스를 부여)
    if (maxPage > 1) {
        if (page === maxPage) {
            pageNum += `<span class="review-page active" data-page="${maxPage}"> ${maxPage} </span>`;
        } else {
            pageNum += `<a href="#" class="review-page" data-page="${maxPage}"> ${maxPage} </a>`;
        }
    }

    $('#numbering').empty().append(pageNum);

    // 페이지 버튼 클릭 이벤트 처리
    $('#numbering .review-page').off('click').on('click', function (e) {
        e.preventDefault();
        // 숫자 버튼이 클릭된 경우 data-page 값 가져오기
        const selectedPage = parseInt($(this).data('page'));
        pagingList(selectedPage, list);
    });

    // 리뷰가 있을 경우 각 리뷰에 대해 댓글 로드
    if (count > 0) {
        for (let i = startRow; i <= endRow; i++) {
            const review = list[i];
            if (review && review.reviewId) {
                loadComments(review.reviewId);
            }
        }
    }
}

// 댓글 로드 함수
function loadComments(reviewId) {
    $.ajax({
        type: "GET",
        url: `/api/comments/cList`,
        data: {reviewId: reviewId}, // reviewId를 쿼리 파라미터로 전달
        dataType: "json",
        success: (comments) => {
            if (Array.isArray(comments) && comments.length > 0) {
                renderComments(comments, reviewId);
                // 댓글이 존재하면 답글 버튼 숨기기
                $(`.review-card[data-review-id="${reviewId}"] .review-reply-btn`).hide();
            } else if (typeof comments === 'object' && comments !== null) {
                // 서버가 단일 객체를 반환하는 경우 배열로 감싸서 처리
                renderComments([comments], reviewId);
                // 댓글이 존재하면 답글 버튼 숨기기
                $(`.review-card[data-review-id="${reviewId}"] .review-reply-btn`).hide();
            } else {
                // 댓글이 없으면 답글 버튼 표시
                $(`.review-card[data-review-id="${reviewId}"] .review-reply-btn`).show();
            }
        },
        error: (xhr) => {
            console.error("댓글 데이터를 불러오지 못했습니다:", xhr.responseText);
            $(`.review-card[data-review-id="${reviewId}"] .cmtArea`).html('<p>댓글을 불러오는 데 실패했습니다.</p>');
            // 오류 발생 시 답글 버튼 표시 (사용자가 다시 시도할 수 있도록)
            $(`.review-card[data-review-id="${reviewId}"] .review-reply-btn`).show();
        }
    });
}

// 평균 별점 및 별점별 리뷰 개수 계산
function calculateAverageFromReviews() {
    let totalScore = 0;
    let totalCount = 0;

    // 별점별 카운트를 위한 객체 초기화
    let reviewCounts = {0: 0, 1: 0, 2: 0, 3: 0, 4: 0, 5: 0};

    list.forEach(review => {
        let score = parseFloat(review.reviewRating);
        totalScore += score;
        totalCount++;
        // 별점 별로 카운트 증가
        if (score >= 4.5) {
            reviewCounts[5]++;
        } else if (score >= 3.5) {
            reviewCounts[4]++;
        } else if (score >= 2.5) {
            reviewCounts[3]++;
        } else if (score >= 1.5) {
            reviewCounts[2]++;
        } else if (score >= 0.5) {
            reviewCounts[1]++;
        } else {
            reviewCounts[0]++;
        }
    });

    console.log(reviewCounts);  // 여기서 reviewCounts를 확인해봄

    let average = totalCount > 0 ? (totalScore / totalCount).toFixed(1) : '0.0';

    // 평균 점수 업데이트
    const averageScoreElement = document.getElementById('average-score');
    if (averageScoreElement) {
        averageScoreElement.textContent = average;
    } else {
        console.error("average-score 요소를 찾을 수 없습니다.");
    }

    // 평균 별점 변환 및 적용
    const averageStarsElement = document.getElementById('average-stars');
    if (averageStarsElement) {
        averageStarsElement.innerHTML = generateStars(average);
    } else {
        console.error("average-stars 요소를 찾을 수 없습니다.");
    }

    // 별점별 리뷰 개수 및 그래프 업데이트
    updateRatingCounts(reviewCounts);
}

// 별점별 카운트 및 그래프 업데이트
function updateRatingCounts(reviewCounts) {
    const totalReviews = list.length;

    // 1점부터 5점까지 반복 (오름차순 또는 내림차순 상관없음)
    for (let score = 1; score <= 5; score++) {
        const count = reviewCounts[score] || 0;
        const percentage = totalReviews > 0 ? (count / totalReviews * 100) : 0;

        // data-rating 속성을 기준으로 올바른 요소 선택
        const ratingBarElement = document.querySelector(`.scoreboard-rating-bar[data-rating="${score}"]`);
        if (ratingBarElement) {
            // rating-count 업데이트
            const ratingCountElement = ratingBarElement.querySelector('.rating-count');
            if (ratingCountElement) {
                ratingCountElement.textContent = count;
            }
            // 진행률(막대) 업데이트
            const progressBar = ratingBarElement.querySelector('.scoreboard-fill');
            if (progressBar) {
                progressBar.style.width = `${percentage}%`;
            }
        }
    }
}

// 별점 생성 함수
function generateStars(score) {
    let fullStars = Math.floor(score);
    let halfStar = (score - fullStars) >= 0.5 ? '★' : '';
    let emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
    return '★'.repeat(fullStars) + halfStar + '☆'.repeat(emptyStars);
}