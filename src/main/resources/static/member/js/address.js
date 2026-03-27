let addresses = []; // 배송지 데이터를 관리하는 배열

// 다음 주소 API를 통해 주소 검색
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            let addr = '';
            if (data.userSelectedType === 'R') {
                addr = data.roadAddress; // 도로명 주소
            } else {
                addr = data.jibunAddress; // 지번 주소
            }

            // 추가: 주소에 "미추홀구"가 포함되어 있는지 확인
            if (addr.indexOf("미추홀구") === -1) {
                alert("미추홀구에 해당하는 주소만 선택 가능합니다. 다시 선택해주세요.");
                return; // 주소 업데이트 중단
            }

            // 주소 데이터를 모달 입력 필드에 설정
            document.getElementById('address-field').value = addr;
        }
    }).open();
}

// 배송지 추가 모달 열기
function addAddress() {
    // 배송지 개수 확인
    if (addresses.length >= 3) {
        alert("배송지는 최대 3개까지만 추가할 수 있습니다.");
        return; // 함수 종료하여 모달이 열리지 않도록 함
    }

    const modalTitle = document.getElementById('modal-title');
    const addressField = document.getElementById('address-field');
    const nameField = document.getElementById('name-field');
    const addressIdField = document.getElementById('address-id-field');

    // 추가 모드 초기화
    modalTitle.textContent = '배송지 추가';
    addressField.value = '';
    nameField.value = '';
    addressIdField.value = '';

    // 모달 열기
    document.getElementById('address-modal').style.display = 'flex';
}

// 배송지 수정 모달 열기
function openAddressModal(id) {
    const modalTitle = document.getElementById('modal-title');
    const addressField = document.getElementById('address-field');
    const nameField = document.getElementById('name-field');
    const addressIdField = document.getElementById('address-id-field');

    // 수정 모드 설정
    const address = addresses.find(addr => addr.id === id);
    if (!address) {
        alert("수정할 배송지를 찾을 수 없습니다.");
        return;
    }

    modalTitle.textContent = '배송지 수정';
    addressField.value = address.address;
    nameField.value = address.name;
    addressIdField.value = id;

    // 모달 열기
    document.getElementById('address-modal').style.display = 'flex';
}

// 모달 닫기
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// 배송지 저장 (추가/수정)
async function saveAddress() {
    const addressField = document.getElementById('address-field').value.trim();
    const addressDetailField = document.getElementById('address-detail-field').value.trim();
    const nameField = document.getElementById('name-field').value.trim();
    const addressIdField = document.getElementById('address-id-field').value;

    if (!addressField || !addressDetailField || !nameField) {
        alert("주소, 상세 주소, 이름을 입력해주세요.");
        return;
    }

    // 배송지 개수 확인 (추가 모드일 때만)
    if (!addressIdField && addresses.length >= 3) {
        alert("배송지는 최대 3개까지만 추가할 수 있습니다.");
        return;
    }

    // 숨겨진 입력 필드에서 회원 ID 가져오기
    const memberId = document.getElementById('member-id').value;

    if (!memberId) {
        alert("회원 정보를 찾을 수 없습니다.");
        return;
    }

    const data = {
        id: addressIdField ? addressIdField : null, // Long 또는 null
        memberId: memberId, // 동적으로 가져온 회원 ID
        address: `${addressField} ${addressDetailField}`, // 주소와 상세 주소 합치기
        name: nameField,
        isMain: addresses.length === 0 ? "메인" : "서브" // 첫 번째 주소는 기본 주소로 설정
    };

    try {
        if (addressIdField) {
            // 수정 모드
            const response = await fetch(`/api/addresses/${data.id}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("배송지 수정 실패");
            }

            const updatedAddress = await response.json();
            const index = addresses.findIndex(addr => addr.id === updatedAddress.id);
            if (index !== -1) {
                addresses[index] = updatedAddress;
            }
        } else {
            // 추가 모드
            const response = await fetch('/api/addresses', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error("배송지 추가 실패");
            }

            const newAddress = await response.json();
            addresses.push(newAddress);
        }

        renderAddresses(); // 리스트 업데이트
        closeModal('address-modal');
    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// 배송지 삭제
async function deleteAddress(id) {
    if (confirm("배송지를 삭제하시겠습니까?")) {
        try {
            const response = await fetch(`/api/addresses/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error("배송지 삭제 실패");
            }

            addresses = addresses.filter(addr => addr.id !== id);
            renderAddresses();
        } catch (error) {
            console.error(error);
            alert("배송지 삭제 중 오류가 발생했습니다.");
        }
    }
}

