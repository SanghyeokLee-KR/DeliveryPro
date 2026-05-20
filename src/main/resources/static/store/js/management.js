// 현재 URL을 가져옵니다
const url = window.location.href;

// URL에서 쿼리 파라미터를 제거한 뒤, 마지막 슬래시 뒤의 값을 추출
const pathWithoutParams = url.split('?')[0]; // 쿼리 파라미터 제거
const pathValue = pathWithoutParams.split('/').pop(); // 마지막 슬래시 뒤의 값 추출

console.log(pathValue);

$(document).ready(function() {
    $.ajax({
        type: "POST",
        url: "/store/storeCount",  // 매장 목록을 가져오는 URL
        data: { pathValue: pathValue },
        dataType: "text",
        success: function(result) {
            const storeCount = parseInt(result);  // 문자열을 정수로 변환

            console.log(storeCount)

            // 매장 리스트가 3개 이상일 때 "가게 추가하기" 버튼 숨기기
            if (storeCount >= 3) {
                $("#store-add-btn").hide();
            } else {
                // 매장 리스트가 3개 미만일 때 "가게 추가하기" 버튼 보이기
                $("#store-add-btn").show();
            }
        },
        error: function(e) {
            console.log("매장 목록을 가져오는 데 실패했습니다.", e);
        }
    });
});


// '가게리스트' 버튼을 클릭했을 때
$("#store-list-view").click(function() {
    // AJAX 요청 시작
    $.ajax({
        type: "POST",
        url: "/store/storeListBox",  // 매장 목록을 가져오는 URL
        data: { pathValue: pathValue },
        dataType: "json",
        success: function(result) {
            // 받은 데이터를 StoreList 함수로 전달하여 화면에 출력
            // 결과 값이 비어있는지 확인
            if (!result || result.length === 0) {
                // 가게가 등록되지 않은 경우 alert 띄우기
                alert("가게 등록을 먼저 해주세요.");
            } else {
                // 받은 데이터를 StoreList 함수로 전달하여 화면에 출력
                console.log(result);
                StoreList(result);
            }
        },
        error: function(e) {
            console.log("매장 정보 가져오기 실패", e);
        }
    });
});

function StoreList(result) {
    let output = "";

    // 배열 순회 시 for...of 사용
    for (let i of result) {
        let statusColor = ""; // 상태에 따른 색상을 저장할 변수
        let statusText = i.preStoStatus; // 상태 텍스트

        // preStoStatus에 따라 색상 지정
        switch (statusText) {
            case "승인":
                statusColor = "#00b894"; // 승인일 때 색상
                break;
            case "차단":
                statusColor = "#d30000"; // 차단일 때 색상
                break;
            case "폐점":
                statusColor = "#808080"; // 폐점일 때 색상 (짙은 회색)
                break;
            case "보류":
                statusColor = "#ff4500"; // 보류일 때 색상
                break;
            default:
                statusColor = "black"; // 기본 색상 (정해지지 않은 상태)
        }

        output += `
        <div class="store-list-container">
            <div class="store-list-item">
                <div>
                    <div class="store-list-content">
                        <div class="store-list-img">
                            <img src="/store-img/store-main-img/${i.preStoPhoto}"/>
                        </div>
                        <div class="store-list-info">
                          <div class="store-list-text">
                            <p class="store-list-name">${i.preStoName}</p>
                            <p class="store-list-category">${i.preStoCategory}</p>
                          </div>
                          <div class="store-list-status-div">
                            <p class="store-list-status" style="color: ${statusColor};">${i.preStoStatus}</p>
                          </div>
                        </div>
                    </div>
                </div>
                <button class="store-list-button" id="store-details-${i.preStoId}">선택하기</button>
            </div>
        </div>
        `;
    }

    // AJAX 요청
    $.ajax({
        type: "POST",
        url: "/store/storeCount",  // 매장 목록을 가져오는 URL
        data: { pathValue: pathValue },
        dataType: "text",
        success: function(result) {
            const storeCount = parseInt(result);  // 문자열을 정수로 변환

            // storeCount가 3 미만일 경우 HTML 추가
            if (storeCount < 3) {
                output += `
                           <div class="store-list-add-container-div" id="store-add-button">
                              <!-- 여기에 추가적인 내용 삽입 -->
                              <div class="store-list-plus">+</div>
                              <h3>가게 추가하기</h3>
                              <p>가게 추가는 최대 3개까지 가능합니다.</p>
                           </div>
                        `;
            }

            // 매장 리스트를 storeList 컨테이너에 출력
            $("#storeList").html(output).show();
        },
        error: function(e) {
            console.error("AJAX 요청에 실패했습니다", e);
        }
    });

    // store-list-item 클릭 이벤트 (이벤트 위임 방식으로 바인딩)
    $("#storeList").on("click", ".store-list-button", function(event) {
        const storeId = $(this).attr('id').split('-')[2];
        console.log(storeId);

        // AJAX 요청을 보내서 해당 storeId에 대한 상세 정보를 가져옴
        $.ajax({
            type: "POST",
            url: "/store/getStoreDetails",  // 매장 상세 정보를 가져오는 URL
            data: { storeId: storeId },  // storeId를 controller로 전달
            success: function(result) {
                console.log(result); // 처리된 결과 확인
                location.reload();
            },
            error: function(e) {
                console.log("매장 상세정보 가져오기 실패", e);
            }
        });
    });


    $("#storeList").on("click", ".store-list-add-container-div", function(event) {
        // store-add-button 클릭 시
        $(".add-store-container").show();  // add-store-container 보이기
        $("#storeList").hide();  // storeList 숨기기
    });
}




