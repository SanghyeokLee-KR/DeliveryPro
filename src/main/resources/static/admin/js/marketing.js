$(document).ready(function () {
    function fetchGenderRatio() {
        $.ajax({
            url: '/genderRatio',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('남녀 비율 통계:', response);
                MFChart(response);
                ratioNumber(response);

                updateMaleFill(response);
                updateFemaleFill(response);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });
    }

    function fetchBirthCategoryRatio() {
        $.ajax({
            url: '/birthCategoryRatio',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('생년월일별 비율 통계:', response);
                drawChart(response);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });
    }

    function fetchTopOrderMemList() {
        $.ajax({
            url: '/topOrderMemList',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('주문 많이 한 사람:', response);
                topOrderMemList(response);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });
    }

    function rewardGradeData() {
        $.ajax({
            url: '/rewardGradeData',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('등급표 :', response);
                rewardGradeFun(response);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });
    }

    // 최초 실행
    fetchGenderRatio();
    fetchBirthCategoryRatio();
    fetchTopOrderMemList();
    rewardGradeData();
});

// ratioNumber
function ratioNumber(data){
    // 예를 들어, data가 아래와 같은 구조라면
    // { maleRatio: 60, femaleRatio: 40 }

    // maleRatio와 femaleRatio 값 추출
    let maleRatio = data.maleRatio.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let femaleRatio = data.femaleRatio.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let orderMaleRatio = data.orderMaleRatio.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let orderFemaleRatio = data.orderFemaleRatio.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let localLogin = data.localLogin.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let naverLogin = data.naverLogin.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let kakaoLogin = data.kakaoLogin.toFixed(2);  // 소수점 두 번째 자리까지 반올림
    let googleLogin = data.googleLogin.toFixed(2);  // 소수점 두 번째 자리까지 반올림

    // #num1부터 #num8까지 각각의 ID에 소수점 2자리로 포맷된 값 출력
    $('#num1').text(maleRatio + '%');  // 남자 비율 출력
    $('#num2').text(femaleRatio + '%');  // 여자 비율 출력
    $('#num3').text(orderMaleRatio + '%');  // 주문한 남성 비율 출력
    $('#num4').text(orderFemaleRatio + '%');  // 주문한 여성 비율 출력
    $('#num5').text(localLogin + '%');  // LOCAL 로그인 비율 출력
    $('#num6').text(naverLogin + '%');  // 네이버 로그인 비율 출력
    $('#num7').text(kakaoLogin + '%');  // 카카오 로그인 비율 출력
    $('#num8').text(googleLogin + '%');  // 구글 로그인 비율 출력
}

// 많이 주문한 회원
function topOrderMemList(data) {
    let output = "";

    // 배열 데이터 순회하며 HTML 생성
    data.forEach((member, index) => {
        // index는 0부터 시작하므로, 1부터 시작하도록 index + 1을 사용
        output += `
            <div class="bottom-right-1">
                <div class="max-rank">
                  <p>${index + 1}</p>  <!-- 순위가 1부터 시작 -->
                </div>
                <div class="max-img">
                  <img src="/admin/img/dashboard/회원.png" alt="회원">
                </div>
                <div class ="max-name">
                  <p>이름: ${member.name}</p>
                  <p>아이디: ${member.id}</p>
                </div>
                <div class="max-order">
                  <p>총 주문 수: ${member.orderCount}</p>
                </div>
            </div>
        `;
    });

    // 결과를 HTML 페이지에 삽입 (예: #orderList라는 ID의 요소에 출력)
    $('#top-order-list').html(output);
}

