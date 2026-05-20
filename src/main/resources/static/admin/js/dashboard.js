$(document).ready(function () {
    // 웹 페이지가 로드되자마자 자동으로 AJAX 요청을 보냄
    $.ajax({
        url: '/dashboardChart',  // 요청을 보낼 URL
        method: 'POST',  // POST 방식으로 요청
        dataType: 'json',  // 서버 응답이 JSON 형식일 경우
        success: function (response) {
            console.log('카테고리별 주문 수:', response);  // 응답 데이터 확인
            // 응답 데이터를 이용해 카테고리 차트 그리기
            categoryChart(response);
        },
        error: function (xhr, status, error) {
            // 요청 실패 시 처리
            console.error('AJAX 요청 실패:', status, error);
        }
    });
});

// 차트를 그리는 함수
function categoryChart(data) {
    const ctx = document.getElementById('categoryChart').getContext('2d');
    if (!ctx) {
        console.error('차트를 표시할 canvas 엘리먼트를 찾을 수 없습니다.');
        return;
    }

    const categories = ['중식', '한식', '패스트푸드', '치킨', '피자', '양식', '일식', '분식', '족발·보쌈', '도시락', '샐러드', '카페·디저트'];
    let categoryCounts = categories.map(() => 0);

    // 각 카테고리별 색상 설정
    const categoryColors = [
        'rgba(255, 99, 132, 0.2)', // 중식 - 빨간색
        'rgba(54, 162, 235, 0.2)', // 한식 - 파란색
        'rgba(255, 159, 64, 0.2)', // 패스트푸드 - 주황색
        'rgba(255, 205, 86, 0.2)', // 치킨 - 노란색
        'rgba(75, 192, 192, 0.2)', // 피자 - 청록색
        'rgba(153, 102, 255, 0.2)', // 양식 - 보라색
        'rgba(255, 159, 223, 0.2)', // 일식 - 핑크색
        'rgba(255, 99, 71, 0.2)', // 분식 - 빨간색 (진한)
        'rgba(144, 238, 144, 0.2)', // 족발·보쌈 - 연두색
        'rgba(240, 128, 128, 0.2)', // 도시락 - 연한 빨간색
        'rgba(255, 182, 193, 0.2)', // 샐러드 - 연분홍색
        'rgba(255, 255, 0, 0.2)' // 카페·디저트 - 노란색
    ];

    Object.keys(data).forEach(category => {
        const index = categories.indexOf(category);
        if (index !== -1) {
            categoryCounts[index] = data[category];
        }
    });

    // 카테고리별 색상과 값을 매칭하여 데이터셋 생성
    const backgroundColors = categoryCounts.map((_, index) => categoryColors[index]);
    const borderColors = backgroundColors.map(color => color.replace('0.2', '1')); // 투명도를 1로 바꿔서 선 색상으로 사용

    console.log('카테고리별 주문 수:', categoryCounts);

    // 차트 생성
    new Chart(ctx, {
        type: 'bar',  // 막대그래프 (Bar Chart)
        data: {
            labels: categories,  // 카테고리 이름을 X축 레이블로 사용
            datasets: [
                {
                    label: '카테고리별 주문 수',  // 첫 번째 데이터셋
                    data: categoryCounts,  // 카테고리별 주문 수
                    backgroundColor: backgroundColors,  // 카테고리별 색상
                    borderColor: borderColors,  // 테두리 색상 (배경 색상의 불투명도 제거)
                    borderWidth: 2,  // 막대의 두께
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    beginAtZero: true,
                    grid: {
                        display: false // X축 눈금선 숨기기
                    }
                },
                y: {
                    beginAtZero: true,  // Y축 0부터 시작
                    max: Math.max(...categoryCounts) + 1,  // Y축 최대값은 데이터의 최대값 + n
                    ticks: {
                        stepSize: 1,  // Y축 눈금 간격을 1로 설정
                        precision: 0,  // 소수점 자리를 0으로 설정
                    },
                    grid: {
                        display: false // Y축 눈금선 숨기기
                    }
                }
            }
        }
    });
}




$("#fetchStatistics").click(function() {
    let date = $("#datePicker").val(); // 사용자가 선택한 날짜
    $.ajax({
        type: "POST",
        url: "/statisticsDateSel",
        contentType: "text",
        data: JSON.stringify({ date: date }), // JSON 형식으로 데이터 전송
        success: function(response) {

        },
        error: function(error) {
            console.log("통계 조회 실패", error);
        }
    });
});


