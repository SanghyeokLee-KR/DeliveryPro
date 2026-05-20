document.addEventListener('DOMContentLoaded', () => {
    const phoneInput = document.getElementById('phone');
    const phoneWrapper = document.getElementById('phoneWrapper');
    const phoneErrorMessage = document.getElementById('phoneErrorMessage');

    const touched = {
        phone: false,
    };
    const phoneRegex = /^01[0-9]-?\d{3,4}-?\d{4}$/;

    function validateField(field) {
        const wrapper = field.closest('.input-wrapper');
        const fieldName = field.id || field.name;
        if (!touched[fieldName]) return true;

        let isValid = true;

        switch (field) {
            case phoneInput:
                if (!phoneRegex.test(phoneInput.value.trim())) {
                    showError(phoneWrapper, phoneErrorMessage, '⚠ 올바른 전화번호 형식이 아닙니다.');
                    isValid = false;
                } else {
                    hideError(phoneWrapper, phoneErrorMessage);
                    isValid = true;
                }
                break;
            default:
                break;
        }

        console.log(`validateField - ${field.id || field.name}:`, isValid);
        return isValid;
    }

    function validateAll() {
        Object.keys(touched).forEach(key => touched[key] = true);

        let valid = true;
        valid = validateField(phoneInput) && valid;

        console.log('validateAll:', valid);
        return valid;
    }

    function showError(wrapper, errorMessage, message) {
        wrapper.classList.add('error');
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }

    function hideError(wrapper, errorMessage) {
        wrapper.classList.remove('error');
        errorMessage.textContent = '';
        errorMessage.style.display = 'none';
    }

    // 운영일 버튼 클릭
    document.addEventListener("DOMContentLoaded", () => {
        const dayBoxes = document.querySelectorAll(".day-box");
        const hiddenInput = document.getElementById("store-operating-days");

        const updateSelectedDays = () => {
            const selectedDays = Array.from(dayBoxes)
                .filter((box) => box.classList.contains("selected"))
                .map((box) => box.dataset.value);

            hiddenInput.value = selectedDays.join(","); // 선택된 요일을 콤마로 구분하여 저장
        };

        dayBoxes.forEach((box) => {
            box.addEventListener("click", () => {
                // 선택 상태 토글
                box.classList.toggle("selected");

                // 선택된 값 업데이트
                updateSelectedDays();
            });
        });

        // 초기 상태값 설정
        updateSelectedDays();
    });
    /*휴무일 요일 선택 버튼*/
    document.querySelectorAll('.day-box').forEach(button => {
        button.addEventListener('click', () => {
            button.classList.toggle('clicked'); // 클릭 시 상태 변경
        });
    });
    /*배달 불가능 지역*/

    const selectElem = document.getElementById('preStoDeliveryArea');
    const selectedAreasDiv = document.getElementById('selected-areas');
    const hiddenInput = document.getElementById('selectedAreasInput');

    // 선택된 지역들을 저장할 배열
    let selectedAreas = [];

    // 선택된 지역들을 화면에 표시하는 함수
    function updateSelectedAreasDisplay() {
        // 기존 내용을 초기화
        selectedAreasDiv.innerHTML = "";
        // 배열에 있는 각 값을 div로 생성하고, 삭제 버튼도 추가
        selectedAreas.forEach(function(area, index) {
            const areaDiv = document.createElement("div");
            areaDiv.classList.add("selected-area-item");

            // 지역 텍스트
            const areaText = document.createElement("span");
            areaText.textContent = area;

            // 삭제 버튼 생성 (클릭 시 해당 항목 삭제)
            const deleteButton = document.createElement("button");
            deleteButton.textContent = "x";
            deleteButton.classList.add("delete-button");
            deleteButton.style.marginLeft = "10px";
            deleteButton.addEventListener('click', function() {
                // 배열에서 해당 항목 제거
                selectedAreas.splice(index, 1);
                updateSelectedAreasDisplay();
                updateHiddenInput();
            });

            areaDiv.appendChild(areaText);
            areaDiv.appendChild(deleteButton);
            selectedAreasDiv.appendChild(areaDiv);
        });
    }

    // 배열을 콤마로 결합하여 hidden input의 value에 저장하는 함수
    function updateHiddenInput() {
        hiddenInput.value = selectedAreas.join(',');
        console.log("업데이트된 배달 불가 지역:", hiddenInput.value);
    }

    // select 요소의 change 이벤트 처리
    selectElem.addEventListener('change', function() {
        const selectedValue = this.value;
        // 값이 비어있지 않고, 이미 선택된 값이 아니면 배열에 추가
        if (selectedValue && !selectedAreas.includes(selectedValue)) {
            selectedAreas.push(selectedValue);
            updateSelectedAreasDisplay();
            updateHiddenInput();
        }
        // 선택 후 select 요소를 빈 값으로 리셋 (다시 선택 가능하도록)
        this.value = "";
    });

    // 페이지 로드 시 초기 업데이트 (기존 값이 있다면)
    updateHiddenInput();

    //전화번호 조건식
    document.getElementById('store-phone').addEventListener('input', function (e) {
        const phoneInput = e.target;
        let phoneValue = phoneInput.value;

        // 항상 '010-'으로 시작
        if (!phoneValue.startsWith('010-')) {
            phoneValue = '010-';
        }

        // '010-' 이후 숫자만 입력 가능
        phoneValue = phoneValue.replace(/[^0-9-]/g, ''); // 숫자와 하이픈 외 제거
        phoneValue = phoneValue.replace(/^010-/, '');    // '010-' 제거 후 뒤 숫자만 처리

        // 하이픈 추가 로직
        if (phoneValue.length > 4 && !phoneValue.includes('-', 4)) {
            phoneValue = phoneValue.slice(0, 4) + '-' + phoneValue.slice(4);
        }

        // 최대 길이 제한 (13자: 010-XXXX-XXXX)
        phoneValue = phoneValue.slice(0, 9);

        // 최종 값 설정
        phoneInput.value = '010-' + phoneValue;

        // 유효성 검사
        const phonePattern = /^010-\d{4}-\d{4}$/;
        if (phonePattern.test(phoneInput.value)) {
            phoneInput.style.border = '2px solid green'; // 올바른 입력 시 테두리 녹색
        } else {
            phoneInput.style.border = '2px solid red'; // 잘못된 입력 시 테두리 빨간색
        }
    });


// 폼 제출 시 추가 확인
    document.querySelector('form').addEventListener('submit', function (e) {
        const phoneInput = document.getElementById('store-phone');
        const phoneValue = phoneInput.value;

        // 전화번호 정규식
        const phonePattern = /^010-\d{4}-\d{4}$/;

        if (!phonePattern.test(phoneValue)) {
            e.preventDefault(); // 폼 제출 방지
            alert('올바른 전화번호 형식(예: 010-1234-5678)을 입력하세요.');
            phoneInput.focus();
        }
    });

    //최소 주문 금액  조건 식
    document.getElementById('store-min-order-amount').addEventListener('input', function (e) {
        const inputField = e.target;
        const errorMessage = document.getElementById('min-order-error');

        // 현재 입력값을 가져옴
        const currentValue = inputField.value;

        // 숫자만 포함하는지 확인
        if (/[^0-9]/.test(currentValue)) {
            // 숫자가 아닌 값이 포함되어 있을 때 경고 메시지 표시
            errorMessage.style.display = 'block';
            // 숫자가 아닌 문자를 모두 제거
            inputField.value = currentValue.replace(/[^0-9]/g, '');
        } else {
            // 숫자만 포함되어 있을 때 경고 메시지 숨김
            errorMessage.style.display = 'none';
        }
    });

    const openingHourStart = document.getElementById("openingHourStart");
    const openingMinuteStart = document.getElementById("openingMinuteStart");
    const openingHourEnd = document.getElementById("openingHourEnd");
    const openingMinuteEnd = document.getElementById("openingMinuteEnd");
    const preStoOpeningHoursInput = document.getElementById("preStoOpeningHours");

    // 두 select의 값을 결합하여 hidden input에 설정하는 함수
    function updateOperatingHours() {
        const startHour = openingHourStart.value;
        const startMinute = openingMinuteStart.value;
        const endHour = openingHourEnd.value;
        const endMinute = openingMinuteEnd.value;

        // 운영 시간을 원하는 형식으로 결합 (예: "09:00 ~ 10:00")
        const operatingHours = `${startHour}:${startMinute} ~ ${endHour}:${endMinute}`;
        preStoOpeningHoursInput.value = operatingHours;
        console.log("업데이트된 운영시간:", operatingHours);
    }

    // 각 select 요소에 change 이벤트 리스너 추가
    openingHourStart.addEventListener("change", updateOperatingHours);
    openingMinuteStart.addEventListener("change", updateOperatingHours);
    openingHourEnd.addEventListener("change", updateOperatingHours);
    openingMinuteEnd.addEventListener("change", updateOperatingHours);

    // 페이지 로드 시 한 번 업데이트 호출하여 초기값 설정
    updateOperatingHours();
}); // 여기에 괄호를 맞춤

const dayBoxes = document.querySelectorAll('.day-box');
const operatingDaysInput = document.getElementById('store-operating-days');

// 선택된 요일을 업데이트하는 함수
function updateOperatingDays() {
    // 선택된 day-box의 data-value 값을 배열로 수집
    const selectedDays = Array.from(dayBoxes)
        .filter(box => box.classList.contains('selected'))
        .map(box => box.getAttribute('data-value'));

    // 배열을 콤마로 결합한 문자열을 hidden input에 저장
    operatingDaysInput.value = selectedDays.join(',');
    console.log("Selected Operating Days:", operatingDaysInput.value);
}

// 각 day-box에 클릭 이벤트 리스너를 추가하여 선택 상태 토글 및 값 업데이트
dayBoxes.forEach(box => {
    box.addEventListener('click', function () {
        box.classList.toggle('selected'); // 선택/해제 토글
        updateOperatingDays();            // hidden input 업데이트
    });
});

// 초기 호출: 페이지 로드시 현재 선택된 값(없다면 빈 문자열)이 hidden input에 설정됨
updateOperatingDays();



