document.addEventListener('DOMContentLoaded', () => {

    const agreeSection = document.getElementById('agreeSection');
    const signupSection = document.getElementById('signupSection');
    const termsContainer = document.getElementById('terms-container');

    termsData.forEach(term => {
        // 약관 항목 생성
        const termItem = document.createElement('div');
        termItem.classList.add('terms-item');

        // 약관 헤더 생성
        const termsHeader = document.createElement('div');
        termsHeader.classList.add('terms-header');

        // 체크박스 라벨 생성
        const label = document.createElement('label');
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.classList.add('term-check');
        if (term.title.includes('(필수)')) {
            checkbox.classList.add('required');
        } else {
            checkbox.classList.add('optional');
        }
        checkbox.id = term.id;
        label.appendChild(checkbox);
        label.appendChild(document.createTextNode(` ${term.title}`));

        // '자세히' 링크 생성
        const detailLink = document.createElement('span');
        detailLink.classList.add('detail-link');
        detailLink.setAttribute('data-target', `${term.id}-modal`);
        detailLink.textContent = '자세히';

        // 체크박스와 링크를 헤더에 추가
        termsHeader.appendChild(label);
        termsHeader.appendChild(detailLink);

        // 약관 항목에 헤더 추가
        termItem.appendChild(termsHeader);

        // 약관 항목을 컨테이너에 추가
        termsContainer.appendChild(termItem);

        // 모달 생성
        const modal = document.createElement('div');
        modal.classList.add('modal');
        modal.id = `${term.id}-modal`;

        const modalContent = document.createElement('div');
        modalContent.classList.add('modal-content');

        const closeBtn = document.createElement('span');
        closeBtn.classList.add('close-btn');
        closeBtn.innerHTML = '&times;';

        const modalBody = document.createElement('div');
        modalBody.classList.add('modal-body');
        modalBody.innerHTML = term.content;

        modalContent.appendChild(closeBtn);
        modalContent.appendChild(modalBody);
        modal.appendChild(modalContent);

        // 모달을 body에 추가
        document.body.appendChild(modal);
    });

    // 체크박스 및 버튼 상태 관리
    const checkAll = document.getElementById('checkAll');
    const termChecks = document.querySelectorAll('.term-check');
    const requiredTerms = document.querySelectorAll('.term-check.required');
    const nextBtn = document.getElementById('nextBtn');

    checkAll.addEventListener('change', () => {
        termChecks.forEach(chk => {
            chk.checked = checkAll.checked;
        });
        toggleNextButton();
    });

    termChecks.forEach(chk => {
        chk.addEventListener('change', () => {
            // 전체 동의 체크박스 상태 업데이트
            const allChecked = Array.from(termChecks).every(c => c.checked);
            checkAll.checked = allChecked;
            toggleNextButton();
        });
    });

    function toggleNextButton() {
        // 필수 약관 모두 체크되었는지 확인
        const allRequiredChecked = Array.from(requiredTerms).every(c => c.checked);
        nextBtn.disabled = !allRequiredChecked;
    }

    // "자세히" 링크 클릭 시 모달 표시
    const detailLinks = document.querySelectorAll('.detail-link');
    const modals = document.querySelectorAll('.modal');
    const closeButtons = document.querySelectorAll('.close-btn');

    detailLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const targetId = link.getAttribute('data-target');
            const modal = document.getElementById(targetId);
            modal.style.display = 'flex';
        });
    });

    // 모달 닫기 버튼 클릭 시 모달 숨기기
    closeButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const modal = btn.closest('.modal');
            modal.style.display = 'none';
        });
    });

    // 배경 클릭 시 모달 닫기
    modals.forEach(modal => {
        modal.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal.style.display = 'none';
            }
            nextBtn.addEventListener('click', () => {
                agreeSection.style.display = 'none'; // 약관 동의 섹션 숨기기
                signupSection.style.display = 'block'; // 회원가입 섹션 표시
            });
        });
    });
    const showError = (wrapper, messageElem, text) => {
        messageElem.innerText = text;
        messageElem.style.display = 'block';
        wrapper.classList.add('error');
        wrapper.classList.remove('success');
    };

    const hideError = (wrapper, messageElem) => {
        messageElem.style.display = 'none';
        wrapper.classList.remove('error');
        wrapper.classList.remove('success');
    };

    const markTouched = (field) => {
        const fieldName = field.id || field.name;
        touched[fieldName] = true;
    };

    // 필드, 상태 관련 변수 선언
    const form = document.getElementById('registrationForm');
    const submitBtn = document.getElementById('submitBtn');

    const checkIdBtn = document.getElementById('checkIdBtn');
    const userId = document.getElementById('userId');
    const idWrapper = document.getElementById('idWrapper');
    const idErrorMessage = document.getElementById('idErrorMessage');

    const password = document.getElementById('password');
    const passwordCheck = document.getElementById('passwordCheck');
    const passwordWrapper = document.getElementById('passwordWrapper');
    const passwordCheckWrapper = document.getElementById('passwordCheckWrapper');
    const passwordErrorMessage = document.getElementById('passwordErrorMessage');
    const passwordLengthError = document.getElementById('passwordLengthError');

    const nameInput = document.getElementById('name');
    const nameWrapper = document.getElementById('nameWrapper');
    const nameErrorMessage = document.getElementById('nameErrorMessage');

    const nicknameInput = document.getElementById('nickname');
    const nicknameWrapper = document.getElementById('nicknameWrapper');
    const nicknameErrorMessage = document.getElementById('nicknameErrorMessage');

    const emailInput = document.getElementById('email');
    const emailInputWrapper = document.getElementById('emailInputWrapper');
    const emailErrorMessage = document.getElementById('emailErrorMessage');

    const phoneInput = document.getElementById('phone');
    const phoneWrapper = document.getElementById('phoneWrapper');
    const phoneErrorMessage = document.getElementById('phoneErrorMessage');

    const birthdate = document.getElementById('birthdate');
    const birthWrapper = document.getElementById('birthWrapper');
    const birthErrorMessage = document.getElementById('birthErrorMessage');

    const genderSelect = document.getElementById('gender');
    const genderWrapper = document.getElementById('genderWrapper');
    const genderErrorMessage = document.getElementById('genderErrorMessage');

    const postcodeInput = document.getElementById('sample6_postcode');
    const addressInput = document.getElementById('sample6_address');
    const detailAddressInput = document.getElementById('sample6_detailAddress');
    const detailAddressErrorMessage = document.getElementById('detailAddressErrorMessage');
    const addressErrorMessage = document.getElementById('addressErrorMessage');
    const postcodeErrorMessage = document.getElementById('postcodeErrorMessage');

    const inputSelectors = 'input[type="text"], input[type="password"], input[type="email"], input[type="tel"], input[type="date"], select';
    const allInputs = document.querySelectorAll(inputSelectors);

    const touched = {
        userId: false,
        password: false,
        passwordCheck: false,
        username: false,
        nickname: false, // 닉네임 추가
        email: false,
        phone: false,
        birthday: false,
        sex: false,
        postcode: false,
        address: false,
        detailAddress: false
    };

    const idRegex = /^[A-Za-z0-9]{5,20}$/;
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    const nameRegex = /^[가-힣]{2,5}$/;
    const nicknameRegex = /^[가-힣A-Za-z0-9]{2,100}$/; // 닉네임 유효성 정규식 (2~100자, 한글, 영문, 숫자)
    const emailRegex = /^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$/;
    const phoneRegex = /^01[0-9]-?\d{3,4}-?\d{4}$/;

    // 이벤트 리스너 추가
    allInputs.forEach((input) => {
        const wrapper = input.closest('.input-wrapper');
        const messageElem = input.nextElementSibling;

        input.addEventListener('focus', () => {
            if (wrapper) wrapper.style.border = '2px solid #B40000';
        });

        input.addEventListener('blur', () => {
            if (wrapper) wrapper.style.border = '1px solid #ccc';
            markTouched(input);
            validateField(input);
            toggleSubmitButton();
        });

        input.addEventListener('input', () => {
            if (touched[input.id || input.name]) {
                validateField(input);
                toggleSubmitButton();
            }
        });
    });

    genderSelect.addEventListener('focus', () => {
        genderWrapper.style.border = '2px solid black';
    });

    genderSelect.addEventListener('blur', () => {
        genderWrapper.style.border = '1px solid #ccc';
        markTouched(genderSelect);
        validateField(genderSelect);
        toggleSubmitButton();
    });

    genderSelect.addEventListener('change', () => {
        if (touched['sex']) {
            validateField(genderSelect);
            toggleSubmitButton();
        }
    });

    // 주소 검색
    const searchAddressBtn = document.getElementById('searchAddressBtn');
    searchAddressBtn.addEventListener('click', () => {
        sample6_execDaumPostcode();
    });

    function sample6_execDaumPostcode() {
        new daum.Postcode({
            oncomplete: function (data) {
                let addr = '';
                if (data.userSelectedType === 'R') {
                    addr = data.roadAddress;
                } else {
                    addr = data.jibunAddress;
                }

                // 추가: 주소에 "미추홀구"가 포함되어 있는지 확인
                if (addr.indexOf("미추홀구") === -1) {
                    alert("미추홀구에 해당하는 주소만 선택 가능합니다. 다시 선택해주세요.");
                    return; // 주소 업데이트 중단
                }

                postcodeInput.value = data.zonecode;
                addressInput.value = addr;
                detailAddressInput.focus();
                touched['postcode'] = true;
                touched['address'] = true;
                validateField(postcodeInput);
                validateField(addressInput);
                toggleSubmitButton();
            }
        }).open();
    }

    // 아이디 중복 확인 버튼 클릭 시 서버로 요청
    checkIdBtn.addEventListener('click', async () => {
        markTouched(userId);
        const userIdValue = userId.value.trim();

        if (!idRegex.test(userIdValue)) {
            showError(idWrapper, idErrorMessage, '⚠ 아이디는 영문/숫자 조합 5~20자여야 합니다.');
            return;
        } else {
            hideError(idWrapper, idErrorMessage);
        }

        try {
            const response = await fetch(`api/member/check-id?userId=${encodeURIComponent(userIdValue)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const data = await response.json();

            if (data.exists) {
                showError(idWrapper, idErrorMessage, '⚠ 사용할 수 없는 아이디입니다.');
                checkIdBtn.classList.add('error');
                checkIdBtn.classList.remove('success');
                checkIdBtn.textContent = '중복확인';
                checkIdBtn.disabled = false;
            } else {
                hideError(idWrapper, idErrorMessage);
                idWrapper.classList.add('success');
                idWrapper.classList.remove('error');
                checkIdBtn.textContent = '✅ 확인 완료';
                checkIdBtn.classList.add('success');
                checkIdBtn.disabled = true; // 중복확인 후 버튼 비활성화
            }
            toggleSubmitButton();
        } catch (error) {
            console.error('Error checking user ID:', error);
            showError(idWrapper, idErrorMessage, '⚠ 아이디 중복 확인 중 오류가 발생했습니다.');
            checkIdBtn.classList.add('error');
            checkIdBtn.classList.remove('success');
            checkIdBtn.textContent = '중복확인';
            checkIdBtn.disabled = false;
        }
    });

    // 필드 유효성 검증 함수
    function validateField(field) {
        const wrapper = field.closest('.input-wrapper');
        const fieldName = field.id || field.name;
        if (!touched[fieldName]) return true;

        let isValid = true;

        switch (field) {
            case userId:
                if (!idRegex.test(userId.value.trim())) {
                    showError(idWrapper, idErrorMessage, '⚠ 아이디는 영문/숫자 조합 5~20자여야 합니다.');
                    isValid = false;
                } else if (!checkIdBtn.classList.contains('success')) {
                    showError(idWrapper, idErrorMessage, '⚠ 아이디 중복확인을 해주세요.');
                    isValid = false;
                } else {
                    hideError(idWrapper, idErrorMessage);
                    isValid = true;
                }
                break;

            case password:
                if (password.value.length < 8) {
                    showError(passwordWrapper, passwordLengthError, '⚠ 비밀번호는 8자리 이상이어야 합니다.');
                    isValid = false;
                } else if (!passwordRegex.test(password.value)) {
                    showError(passwordWrapper, passwordLengthError, '⚠ 비밀번호는 영문, 숫자, 특수문자 포함 8자리 이상이어야 합니다.');
                    isValid = false;
                } else {
                    hideError(passwordWrapper, passwordLengthError);
                    isValid = true;
                }
                break;

            case passwordCheck:
                if (password.value.length >= 8) {
                    if (password.value !== passwordCheck.value) {
                        showError(passwordCheckWrapper, passwordErrorMessage, '⚠ 입력하신 비밀번호와 일치하지 않습니다.');
                        isValid = false;
                    } else {
                        hideError(passwordCheckWrapper, passwordErrorMessage);
                        isValid = true;
                    }
                } else {
                    hideError(passwordCheckWrapper, passwordErrorMessage);
                    isValid = passwordRegex.test(password.value);
                }
                break;

            case nameInput:
                if (!nameRegex.test(nameInput.value.trim())) {
                    showError(nameWrapper, nameErrorMessage, '⚠ 이름은 한글 2~5글자만 가능합니다.');
                    isValid = false;
                } else {
                    hideError(nameWrapper, nameErrorMessage);
                    isValid = true;
                }
                break;

            case nicknameInput:
                if (nicknameInput.value.trim() !== '' && !nicknameRegex.test(nicknameInput.value.trim())) {
                    showError(nicknameWrapper, nicknameErrorMessage, '⚠ 닉네임은 2~100자, 한글, 영문, 숫자만 가능합니다.');
                    isValid = false;
                } else {
                    hideError(nicknameWrapper, nicknameErrorMessage);
                    isValid = true;
                }
                break;

            case emailInput:
                if (!emailRegex.test(emailInput.value.trim())) {
                    showError(emailInputWrapper, emailErrorMessage, '⚠ 올바른 이메일 주소가 아닙니다.');
                    isValid = false;
                } else {
                    hideError(emailInputWrapper, emailErrorMessage);
                    isValid = true;
                }
                break;

            case phoneInput:
                if (!phoneRegex.test(phoneInput.value.trim())) {
                    showError(phoneWrapper, phoneErrorMessage, '⚠ 올바른 전화번호 형식이 아닙니다.');
                    isValid = false;
                } else {
                    hideError(phoneWrapper, phoneErrorMessage);
                    isValid = true;
                }
                break;

            case birthdate:
                if (!birthdate.value) {
                    showError(birthWrapper, birthErrorMessage, '⚠ 생년월일을 선택해주세요.');
                    isValid = false;
                } else {
                    hideError(birthWrapper, birthErrorMessage);
                    isValid = true;
                }
                break;

            case genderSelect:
                if (!genderSelect.value) {
                    showError(genderWrapper, genderErrorMessage, '⚠ 성별을 선택해주세요.');
                    isValid = false;
                } else {
                    hideError(genderWrapper, genderErrorMessage);
                    isValid = true;
                }
                break;

            case postcodeInput:
            case addressInput:
                if (!postcodeInput.value || !addressInput.value) {
                    showError(postcodeInput.closest('.input-wrapper'), postcodeErrorMessage, '⚠ 우편번호를 입력해주세요.');
                    showError(addressInput.closest('.input-wrapper'), addressErrorMessage, '⚠ 주소를 입력해주세요.');
                    isValid = false;
                } else {
                    hideError(postcodeInput.closest('.input-wrapper'), postcodeErrorMessage);
                    hideError(addressInput.closest('.input-wrapper'), addressErrorMessage);
                    isValid = true;
                }
                break;

            case detailAddressInput:
                if (detailAddressInput.value.trim() === '') {
                    showError(detailAddressWrapper, detailAddressErrorMessage, '⚠ 상세주소를 입력해주세요.');
                    isValid = false;
                } else {
                    hideError(detailAddressWrapper, detailAddressErrorMessage);
                    isValid = true;
                }
                break;

            default:
                break;
        }

        console.log(`validateField - ${field.id || field.name}:`, isValid);
        return isValid;
    }

    // 전체 유효성 검증 함수
    function validateAll() {
        Object.keys(touched).forEach(key => touched[key] = true);

        let valid = true;
        valid = validateField(userId) && valid;
        valid = validateField(password) && valid;
        valid = validateField(passwordCheck) && valid;
        valid = validateField(nameInput) && valid;
        valid = validateField(nicknameInput) && valid; // 닉네임 추가
        valid = validateField(emailInput) && valid;
        valid = validateField(phoneInput) && valid;
        valid = validateField(birthdate) && valid;
        valid = validateField(genderSelect) && valid;
        valid = validateField(postcodeInput) && valid;
        valid = validateField(addressInput) && valid;
        valid = validateField(detailAddressInput) && valid;
        // nickname은 선택 사항이므로 제외

        console.log('validateAll:', valid);
        return valid;
    }

    // 제출 버튼 토글 함수
    function toggleSubmitButton() {
        const isEnabled = allFieldsOkWithoutForcedTouch();
        submitBtn.disabled = !isEnabled;
        console.log('Submit Button Enabled:', isEnabled);
    }

    // 모든 필드가 유효한지 확인하는 함수
    function allFieldsOkWithoutForcedTouch() {
        for (const key in touched) {
            if (key === 'nickname') continue; // 선택 사항 제외
            const fieldElem = document.getElementById(key) || document.querySelector(`[name="${key}"]`);
            if (fieldElem && !validateField(fieldElem)) {
                return false;
            }
        }
        return true;
    }

    // 폼 제출 이벤트 (AJAX로 처리)
    form.addEventListener('submit', (e) => {
        if (!validateAll()) {
            e.preventDefault(); // 유효성 검사 실패 시 폼 제출 방지
            alert('입력된 정보를 확인해주세요.');
            return;
        }

        // 주소 하나로 합쳐서 기존 'address' 필드에 설정
        const combinedAddress = `${postcodeInput.value} ${addressInput.value} ${detailAddressInput.value}`;
        addressInput.value = combinedAddress;
    });
    // "다음" 버튼 클릭 시 섹션 전환
    nextBtn.addEventListener('click', () => {
        agreeSection.style.display = 'none'; // 약관 동의 섹션 숨기기
        signupSection.style.display = 'block'; // 회원가입 섹션 표시
    });
});