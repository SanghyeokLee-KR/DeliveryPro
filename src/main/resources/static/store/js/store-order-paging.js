$(document).ready(function () {

    var preId = $("#preSto-id").val();
    console.log("preSto-id:", preId);

    // 전역 변수: 전체 데이터, 필터링된 데이터, 페이지 사이즈, 현재 페이지
    var allData = [];
    var filteredData = [];
    var pageSize = 10;
    var currentPage = 1;
    var totalPages = 1;

    // 날짜 포맷 함수: "YYYY.MM.DD HH:mm"
    function formatDate(dateStr) {
        if (!dateStr) return "";
        var date = new Date(dateStr);
        var yyyy = date.getFullYear();
        var mm = ("0" + (date.getMonth() + 1)).slice(-2);
        var dd = ("0" + date.getDate()).slice(-2);
        var hh = ("0" + date.getHours()).slice(-2);
        var mi = ("0" + date.getMinutes()).slice(-2);
        return yyyy + "." + mm + "." + dd + " " + hh + ":" + mi;
    }

    // 테이블에 데이터를 렌더링하는 함수 (페이징 적용)
    function renderTable(page) {
        currentPage = page;
        var tbody = $("#deliveryDataBody");
        tbody.empty();

        // 페이지 범위에 해당하는 데이터 추출
        var start = (currentPage - 1) * pageSize;
        var end = start + pageSize;
        var pageData = filteredData.slice(start, end);

        if (pageData.length === 0) {
            tbody.append("<tr><td colspan='4'>데이터가 없습니다.</td></tr>");
        } else {
            $.each(pageData, function (index, order) {
                var processedTime = order.callTime ? order.callTime : order.createdAt;
                processedTime = formatDate(processedTime);
                var row = "<tr>" +
                    "<td>" + order.deliveryId + "</td>" +
                    "<td>" + processedTime + "</td>" +
                    "<td>" + order.deliveryType + "</td>" +
                    "<td>" + order.deliveryStatus + "</td>" +
                    "</tr>";
                tbody.append(row);
            });
        }
    }

    // 페이지네이션 컨트롤을 생성하는 함수 (1 ~ totalPages)
    function renderPagination() {
        var paginationDiv = $("#pagination");
        paginationDiv.empty();
        // 총 페이지 수 계산
        totalPages = Math.ceil(filteredData.length / pageSize);
        // 만약 총 페이지 수가 0이면 최소 1페이지로 간주
        totalPages = totalPages > 0 ? totalPages : 1;

        for (var i = 1; i <= totalPages && i <= 10; i++) { // 1~10페이지까지만 보여줌
            var btn = $("<button>")
                .text(i)
                .css({
                    "margin": "0 5px",
                    "padding": "5px 10px",
                    "background-color": "#368802",  // 배경색 변경
                    "color": "white",                // 글자색 변경
                    "border-radius": "5px",          // 모서리 둥글게
                    "border": "1px solid #368802"    // 테두리 색상도 배경색과 동일하게
                });
            // 현재 페이지 강조 처리
            if (i === currentPage) {
                btn.css("font-weight", "bold");
            }
            // 클릭 이벤트: 페이지 변경
            btn.on("click", (function (page) {
                return function () {
                    renderTable(page);
                    renderPagination();
                };
            })(i));
            paginationDiv.append(btn);
        }
    }

    // 필터링 적용 함수
    function applyFilter() {
        var selectedType = $("#deliveryType").val(); // select 값 가져오기
        if (selectedType === "all") {
            filteredData = allData.slice(); // 전체 데이터 표시
        } else {
            filteredData = allData.filter(function (item) {
                return item.deliveryType === selectedType;
            });
        }
        // 새 필터 적용 시 1페이지로 초기화
        renderTable(1);
        renderPagination();
    }

    // AJAX 호출하여 데이터를 가져오기
    $.ajax({
        url: '/api/delivery/' + preId,
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            console.log("받은 전체 데이터:", data);
            allData = data; // 전역 변수에 저장
            // 초기에 전체 데이터로 필터링 (전체)
            applyFilter();
        },
        error: function (xhr, status, error) {
            console.error("데이터 불러오기 실패:", error);
        }
    });

    // 필터 변경 시 이벤트 처리 (select 요소)
    $("#deliveryType").on("change", function () {
        applyFilter(); // 필터링 적용
    });
});