function checkApprovalStatus(element) {
    var storeStatus = element.getAttribute('data-value');

    if (storeStatus !== "승인") {
        alert("미승인된 매장은 메뉴 등록이 불가능합니다.");
    } else {
        // store-add-menu 클릭 이벤트 (메뉴 추가하기 버튼 클릭 시)
        $(document).on('click', '#add-store-menu', function() {
            // 가게 선택이 되어 있으면 모달 보이게 하기
            $("#menu-add-modal").show();
        });
    }
}

// 모달 닫기 이벤트
$(document).on('click', '#close-modal', function() {
    // 모달 닫기
    $("#menu-add-modal").hide();
});

// 모달 외부 클릭 시 닫기
$(window).click(function(event) {
    if ($(event.target).is("#menu-add-modal")) {
        $("#menu-add-modal").hide();
    }
});

// store-menu-view 클릭 이벤트 (메뉴 보기 버튼 클릭 시)
$(document).on('click', '#store-menu-list', function() {
    // data-value에서 preStoId 값을 가져옴
    const preStoId = $(this).data('value');  // data-value로 preStoId 가져오기

    // store-detail-menu-view 보이게 하기
    $("#store-detail-menu-view").show();

    // AJAX 요청을 통해 매장 메뉴 목록을 가져옴
    $.ajax({
        type: "POST",
        url: "/store/getMenuList",  // 매장 메뉴 목록을 가져오는 URL
        data: { preStoId: preStoId },
        dataType: "json",
        success: function (result) {
            console.log(result); // 처리된 결과 확인

            // result가 배열이고, 배열의 길이가 0일 경우
            if (Array.isArray(result) && result.length === 0) {
                alert("메뉴가 없습니다.");
            } else {
                getStoreMenuList(result); // 받은 데이터로 DOM 업데이트
            }
        },
        error: function (e) {
            console.log("매장 메뉴 정보 가져오기 실패", e);
        }
    });
});


