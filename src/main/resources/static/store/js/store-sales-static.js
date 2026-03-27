$(document).ready(function () {
    // 클릭 이벤트 핸들러
    $('#SSSbtn').on('click', function () {
        // AJAX 요청
        $.ajax({
            url: '/api/stores/statistics',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('통계 데이터:', response);

                // 주간 주문 횟수 선 그래프 그리기
                renderChart(response);
                // 별점별 리뷰 개수로 막대 그래프 그리기
                renderBarChart(response);

                // starCount 값을 가져와서 소수점 한 자리로 반올림
                const starCount = response.starCount;

                // 소수점 한 자리로 반올림하여 표시
                const roundedStarCount = starCount.toFixed(1);

                // 별점 표시 (0 ~ 5 범위)
                let stars = "☆☆☆☆☆"; // 기본 별점
                if (roundedStarCount >= 1) stars = "★☆☆☆☆";


                if (roundedStarCount >= 2) stars = "★★☆☆☆";
                if (roundedStarCount >= 3) stars = "★★★☆☆";
                if (roundedStarCount >= 4) stars = "★★★★☆";
                if (roundedStarCount >= 5) stars = "★★★★★";

                // 해당 p 태그에 별점과 값을 삽입
                $('.SSSstars').text(stars);  // 별점 표시
                $('.SSSstars').next().text(roundedStarCount + " / 5");
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
                alert('통계 데이터를 가져오는 데 실패했습니다.');
            }
        });
    });
});



// 차트를 그리는 함수
function renderChart(data) {
    const labels = generateDateLabels(); // X축 레이블 생성 (지난 일주일의 날짜)
    const todayOrders = data.todayOrders[0]; // 오늘의 주문 수 (단일 값)
    const weekOrders = data.weekOrders;     // 지난 일주일 동안의 주문 수

    // 오늘의 주문 수를 weekOrders 크기에 맞게 배열로 변환
    const todayOrdersData = new Array(weekOrders.length).fill(todayOrders);

    const ctx = document.getElementById('orderChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',  // 선 그래프 (Line Chart)
        data: {
            labels: labels,  // X축 레이블
            datasets: [
                {
                    label: '오늘 주문수',  // 첫 번째 선 (오늘의 주문)
                    data: todayOrdersData,  // 오늘의 주문 수 (일주일 간 동일한 값으로 표시)
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',  // 선 아래의 색상
                    borderColor: 'rgba(75, 192, 192, 1)',  // 선의 색상
                    borderWidth: 2,  // 선의 두께
                    fill: true,  // 선 아래를 채울지 여부
                    tension: 0.4  // 선의 부드러움
                },
                {
                    label: '이번주 주문수',  // 두 번째 선 (이번 주의 주문)
                    data: weekOrders,  // 이번 주 주문 수
                    backgroundColor: 'rgba(153, 102, 255, 0.2)',  // 선 아래의 색상
                    borderColor: 'rgba(153, 102, 255, 1)',  // 선의 색상
                    borderWidth: 2,  // 선의 두께
                    fill: true,  // 선 아래를 채울지 여부
                    tension: 0.4  // 선의 부드러움
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                },
            },
            scales: {
                x: {
                    type: 'category',  // 날짜 기반 X축
                    labels: labels,    // 날짜를 X축 레이블로 사용
                },
                y: {
                    beginAtZero: true,  // Y축 0부터 시작
                    max: Math.max(...weekOrders) + 9,  // Y축 최대값은 데이터의 최대값 + 10
                }
            }
        }
    });
}

// 날짜 레이블 생성 (일주일간의 날짜)
function generateDateLabels() {
    const labels = [];
    const today = new Date();

    // 지난 일주일 날짜를 생성
    for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        labels.push(`${date.getMonth() + 1}/${date.getDate()}`);  // 월/일 형식으로 표시
    }
    return labels;
}


// 막대 그래프를 그리는 함수
function renderBarChart(data) {
    const labels = ['1 Star', '2 Star', '3 Star', '4 Star', '5 Star'];  // 별점 레이블 (1~5)
    const starRatings = data.starRatings;  // 별점별 리뷰 개수

    // Y축 최대값을 데이터의 최대값 + 2로 설정
    const maxStarRating = Math.max(...starRatings); // 데이터의 최대값
    const yAxisMax = maxStarRating + 2; // 최대값 + 2

    const ctx = document.getElementById('starRatingChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',  // 막대 그래프 (Bar Chart)
        data: {
            labels: labels,  // X축 레이블 (별점 1~5)
            datasets: [{
                label: '리뷰별 별점 개수',  // 데이터 설명
                data: starRatings,  // 별점별 리뷰 개수
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',  // 1 Star - 빨간색
                    'rgba(54, 162, 235, 0.2)',  // 2 Star - 파란색
                    'rgba(255, 206, 86, 0.2)',  // 3 Star - 노란색
                    'rgba(75, 192, 192, 0.2)',  // 4 Star - 청록색
                    'rgba(153, 102, 255, 0.2)'   // 5 Star - 보라색
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',  // 1 Star - 빨간색
                    'rgba(54, 162, 235, 1)',  // 2 Star - 파란색
                    'rgba(255, 206, 86, 1)',  // 3 Star - 노란색
                    'rgba(75, 192, 192, 1)',  // 4 Star - 청록색
                    'rgba(153, 102, 255, 1)'   // 5 Star - 보라색
                ],
                borderWidth: 1,  // 테두리 두께
                datalabels: {
                    anchor: 'end',
                    align: 'top',
                    font: {
                        weight: 'bold'
                    },
                    formatter: function(value, context) {
                        if (value === maxStarRating) { // 최고 평점인 경우
                            return value; // 값 표시
                        }
                        return ''; // 나머지 값은 표시하지 않음
                    }
                }
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                datalabels: {
                    display: true,
                    color: 'black',  // 데이터 레이블 색상
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                }
            },
            scales: {
                x: {
                    beginAtZero: true,  // X축 0부터 시작
                    title: {
                        display: true,
                        text: 'Star Rating'  // X축 제목 (별점)
                    }
                },
                y: {
                    beginAtZero: true,  // Y축 0부터 시작
                    max: yAxisMax,  // Y축 최대값을 maxStarRating + 5로 설정
                    title: {
                        display: true,
                        text: 'Number of Reviews'  // Y축 제목 (리뷰 수)
                    },
                    ticks: {
                        stepSize: 1,  // Y축 눈금은 정수로 표시
                    }
                }
            }
        }
    });
}



