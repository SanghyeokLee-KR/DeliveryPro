$(document).ready(function () {
    // 클릭 이벤트 핸들러
    $('#sales').on('click', function () {
        // AJAX 요청
        $.ajax({
            url: '/store/storeSalesData',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('매출 데이터 :', response);

                // 매출 데이터 애니메이션 효과 적용하여 출력
                animateSalesData("todayStoreAmount", response.todaySales);
                animateSalesData("weekStoreAmount", response.weekSales);
                animateSalesData("monthStoreAmount", response.monthSales);
                animateSalesData("totalStoreAmount", response.totalSales);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });

        $.ajax({
            url: '/store/storeMemBirthSalesData',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('받아온 데이터:', response);

                // 남녀 성비 데이터 가져오기
                let genderData = response.genderRatio;
                let maleCount = genderData.male;
                let femaleCount = genderData.female;
                let total = maleCount + femaleCount;

                // 퍼센트 계산 (소수점 2자리)
                let malePercent = total > 0 ? ((maleCount / total) * 100).toFixed(2) : "0.00";
                let femalePercent = total > 0 ? ((femaleCount / total) * 100).toFixed(2) : "0.00";

                // HTML에 표시
                $("#malePercent").text(`${malePercent}%`);
                $("#femalePercent").text(`${femalePercent}%`);

                // 남녀 성비 차트 데이터
                let genderChartData = {
                    datasets: [{
                        data: [maleCount, femaleCount],
                        backgroundColor: ["#a4d6ef", "#fb9d9e"]
                    }]
                };

                // 연령대 차트 데이터
                let ageData = response.ageGroups;
                let ageLabels = Object.keys(ageData);
                let ageValues = Object.values(ageData);
                let ageChartData = {
                    datasets: [{
                        data: ageValues,
                        backgroundColor: ["#fac200", "#002a87", "#59a7e4", "#358700", "#B8E15FFF", "#DD4344FF"]
                    }]
                };

                // 남녀 성비 차트 생성 (memGenderChart에 표시)
                new Chart(document.getElementById('memGenderChart'), {
                    type: 'pie',
                    data: genderChartData,
                    options: {
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });

                // 연령대 차트 생성 (memBirthChart에 표시)
                new Chart(document.getElementById('memBirthChart'), {
                    type: 'pie',
                    data: ageChartData,
                    options: {
                        responsive: true,
                        maintainAspectRatio: false
                    }
                });
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });

        $.ajax({
            url: '/store/storeMenuRank',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('메뉴 매출 랭킹 데이터 :', response);

                storeMenuList(response);
            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
            }
        });

        $.ajax({
            url: '/api/stores/statistics',
            method: 'POST',
            dataType: 'json',
            success: function (response) {
                console.log('통계 데이터:', response);

                // 주간 주문 횟수 선 그래프 그리기
                renderChart(response);

            },
            error: function (xhr, status, error) {
                console.error('AJAX 요청 실패:', status, error);
                alert('통계 데이터를 가져오는 데 실패했습니다.');
            }
        });

        function animateSalesData(elementId, targetValue) {
            let current = 0;
            let increment = Math.ceil(targetValue / 100);
            let interval = setInterval(function () {
                current += increment;
                if (current >= targetValue) {
                    current = targetValue;
                    clearInterval(interval);
                }
                document.getElementById(elementId).textContent = current.toLocaleString();
            }, 10);
        }

        function storeMenuList(response) {
            let output = "";

            // menuRankList 배열을 순회
            let menuRankList = response.menuRankList;  // response에서 menuRankList를 가져옵니다.

            // 배열 내 객체들 정렬 (orderCount 기준 내림차순)
            menuRankList.sort((a, b) => b.orderCount - a.orderCount);

            // 배열 순회하여 HTML 출력
            for (let i = 0; i < menuRankList.length; i++) {
                let menu = menuRankList[i];
                let rank = i + 1;  // 순위는 1부터 시작

                output += `
            <div class="store-sales-bottom-mid-info">
                <!-- 매출 순위 내용 (순위, 메뉴사진, 메뉴명) -->
                <div class="store-sales-bottom-mid-left">
                    <!-- 매출 순위 순위 -->
                    <div class="store-sales-bottom-mid-left-rank">${rank}</div>
                    <!-- 매출 순위 이미지 -->
                    <div class="store-sales-bottom-mid-left-menu-img">
                        <img src="/store-img/store-menu-img/${menu.menuPictureUrl}" alt="대체 이미지"/>
                    </div>
                    <!-- 매출 순위 메뉴명 -->
                    <div class="store-sales-bottom-mid-left-menu-name">${menu.menuName}</div>
                </div>
                <!-- 누적 주문 내용 -->
                <div class="store-sales-bottom-mid-right">
                    <!-- 누적 주문 -->
                    <div class="store-sales-bottom-mid-number-order">${menu.orderCount}회</div>
                </div>
            </div>
        `;
            }

            // HTML을 특정 영역에 삽입 (예: '#menuRankList')
            $('#menuRankList').html(output);
        }

        // 차트를 그리는 함수
        function renderChart(data) {
            const labels = generateDateLabels(); // X축 레이블 생성 (지난 일주일의 날짜)
            const weekOrders = data.weekOrders;     // 지난 일주일 동안의 주문 수

            const ctx = document.getElementById('storeOrderChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',  // 막대 그래프 (Bar Chart)
                data: {
                    labels: labels,  // X축 레이블
                    datasets: [
                        {
                            label: '주문수',  // 두 번째 막대 (이번 주의 주문)
                            data: weekOrders,  // 이번 주 주문 수
                            backgroundColor: 'rgba(89,246,183,0.9)',  // 막대 색상
                            borderColor: 'rgb(89,250,230)',  // 막대 테두리 색상
                            borderWidth: 1,  // 막대 테두리 두께
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
                            grid: {
                                display: false  // X축 눈금선 없애기
                            }
                        },
                        y: {
                            beginAtZero: true,  // Y축 0부터 시작
                            max: Math.max(...weekOrders) + 1,  // Y축 최대값은 데이터의 최대값 + 10
                            grid: {
                                display: false  // Y축 눈금선 없애기
                            }
                        }
                    }
                }
            });
        }
    });
});