// 나이대별 주문 차트
function drawChart(data) {
    const ageGroups = ["10대 이하", "20대", "30대", "40대", "50대", "60대 이상"];
    const allCategories = ["중식", "한식", "패스트푸드", "치킨", "피자", "양식", "일식", "분식", "족발·보쌈", "도시락", "샐러드", "카페·디저트"];
    const categories = new Set(allCategories); // 고정된 음식 카테고리 목록
    const ageData = {}; // 나이대별 카테고리 주문 건수 저장

    let maxOrderCount = 0; // 최대 주문 건수 추적

    // 데이터 정리: 카테고리별 주문 건수 수집
    for (let age in data) {
        ageData[age] = {};
        for (let category in data[age]) {
            categories.add(category); // 실제 데이터에 있는 카테고리도 추가
            ageData[age][category] = data[age][category];

            // 최대 주문 건수 갱신
            if (data[age][category] > maxOrderCount) {
                maxOrderCount = data[age][category];
            }
        }
    }

    const sortedCategories = Array.from(categories); // X축: 모든 음식 카테고리
    const datasets = ageGroups.map((age, index) => ({
        label: age, // 선 그래프의 이름 = 나이대
        data: sortedCategories.map(category => ageData[age]?.[category] || 0), // 없는 데이터는 0 처리
        borderColor: `hsl(${index * 60}, 70%, 50%)`, // 색상 다르게 설정
        backgroundColor: "transparent",
        borderWidth: 2,
        tension: 0.3 // 곡선 부드럽게
    }));

    const ctx = document.getElementById('birthCategoryChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: sortedCategories, // X축: 모든 음식 카테고리
            datasets: datasets // Y축: 나이대별 주문 건수
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top'
                },
                tooltip: {
                    callbacks: {
                        label: function (tooltipItem) {
                            return `${tooltipItem.dataset.label}: ${tooltipItem.raw}건`;
                        }
                    }
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                    },
                    grid: {
                        display: false // x축 눈금선 숨기기
                    }
                },
                y: {
                    title: {
                        display: true,
                    },
                    beginAtZero: true,
                    suggestedMax: maxOrderCount + 1
                }
            }
        }
    });
}

// 남/여 비율과 로그인 타입
function MFChart(data) {
    // 받아온 데이터
    var femaleRatio = data.femaleRatio;  // 여성 비율
    var maleRatio = data.maleRatio;  // 남성 비율
    var orderFemaleRatio = data.orderFemaleRatio;  // 주문 여성 비율
    var orderMaleRatio = data.orderMaleRatio;  // 주문 남성 비율
    var localLogin = data.localLogin;  // 로컬 로그인
    var naverLogin = data.naverLogin;  // 네이버 로그인
    var kakaoLogin = data.kakaoLogin;  // 카카오 로그인
    var googleLogin = data.googleLogin;  // 구글 로그인



    // genderRatio_2 차트 (주문 성비)
    var ctx2 = document.getElementById('genderRatio_2').getContext('2d');
    new Chart(ctx2, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [orderMaleRatio, orderFemaleRatio], // 주문 성비
                backgroundColor: ['#3498db', '#ff69b4'],  // 하늘색, 핑크색
                hoverBackgroundColor: ['#2980b9', '#e75480'],
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                tooltip: {
                    callbacks: {
                        label: function (tooltipItem) {
                            return tooltipItem.raw.toFixed(2) + '%';
                        }
                    }
                }
            },
            cutout: '50%',
        }
    });

    // genderRatio_3 차트 (로그인 비율)
    var ctx3 = document.getElementById('genderRatio_3').getContext('2d');
    new Chart(ctx3, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [localLogin, naverLogin, kakaoLogin, googleLogin],
                backgroundColor: ['#dc3545', '#28a745', '#ffc107', '#007bff'],
                hoverBackgroundColor: ['#c82333', '#218838', '#e0a800', '#0069d9'],
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                tooltip: {
                    callbacks: {
                        label: function (tooltipItem) {
                            return tooltipItem.raw.toFixed(2) + '%';
                        }
                    }
                }
            },
            cutout: '50%',
        }
    });
}

// 남성 비율 svg에 적용
function updateMaleFill(data) {
    const maleFill = document.getElementById("male-fill");
    const totalHeight = 78.38;
    const percent = parseFloat(data.maleRatio);
    const fillHeight = totalHeight * (percent / 100);
    const newY = totalHeight - fillHeight;
    maleFill.setAttribute("y", newY);
    maleFill.setAttribute("height", fillHeight);
}