function getStoreMenuList(result) {
    let output = "";

    const preStoId = result[0].preStoId;

    // 검색 및 추가 버튼 추가
    output += getSearchAndAddMenuHtml(preStoId);

    for (let i in result) {
        let menuPopularityText = "";
        let popularityColor = ""; // 인기 여부에 따라 색상을 설정할 변수

        if (result[i].menuPopularity == 0) {
            menuPopularityText = "일반";
            popularityColor = "#00b894"; // 일반일 경우 회색
        } else if (result[i].menuPopularity == 1) {
            menuPopularityText = "인기";
            popularityColor = "#EDD200"; // 인기일 경우 초록색
        }

        const menuId = result[i].menuId;
        const menuPictureUrl = result[i].menuPictureUrl

        // 가격에 세자리마다 쉼표 추가
        let formattedPrice = Number(result[i].menuPrice).toLocaleString();

        // menuStatus에 맞춰 select 옵션의 기본 선택값을 설정
        let selectedStatus = "";
        if (result[i].menuStatus == "판매중") {
            selectedStatus = "판매중";
        } else if (result[i].menuStatus == "품절") {
            selectedStatus = "품절";
        } else if (result[i].menuStatus == "보류") {
            selectedStatus = "보류";
        }

        output += `
        <div class="store-detail-menu-view">
            <div class="menu-list-item">
                <img src="/store-img/store-menu-img/${result[i].menuPictureUrl}" class="menu-list-img"/>
                <div class="menu-list-info">
                    <div style="display: flex; align-items: center;">
                        <p class="menu-list-name">${result[i].menuName}</p>
                        <p class="menu-list-popularity" style="color: ${popularityColor};">${menuPopularityText}</p>
                        <p class="menu-list-mainMenu">【${result[i].menuCategory}】</p>
                    </div>
                    <p class="menu-list-description">${result[i].menuDescription}</p>
                    <p class="menu-list-price">${formattedPrice}원</p>
                </div>
                <div style="margin-left: auto">
                    <div>
                        <a id="menuModify" class="menu-Modi-del" data-menu-id="${menuId}" data-menu-image="${menuPictureUrl}"><span class="menu-list-modi-del">수정</span></a>
                        <a id="menuDelete" class="menu-Modi-del" data-menu-id="${menuId}"><span class="menu-list-modi-del">삭제</span></a>
                    </div>
                    <div class="menu-list-sales-status">
                        <select class="sales-status" data-menu-id="${menuId}">
                            <option value="판매중" ${selectedStatus === "판매중" ? "selected" : ""}>판매중</option>
                            <option value="품절" ${selectedStatus === "품절" ? "selected" : ""}>품절</option>
                            <option value="보류" ${selectedStatus === "보류" ? "selected" : ""}>보류</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>        
        `;
    }

    // 메뉴 목록을 store-menu-view에 추가
    $("#store-detail-menu-view").html(output);

    // '판매중', '품절', '보류' 선택을 변경했을 때의 이벤트 리스너 추가
    $(".sales-status").change(function() {
        const menuId = $(this).data("menu-id");  // 해당 메뉴의 ID
        const newStatus = $(this).val();         // 변경된 상태값

        // AJAX를 사용하여 서버에 상태 변경을 요청
        $.ajax({
            url: "/store/updateMenuStatus",  // 서버의 API URL (적절히 수정)
            type: "POST",
            data: {
                menuId: menuId,
                newStatus: newStatus
            },
            success: function(response) {
                if (response.success) {
                    console.log("상태값 변경 " + response);
                } else {
                    console.log("상태값 변경 " + response);
                }
            },
            error: function(xhr, status, error) {
                console.error("AJAX 오류:", error);
            }
        });
    });
}

// 수정 버튼 클릭 시 모달 띄우기
$(document).on('click', '#menuModify', function() {
    // 해당 메뉴 정보를 가져오기 위해 menuId 사용
    const menuId = $(this).data('menu-id');
    const menuName = $(this).closest('.menu-list-item').find('.menu-list-name').text();
    const menuPrice = $(this).closest('.menu-list-item').find('.menu-list-price').text().replace('원', '').replace(/,/g, '');
    const menuDescription = $(this).closest('.menu-list-item').find('.menu-list-description').text();
    const menuListPopularity = $(this).closest('.menu-list-item').find('.menu-list-popularity').text(); // 이 값이 '0' 또는 '1'일 것으로 예상

    const menuImgUrl = $(this).data('menu-image');

    // 모달에 기존 메뉴 정보 표시
    $('#modiMenuId').val(menuId);  // 메뉴 ID 값을 히든 필드에 추가
    $('#modiMenuName').val(menuName);
    $('#modiMenuPrice').val(menuPrice);
    $('#modiMenuDescription').val(menuDescription);

    // menuListPopularity 값이 문자열로 올 수 있기 때문에 숫자형으로 변환 후 설정
    $('#menuPopular').val(parseInt(menuListPopularity)); // '0' 또는 '1' 값을 int로 변환하여 설정

    // 기존 이미지가 있다면 미리보기와 이미지 이름을 설정
    if (menuImgUrl) {
        $('#menuImgName').text(menuImgUrl);  // 이미지 파일 이름 텍스트로 표시
        $('#menuPictureUrl').val(menuImgUrl);
        $('#menuImagePreview').attr('src', `/store-img/store-menu-img/${menuImgUrl}`);  // 기존 이미지 URL을 미리보기로 설정
    } else {
        $('#menuImgName').text('이미지가 없습니다');  // 이미지가 없을 경우 기본 텍스트 설정
        $('#menuImagePreview').attr('src', '');  // 이미지 미리보기 빈 상태로 설정
    }

    // 모달 표시
    $(".menu-modify-modal").css("display", "block");

    // 모달 닫기
    $(".menu-modify-close").click(function() {
        $(".menu-modify-modal").css("display", "none");
    });
});

