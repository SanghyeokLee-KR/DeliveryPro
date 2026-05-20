// 계좌 번호 입력 / 출력 관련 JS
$(document).ready(function() {
    // 계좌 등록 폼 제출 시 유효성 검사
    $("form").on("submit", function(event) {
        var riderBankName = $("#bank").val(); // 은행명
        var riderAccountNumber = $("#account-number").val(); // 계좌번호

        // 은행명과 계좌번호가 비어있다면
        if (!riderBankName || !riderAccountNumber) {
            alert("입력이 올바르지 않습니다. 은행명과 계좌번호를 모두 입력해주세요.");
            event.preventDefault(); // 폼 제출 막기
            return false; // 더 이상 진행하지 않음
        }
    });

    $.ajax({
        type: 'POST',
        url: '/getRiderAccountList',
        dataType: "json",
        success: function(result) {
            // 받은 데이터를 HTML에 출력
            RiderAccList(result);
            console.log(result);
        },
        error: function(xhr, status, error) {
            console.error("AJAX 요청 실패:", error);
        }
    });

    // 수정/삭제 클릭 시 모달 열기
    $(document).on('click', '#account-crystal', function() {
        $("#crystal-modal").show(); // 모달 열기
    });

    // 모달 닫기 버튼 클릭 시 모달 닫기
    $(document).on('click', '#close-modal', function() {
        $("#crystal-modal").hide(); // 모달 닫기
    });

    // 모달 외부 클릭 시 닫기
    $(window).click(function(event) {
        if ($(event.target).is("#crystal-modal")) {
            $("#crystal-modal").hide(); // 모달 닫기
        }
    });
});

// 계좌번호 마스킹 함수
function maskAccountNumber(accountNumber) {
    if (accountNumber.length <= 4) return accountNumber;  // 계좌번호가 너무 짧으면 그대로 반환
    let firstPart = accountNumber.slice(0, 2);   // 앞 2자리
    let lastPart = accountNumber.slice(-1);      // 맨 끝 1자리
    let maskedPart = '*'.repeat(accountNumber.length - 3);  // 중간 부분을 '*'로 마스킹

    return firstPart + maskedPart + lastPart;  // 앞 2자리 + 마스킹 + 끝 1자리
}

// 예금주 마스킹 함수
function maskAccountHolder(holderName) {
    if (holderName.length <= 1) return holderName;  // 예금주 이름이 한 글자 이하일 경우 그대로 반환
    let firstChar = holderName.charAt(0);  // 첫 번째 글자
    let secondChar = '*';  // 두 번째 글자는 '*'로 마스킹
    let remainingChars = holderName.slice(2);  // 나머지 부분은 그대로

    return firstChar + secondChar + remainingChars;  // 첫 번째 글자 + '*' + 나머지
}

// 계좌 번호 리스트를 출력
function RiderAccList(result) {
    let output = "";

    // 배열 순회 시 for...of 사용
    for (let i of result) {
        let maskedAccountNumber = maskAccountNumber(i.riderAccountNumber);
        let maskedAccountHolder = maskAccountHolder(i.riderAccountHolder);

        const accountId = i.riderAid;  // i에서 riderAid 값을 가져옵니다.

        output += `
                        <div class="rider-account-account-details">
                            <div><strong>은행명 : </strong>${i.riderBankName}</div>
                            <div style="width: 200px;"><strong>계좌번호 : </strong>${maskedAccountNumber}</div>
                            <div><strong>예금주 : </strong>${maskedAccountHolder}</div>
                            <div style="width: 100px;">
                                <a id="account-crystal" class="crystal-a" data-account-id="${accountId}">수정</a>
                                <span>/</span>
                                <a class="crystal-d" data-account-id="${accountId}">삭제</a>  <!-- 삭제 버튼에 data-account-id 추가 -->
                            </div>
                        </div>
                    `;
    }
    $("#riderAccountList").html(output).show();

    // 삭제 버튼 클릭 이벤트 처리
    $(document).on("click", ".crystal-d", function() {
        const accountId = $(this).data('account-id');

        // 사용자가 확인을 누르면 삭제를 진행
        if (confirm("정말 삭제하시겠습니까?")) {
            $.ajax({
                url: '/riderAccountDelete',
                type: 'POST',
                data: { accountId: accountId },
                dataType: "text",
                success: function(response) {
                    alert('계좌 삭제 ' + response);
                    location.reload();
                },
                error: function(error) {
                    // AJAX 요청 실패 시
                    alert('삭제 실패: 서버 오류');
                }
            });
        } else {
            console.log("삭제가 취소되었습니다.");
        }
    });
}

// 입력 텍스트 길이 실시간으로 보기
function updateCharacterCount() {
    const inputField = document.getElementById('account-number');
    const charCount = document.getElementById('char-count');
    const currentLength = inputField.value.length;

    // 글자 수 업데이트
    if (currentLength <= 16) {
        charCount.textContent = `${currentLength}/14`; // n글자 이내일 때만 업데이트
    }

    // 20글자 초과 시 alert 띄우기
    if (currentLength > 14) {
        alert('입력 최대치를 초과했습니다.');
        inputField.value = inputField.value.substring(0, 14); // n글자 이상 입력된 경우, n글자로 자르기
        charCount.textContent = `14/14`; // n글자 넘어가면 카운트는 n/n으로 고정
    }
}

// 계좌번호에서 '-' 기호 제거하는 함수
function cleanAccountNumber() {
    var accountNumberInput = document.getElementById('account-number');
    // 계좌번호에서 '-' 문자를 제거
    var cleanedAccountNumber = accountNumberInput.value.replace(/-/g, '');
    // 변경된 값으로 input에 다시 설정
    accountNumberInput.value = cleanedAccountNumber;
}