// 여성 비율 svg에 적용
function updateFemaleFill(data) {
    const femaleFill = document.getElementById("female-fill");
    const totalHeight = 78.38;
    const percent = parseFloat(data.femaleRatio);
    const fillHeight = totalHeight * (percent / 100);
    const newY = totalHeight - fillHeight; // 남성 채움을 고려하여 y값 조정
    femaleFill.setAttribute("y", newY);
    femaleFill.setAttribute("height", fillHeight);
}

// 등급별 차트
function rewardGradeFun(data) {
    const ctx = document.getElementById('gradeChart').getContext('2d');

    // 순서를 지정합니다.
    const order = ['welcome', 'family', 'vip', 'vvip'];

    // 데이터 순서대로 정렬
    const sortedData = order.map(grade => data[grade]);

    // sortedData는 각 등급별 회원 수 (sorted: welcome, family, vip, vvip 순)
    const counts = sortedData;

    // 순서대로 정렬된 등급
    const labels = order;

    // 각 등급에 맞는 색상을 지정합니다.
    const colors = {
        welcome: 'rgb(84,177,228)',  // 하늘색
        family: 'rgb(109,187,78)',    // 초록색
        vip: 'rgb(247,147,4)',        // 주황색
        vvip: 'rgb(187,0,0)'          // 빨간색
    };

    // 각 등급에 맞는 색상 배열을 생성
    const backgroundColors = labels.map(label => colors[label]);

    // 각 등급에 맞는 이미지 경로 지정 (이미지 URL)
    const imagePaths = {
        welcome: '/member/img/grade/등급-40.svg',
        family: '/member/img/grade/등급-39.svg',
        vip: '/member/img/grade/등급-38.svg',
        vvip: '/member/img/grade/등급-37.svg',
    };

    new Chart(ctx, {
        type: 'bar',  // 막대 그래프
        data: {
            labels: labels,  // X축에 표시될 등급
            datasets: [{
                label: '회원 수',  // 그래프의 레이블
                data: counts,  // 각 등급의 회원 수
                backgroundColor: backgroundColors,  // 동적으로 설정된 배경색
            }]
        },
        options: {
            responsive: true,
            indexAxis: 'y',  // 가로 막대 그래프
            scales: {
                x: {
                    beginAtZero: true,  // X축이 0부터 시작
                    grid: {
                        display: false  // X축 눈금선 숨기기
                    }
                },
                y: {
                    beginAtZero: true,  // Y축이 0부터 시작
                    grid: {
                        display: false  // Y축 눈금선 숨기기
                    }
                }
            },
            plugins: {
                legend: {
                    display: false  // 범례 표시 안 함
                },
                tooltip: {
                    callbacks: {
                        label: function(tooltipItem) {
                            return tooltipItem.label + ': ' + tooltipItem.raw + '명';  // 툴팁에 회원 수 표시
                        }
                    }
                }
            },
            // Custom plugin to add image to labels
            layout: {
                padding: {
                    left: 50  // 텍스트와 이미지를 충분히 배치할 여유 공간 설정
                }
            },
            responsive: true,
            animation: {
                onComplete: function() {
                    const chart = this;
                    const ctx = chart.ctx;
                    const xScale = chart.scales.x;
                    const yScale = chart.scales.y;

                    // 각 레이블에 이미지를 그리기
                    chart.data.labels.forEach((label, index) => {
                        const image = new Image();
                        image.src = imagePaths[label];  // 이미지 경로를 가져옵니다.

                        image.onload = function() {
                            const x = xScale.getPixelForValue(0) - 90;  // 이미지가 텍스트 왼쪽에 위치하도록 설정
                            const y = yScale.getPixelForValue(index) - yScale.height / chart.data.labels.length / 2;  // 각 레벨에 맞는 Y 위치 설정
                            ctx.drawImage(image, x, y, 25, 25);  // 이미지 크기와 위치 지정
                        };
                    });
                }
            }
        }
    });
}