// 모달에서 form 제출 시 AJAX 요청 보내기
$('#menu-modify-form').submit(function(e) {
    e.preventDefault(); // 폼 제출 시 페이지 리로드 방지

    // 폼 데이터 수집
    const formData = new FormData(this);  // 'this'는 폼 자체를 참조합니다.

    // AJAX로 폼 데이터 전송
    $.ajax({
        url: '/store/menuModify',  // 서버에 요청을 보낼 URL
        type: 'POST',
        data: formData,  // 수집한 FormData 객체를 데이터로 전송
        contentType: false,  // 파일 업로드와 같은 경우 자동으로 설정해주는 부분
        processData: false,  // 'formData'는 이미 데이터를 담고 있기 때문에 jQuery에서 자동으로 변환하지 않음
        success: function(response) {
            alert('메뉴 ' + response + ' 하였습니다.');
            $(".menu-modify-modal").css("display", "none");
            location.reload(); // 페이지를 새로 고침하여 메뉴 수정 반영
        },
        error: function() {
            alert('서버 오류로 메뉴 수정이 실패했습니다.');
        }
    });
});


$(document).on('click', '#menuDelete', function() {
    // 삭제할 메뉴의 ID 가져오기
    const menuId = $(this).data('menu-id');

    // 삭제 확인 메시지 (경고)
    if (confirm('정말로 이 메뉴를 삭제하시겠습니까?')) {
        // AJAX 요청을 보내서 메뉴 삭제
        $.ajax({
            url: '/store/menuDelete',
            type: 'POST',  // 요청 방식 (POST)
            data: { menuId: menuId },
            success: function(response) {
                $(`#menu-${menuId}`).remove();
                alert('메뉴 ' + response);
                location.reload();
            },
            error: function(error) {
                // AJAX 요청 실패 시
                alert('삭제 실패: 서버 오류');
            }
        });
    }
});


function getSearchAndAddMenuHtml(preStoId) {
    return `
        <div class="menu-list-search-add">
            <div style="display: flex; align-items: center">
                <select id="store-menu-category" style="margin-right: 10px;">
                    <option value="">선택하세요</option>
                    <option value="">메뉴 이름</option>
                    <option value="메인메뉴">메인 메뉴</option>
                    <option value="사이드메뉴">사이드 메뉴</option>
                    <option value="소스">소스</option>
                    <option value="음료">음료</option>
                </select>
                <input type="text" id="menu-list-search" name="menu-list-search" class="menu-list-search" placeholder="메뉴를 검색하세요" />
                <button id="menu-list-search-bt" class="menu-list-search-bt" data-value="${preStoId}">검색</button>
            </div>
            <!-- 수정된 부분: data-value를 이용해 preStoId 전달 -->
            <div id="add-store-menu" class="menu-list-add-bt" data-value="${preStoId}" onclick="openAddMenuModal(this)">
                <p>메뉴 추가하기</p>
            </div>
        </div>
    `;
}

