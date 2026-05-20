// DOM이 모두 로드된 후 실행
document.addEventListener("DOMContentLoaded", function () {
    // scroll-text 요소의 내용을 가져옴
    const scrollText = document.getElementById("scroll-text");

    // 이미 렌더링된 텍스트 데이터를 사용
    const sessionAddress = scrollText.textContent;

    // 텍스트가 길 경우 출력 (필요한 애니메이션 로직 추가 가능)
    console.log("세션 주소 데이터:", sessionAddress);
});

/* 로그인 확인 js */
function loginConfirm(event) {
    // a 태그 기본 동작 중단
    event.preventDefault();

    // 실제 클릭한 a 태그를 가져옴
    const link = event.currentTarget;
    const href = link.getAttribute("href");

    console.log("실제 클릭한 요소의 href:", href);

// 각 ID를 추출
    if (href.includes('/myPage/')) {
        const memIdMatch = href.match(/\/myPage\/(\d+)/);
        const memId = memIdMatch ? memIdMatch[1] : null;
        if (memId) {
            console.log('memId found:', memId);
            window.location.href = href;
            return true;
        } else {
            alert("로그인후 이용해주세요 ")
            window.location.href = "/mLoginForm"; // 로그인 페이지로 이동
            return false;
        }
    } else if (href.includes('/cart/')) {
        const cartmemIdMatch = href.match(/\/cart\/(\d+)/);
        const cartmemId = cartmemIdMatch ? cartmemIdMatch[1] : null;
        if (cartmemId) {
            console.log('cartmemId found:', cartmemId);
            window.location.href = href;
            return true;
        } else {
            alert("로그인후 이용해주세요 ")
            window.location.href = "/mLoginForm"; // 로그인 페이지로 이동
            return false;
        }
    }
}

/* 주소창 추가 확인 js */
function confirmNavigation(event) {
    // a 태그 기본 동작 중단
    event.preventDefault();

    // 실제 클릭한 a 태그를 가져옴
    const link = event.currentTarget;
    const href = link.getAttribute("href");

    console.log("실제 클릭한 요소의 href:", href);

    // href에서 mem_id 추출
    const memIdMatch = href.match(/\/myPage\/(\d+)/);
    const memId = memIdMatch ? memIdMatch[1] : null;

    console.log("memIdMatch:", memIdMatch);
    console.log("memId:", memId);

    // 로그인 상태 확인 (memId가 없으면 로그인되지 않은 것으로 판단)
    if (!memId) {
        alert("로그인 해주세요");
        window.location.href = "/mLoginForm"; // 로그인 페이지로 이동
        return false;
    }

    // confirm 창 표시
    const userConfirmed = confirm("주소를 추가하러 이동하시겠습니까?");
    if (!userConfirmed) {
        // 사용자가 '취소'를 클릭하면 이동 중단
        return false;
    }

    // '확인'을 클릭하면 페이지 이동
    window.location.href = href;
    return true;
}

/* 탭 전환 상태를 localStorage에 저장 */
function setActiveTab(tabName) {
    localStorage.setItem("activeTab", tabName);
}


document.addEventListener('DOMContentLoaded', function() {
    const checkbox = document.querySelector('#button-3 .checkbox');
    if (!checkbox) {
        console.error("체크박스 요소를 찾을 수 없습니다.");
        return;
    }

    // 로컬 저장소에서 상태를 불러오기
    const savedState = localStorage.getItem('toggleState');
    checkbox.checked = (savedState === 'checked');

    // checkbox 상태 변경 이벤트 처리
    checkbox.addEventListener('change', function() {
        if (checkbox.checked) {
            localStorage.setItem('toggleState', 'checked');
            window.location.href = "rider";
        } else {
            localStorage.setItem('toggleState', 'unchecked');
            window.location.href = "customer";
        }
    });
});