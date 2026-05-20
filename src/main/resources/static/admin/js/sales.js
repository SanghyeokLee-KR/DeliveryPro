$(document).ready(function () {
    // 웹 페이지가 로드되자마자 자동으로 AJAX 요청을 보냄
    $.ajax({
        url: '/salesChart',  // 요청을 보낼 URL
        method: 'POST',  // POST 방식으로 요청
        dataType: 'json',  // 서버 응답이 JSON 형식일 경우
        success: function (response) {
            console.log('일별 매출 현황:', response);  // 응답 데이터 확인
            // 응답 데이터를 이용해 차트 그리기
            salesChart(response);
        },
        error: function (xhr, status, error) {
            // 요청 실패 시 처리
            console.error('AJAX 요청 실패:', status, error);
        }
    });
});

function salesChart(data) {
    // 날짜와 매출을 각각 추출하여 배열로 저장 (reverse 제거)
    const labels = Object.keys(data);  // 날짜 배열 그대로 사용
    const sales = Object.values(data);  // 매출 배열 그대로 사용

    // 차트 그리기
    const ctx = document.getElementById('salesChart').getContext('2d');

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,  // 그대로 사용된 날짜 배열
            datasets: [{
                label: '일별 매출액',
                data: sales,  // 그대로 사용된 매출 배열
                borderColor: 'rgba(75, 192, 192, 1)',
                fill: false,
                tension: 0.1  // 매끄러운 선 그리기
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    type: 'category',  // 'category' 타입을 사용하여 날짜를 표시
                    title: {
                        display: true,
                    },
                    ticks: {
                        autoSkip: true,
                        maxTicksLimit: 10 // 최대 표시할 날짜 수
                    }
                },
                y: {
                    title: {
                        display: true,
                    },
                    beginAtZero: true  // y축이 0에서 시작하도록 설정
                }
            }
        }
    });
}


$(document).ready(function () {
    // 웹 페이지가 로드되자마자 자동으로 AJAX 요청을 보냄
    $.ajax({
        url: '/topSellingStore',  // 요청을 보낼 URL
        method: 'POST',  // POST 방식으로 요청
        dataType: 'json',  // 서버 응답이 JSON 형식일 경우
        success: function (response) {
            console.log('베스트 매출 매장:', response);  // 응답 데이터 확인
            bestStore(response);
        },
        error: function (xhr, status, error) {
            // 요청 실패 시 처리
            console.error('AJAX 요청 실패:', status, error);
        }
    });
});


function bestStore(result) {
    let output = "";

    // 객체의 키-값을 배열로 변환 후, 매출 금액을 기준으로 내림차순 정렬
    let sortedStores = Object.entries(result)
        .sort((a, b) => parseFloat(b[0]) - parseFloat(a[0])); // 매출 금액 기준 정렬

    // 순위 변수 추가
    let rank = 1;

    // 정렬된 데이터를 순회하며 HTML 생성
    for (let [sales, store] of sortedStores) {
        let formattedSales = parseInt(sales).toLocaleString();
        let salesClass = parseInt(sales) >= 1000000 ? 'large-sales' : '';

        output += `
            <div class="admin-best-store">
                <span class="best-store-rank">${rank}</span> <!-- 순위 추가 -->
                <img src="/store-img/store-main-img/${store.preStoPhoto}" />
                <div class="best-store-title">
                    <div>
                        <p class="best-store-name">${store.preStoName || '정보 없음'}</p>
                        <p class="best-store-memId">ID · ${store.preStoPreMemId}</p>
                    </div>
                    <p class="best-store-total-amount ${salesClass}">${formattedSales} 원</p> 
                </div>
            </div>
        `;

        rank++; // 다음 순위를 위해 증가
    }

    // 결과를 HTML 페이지에 삽입
    $('#best-store-list').html(output);
}



$(document).ready(function () {
    $(".admin-sales-amount").each(function () {
        let $this = $(this);
        let finalValue = parseInt($this.attr("data-amount")) || 0; // data-amount 값 가져오기
        let duration = 1500; // 애니메이션 지속 시간 (1.5초)

        $({ count: 0 }).animate(
            { count: finalValue },
            {
                duration: duration,
                easing: "swing", // 부드러운 증가 효과
                step: function () {
                    $this.text(Math.floor(this.count).toLocaleString() + " 원"); // 숫자 형식 적용
                },
                complete: function () {
                    $this.text(finalValue.toLocaleString() + " 원"); // 최종 값 설정
                }
            }
        );
    });
});