// '메뉴 추가하기' 버튼 클릭 시 처리
function openAddMenuModal(button) {
    // 버튼에서 preStoId 가져오기
    const preStoId = $(button).data('value');

    // 모달을 열거나 메뉴 추가 폼을 표시하는 로직을 추가
    // 예시로, 모달을 여는 코드
    $('#menu-add-modal').show(); // 모달 열기

    // 필요시 preStoId를 폼에 전달하거나 초기화하는 작업 추가
    $('#menu-add-modal').data('preStoId', preStoId); // 예시로 preStoId를 모달에 저장
}


// 검색 버튼 클릭 시 이벤트
$(document).on('click', '#menu-list-search-bt', function() {
    let keyword = $('#menu-list-search').val();
    let category = $('#store-menu-category').val();
    let preStoId = $(this).data('value');

    // AJAX 요청 시작
    $.ajax({
        type: "POST",
        url: "/store/searchMenuList",
        data: {
            "category": category,  // 카테고리 값
            "keyword": keyword,    // 검색어
            "preStoId": preStoId   // 가게 PK 번호
        },
        dataType: "json",
        success: (result) => {
            // result가 null 이거나 빈 데이터일 경우 처리
            if (result == null || result.length === 0) {
                alert('해당 메뉴는 없습니다');
            } else {
                getStoreMenuList(result);
            }
        },
        error: () => {
            alert('searchMenuList 통신 실패');
        }
    });
});


// 이미지 미리보기 함수
function previewImage(event) {
    const file = event.target.files[0];  // 업로드된 파일을 가져옵니다.
    const reader = new FileReader();  // FileReader 객체 생성

    const fileInput = event.target;
    const fileName = fileInput.files.length > 0 ? fileInput.files[0].name : ""; // 첫 번째 파일의 이름을 가져옵니다.
    const fileNameDisplay = document.getElementById("file-name");

    if (fileName) {
        fileNameDisplay.textContent = `선택된 파일: ${fileName}`; // 파일 이름을 표시
    } else {
        fileNameDisplay.textContent = "사업자등록증을 업로드 해주세요"; // 파일이 선택되지 않았을 경우 텍스트 지우기
    }

    reader.onload = function (e) {
        const imagePreview = document.getElementById('imagePreview');
        if (!imagePreview) {
            // 미리보기 이미지를 추가합니다.
            const img = document.createElement('img');
            img.id = 'imagePreview';
            img.src = e.target.result;
            img.alt = 'Uploaded Image';
            img.style.maxWidth = '150px';  // 미리보기 이미지 크기 설정
            img.style.maxHeight = '150px'; // 미리보기 이미지 크기 설정
            document.querySelector('.file-upload').appendChild(img);  // 파일 업로드 div에 이미지 추가
        } else {
            // 이미 미리보기가 있으면 이미지를 업데이트
            imagePreview.src = e.target.result;
        }
    };

    // 파일을 읽어들입니다.
    if (file) {
        reader.readAsDataURL(file);
    }
}

// select 요소에서 선택된 값을 가져오고, 이를 int로 변환
const selectElement = document.getElementById('menuPopular');
selectElement.addEventListener('change', function() {
    const selectedValue = parseInt(selectElement.value);  // 선택된 값을 int로 변환
    console.log(selectedValue); // 변환된 int 값 출력
});


// 모달 열기
function openModal(fieldName) {
    console.log('openModal 실행됨, fieldName:', fieldName); // 디버깅 로그
    const modal = document.getElementById(`modal-${fieldName}`);
    console.log('모달 :', modal); // 모달 객체 확인
    if (modal) {
        modal.style.display = "flex"; // Flexbox로 설정
    } else {
        console.error('모달을 찾을 수 없습니다:', `modal-${fieldName}`);
    }
}

// 모달 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = "none"; // 숨기기
    }
}

function updateCharacterCount() {
    const inputField = document.getElementById('store-field');
    const charCount = document.getElementById('char-count');
    const currentLength = inputField.value.length;

    // 글자 수 업데이트
    if (currentLength <= 20) {
        charCount.textContent = `${currentLength}/20`; // 20글자 이내일 때만 업데이트
    }

    // 20글자 초과 시 alert 띄우기
    if (currentLength > 20) {
        alert('입력 최대치를 초과했습니다.');
        inputField.value = inputField.value.substring(0, 20); // 20글자 이상 입력된 경우, 20글자로 자르기
        charCount.textContent = `20/20`; // 20글자 넘어가면 카운트는 20/20으로 고정
    }
}