// 기본 배송지 설정
async function setMainAddress(id) {
    const memberId = document.getElementById('member-id').value;

    if (!memberId) {
        alert("회원 정보를 찾을 수 없습니다.");
        return;
    }

    try {
        const response = await fetch(`/api/addresses/set-main/${id}?memberId=${memberId}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'}
        });

        if (!response.ok) {
            throw new Error("기본 배송지 설정 실패");
        }

        const updatedAddress = await response.json();

        // 모든 배송지의 isMain을 "서브"로 변경하고, 업데이트된 주소만 "메인"으로 설정
        addresses = addresses.map(addr => {
            if (addr.id === updatedAddress.id) {
                return updatedAddress;
            } else {
                return {...addr, isMain: "서브"};
            }
        });

        renderAddresses();
    } catch (error) {
        console.error(error);
        alert("기본 배송지 설정 중 오류가 발생했습니다.");
    }
}

// 배송지 목록 렌더링
function renderAddresses() {
    const container = document.getElementById('address-container');
    container.innerHTML = ''; // 기존 내용을 초기화

    addresses.forEach(address => {
        const addressDiv = document.createElement('div');
        addressDiv.className = 'add_test_out';
        addressDiv.innerHTML = `
            <div class="add_test_img">
                <img src="/member/img/my-page/위치-45.svg" alt="집 아이콘" width="30"/>
            </div>
            <div class="add_test_info">
                <div class="add_test_header">
                    <div class="add_test_title"><h3>${address.isMain === "메인" ? '기본 배송지' : '배송지'}</h3></div>
                    <div class="add_test_button">
                        <button onclick="deleteAddress(${address.id})">삭제</button>
                        <button onclick="openAddressModal(${address.id})">수정</button>
                    </div>
                </div>
                <div class="add_test_mid">
                    <p>${address.address}</p>
                    <p>이름: ${address.name}</p>
                </div>
                <div class="add_test_footer add_test_input">
                    <input type="radio" name="default-address" ${address.isMain === "메인" ? 'checked' : ''} 
                        onclick="setMainAddress(${address.id})" />
                    <label>기본 배송지로 선택</label>
                </div>
            </div>
        `;
        container.appendChild(addressDiv);
    });

    // 배송지 개수에 따라 추가 버튼 활성화/비활성화
    const addButton = document.getElementById('add-address-button'); // 배송지 추가 버튼의 ID 가정
    if (addButton) { // addButton이 존재하는지 확인
        if (addresses.length >= 3) {
            addButton.disabled = true;
            addButton.title = "배송지는 최대 3개까지만 추가할 수 있습니다.";
        } else {
            addButton.disabled = false;
            addButton.title = "";
        }
    } else {
        console.warn("Add address button not found! Please ensure there is a button with id 'add-address-button'.");
    }
}

// 초기 실행: 서버에서 배송지 목록 가져오기
document.addEventListener('DOMContentLoaded', async () => {
    // 숨겨진 입력 필드에서 회원 ID 가져오기
    const memberId = document.getElementById('member-id').value;

    if (!memberId) {
        console.error("회원 ID를 찾을 수 없습니다.");
        return;
    }

    try {
        const response = await fetch(`/api/addresses/member/${memberId}`);
        if (!response.ok) {
            throw new Error("배송지 목록 조회 실패");
        }
        const data = await response.json();
        addresses = data;
        renderAddresses();
    } catch (error) {
        console.error(error);
        alert("배송지 목록을 불러오는 중 오류가 발생했습니다.");
    }
});