// AJAX를 사용하여 매장 정보 업데이트
function updateStoreDetails(field) {
    let value;

    if (field === 'category') {
        const categorySelect = document.getElementById("store-detail-category");
        value = categorySelect ? categorySelect.value : null;
    } else if (field === 'address') {
        // 주소 필드인 경우, hidden 필드(preStoAddress)에 저장된 값을 가져옴
        value = document.getElementById("preStoAddress").value.trim();
        console.log("주소 필드 value:", value); // 주소 값 확인
    } else {
        const inputElement = document.getElementById(`${field}-field`);
        value = inputElement ? inputElement.value.trim() : null;
    }

    if (!value) {
        alert("수정할 값을 입력해주세요.");
        return;
    }

    // AJAX 요청
    $.ajax({
        url: "/store/updateStoreDetails",  // 서버에서 처리할 URL
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({field, value}),  // field와 value를 JSON 형태로 전송
        success: function (response) {
            if (response.success) {
                alert(response.message || "업데이트가 성공적으로 완료되었습니다.");
                closeModal(`modal-${field}`);  // 모달 닫기
                location.reload();  // 페이지 새로고침 (원하는 대로 처리 가능)
            } else {
                alert(response.message || "업데이트에 실패했습니다. 다시 시도해주세요.");
            }
        },
        error: function (xhr) {
            console.error("업데이트 중 오류 발생:", xhr.responseText);
            alert("업데이트 중 오류가 발생했습니다.");
        },
    });
}

$(document).ready(function() {
    // 메뉴 추가 폼 제출 이벤트
    $(".menu-add-form").submit(function(event) {
        event.preventDefault(); // 기본 form submit 막기

        // 폼 데이터 가져오기
        const formData = new FormData(this);

        // AJAX로 데이터 전송
        $.ajax({
            type: "POST",
            url: "/addMenu",  // 서버의 메뉴 추가 처리 URL
            data: formData,
            processData: false, // 폼 데이터에 파일이 포함되므로 processData를 false로 설정
            contentType: false, // 파일을 전송하므로 contentType을 false로 설정
            dataType: "json",   // 응답 데이터 타입 설정
            success: function(response) {
                if (response.status === "success") {
                    // 메뉴 추가 성공 시, 메뉴 목록을 가져옴
                    const preStoId = response.preStoId; // 메뉴 추가가 성공하면 매장 ID를 받음

                    // 메뉴 목록을 다시 가져오는 AJAX 호출
                    $.ajax({
                        type: "POST",
                        url: "/store/getMenuList",  // 매장 메뉴 목록을 가져오는 URL
                        data: { preStoId: preStoId },  // 매장 ID를 전달
                        dataType: "json",
                        success: function(result) {
                            console.log(result); // 처리된 결과 확인

                            // result가 배열이고, 배열의 길이가 0일 경우
                            if (Array.isArray(result) && result.length === 0) {
                                alert("메뉴가 없습니다.");
                            } else {
                                getStoreMenuList(result); // 받은 데이터로 DOM 업데이트
                            }
                        },
                        error: function(e) {
                            console.log("매장 메뉴 정보 가져오기 실패", e);
                        }
                    });

                    // 메뉴 추가가 성공하면 모달 닫기
                    $("#menu-add-modal").hide(); // 모달을 숨김

                    // 폼 초기화
                    $(".menu-add-form")[0].reset(); // 폼 리셋

                    // 이미지 파일 입력 초기화 (파일 선택 초기화)
                    $("input[type='file']").val(""); // 이미지 파일 입력 필드 초기화

                    // 미리보기 이미지 초기화
                    $("#menuImagePreview").attr("src", ""); // 이미지 미리보기 초기화
                    $("#menuImagePreview").hide(); // 미리보기 이미지 숨기기
                    $("#menu-img-title").text("현재 적용중인 이미지: "); // 텍스트 초기화
                    $("#menuImgName").text(""); // 파일 이름 텍스트 초기화
                } else {
                    alert(response.message || "메뉴 추가 실패");
                }
            },
            error: function(error) {
                console.log("메뉴 추가 실패", error);
                alert("메뉴 추가 처리 중 오류가 발생했습니다.");
            }
        });
    });

    // 이미지 선택 시 미리보기
    $('#mpFile').on('change', function(event) {
        const file = event.target.files[0];  // 선택된 파일
        if (file) {
            // 이미지 파일만 처리
            const reader = new FileReader();  // 파일 리더 객체

            // 파일 리더로 이미지 파일을 읽고, 미리보기 업데이트
            reader.onload = function(e) {
                const imgUrl = e.target.result;  // 읽은 이미지의 URL

                // 이미지 미리보기 업데이트
                $('#menuImagePreview').attr('src', imgUrl);  // 미리보기 이미지 설정
                $('#menu-img-title').text("변경할 이미지: ");  // 텍스트 변경
                $('#menuImgName').text(file.name);  // 선택한 파일 이름 텍스트로 표시
                $('#menuImagePreview').show(); // 이미지 미리보기 표시
            };

            // 파일 읽기 (이미지 파일을 읽어들임)
            reader.readAsDataURL(file);
        } else {
            // 파일을 선택하지 않으면 미리보기 비우기
            $('#menuImagePreview').attr('src', '');  // 빈 상태로 설정
            $('#menu-img-title').text("현재 적용중인 이미지: ");  // 텍스트 원래대로 되돌리기
            $('#menuImgName').text('');  // 텍스트 비우기
            $('#menuImagePreview').hide(); // 미리보기 숨기기
        }
    });

    // 모달이 열릴 때마다 이미지 초기화
    $('#menu-add-modal').on('show.bs.modal', function() {
        // 폼 리셋
        $(".menu-add-form")[0].reset();

        // 이미지 초기화
        $("input[type='file']").val(""); // 파일 입력 필드 초기화
        $("#menuImagePreview").attr("src", "").hide(); // 이미지 미리보기 초기화
        $("#menu-img-title").text("현재 적용중인 이미지: "); // 텍스트 초기화
        $("#menuImgName").text(""); // 선택된 파일 이름 초기화
    });
});


// 가게 리스트 클릭 시 storeId를 서버로 전달하는 예시
$(document).on("click", ".store-item", function () { // .store-item은 각 가게 리스트 아이템의 클래스
    const storeId = $(this).data("store-id"); // data-store-id 속성에서 storeId 가져오기

    $.ajax({
        url: '/stores/details',
        method: 'POST',
        data: { storeId: storeId },
        success: function(response) {
            if (response.error) {
                alert(response.error);
            } else {
                // 세션이 업데이트되었으므로, alarm.js가 올바르게 동작할 수 있도록 새로고침 또는 필요한 로직 수행
                location.reload(); // 예시: 페이지 새로고침
            }
        },
        error: function(error) {
            console.error("Error fetching store details:", error);
        }
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const customRadio = document.getElementById('stop-custom');
    const customInputDiv = document.getElementById('custom-time-input');
    const otherRadios = document.querySelectorAll('.stop-radio:not(#stop-custom)');

    customRadio.addEventListener('change', function() {
        if (customRadio.checked) {
            customInputDiv.style.display = 'block';  // 입력 필드 보이기

            // "직접 설정"을 선택하면 다른 라디오 버튼들의 선택을 해제
            otherRadios.forEach(function(radio) {
                radio.checked = false;
            });
        }
    });

    // 다른 옵션이 선택되면 입력 필드를 숨기고 "직접 설정" 외의 라디오 버튼들을 활성화
    otherRadios.forEach(function(radio) {
        radio.addEventListener('change', function() {
            customInputDiv.style.display = 'none';  // 입력 필드 숨기기
        });
    });
});
    //배달 지역 상태 토글
document.querySelectorAll(".toggle").forEach(toggle => {
    toggle.addEventListener("click", function() {
        this.classList.toggle("on");
        const label = this.previousElementSibling;
        if (this.classList.contains("on")) {
            label.textContent = "가능";
        } else {
            label.textContent = "불가능";
        }
    });
});