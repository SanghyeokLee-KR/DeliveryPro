// customer_main_ajaxslider.js

document.addEventListener('DOMContentLoaded', async () => {
    console.log("[AJAX Slider] DOMContentLoaded!");

    // 1) 광고 슬라이더 컨테이너
    const swiperWrapper = document.getElementById('sliderWrapper');
    if (!swiperWrapper) {
        console.error("[AJAX Slider] #sliderWrapper not found!");
        return;
    }

    // 2) AJAX로 광고 목록 가져오기
    let ads = [];
    try {
        // 서버에 GET /api/advertisements
        const response = await fetch('/api/advertisements');
        // 만약 서버가 /api/ads 였다면 아래를 '/api/ads'로 해야 함
        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }
        ads = await response.json();
        console.log("[AJAX Slider] ads=", ads);
        // e.g. [{advId:1, advTitle:"짜장면", advImageUrl:"/admin/img/광고/1번_광고_짜장면.jpg",...},...]
    } catch (error) {
        console.error("[AJAX Slider] 광고 목록 불러오기 실패:", error);
        return;
    }

    // 3) 광고 목록이 비어있으면 중단
    if (!Array.isArray(ads) || ads.length === 0) {
        console.warn("[AJAX Slider] 광고가 없습니다.");
        return;
    }

    // 4) <img> 태그를 동적으로 생성해 swiperWrapper에 추가
    ads.forEach(ad => {
        const imgEl = document.createElement('img');
        imgEl.classList.add('user-main-slider-image');
        imgEl.src = ad.advImageUrl;
        imgEl.alt = `광고 ${ad.advId}번 사진`;
        swiperWrapper.appendChild(imgEl);
    });

    // 5) 이제 이미지가 DOM에 추가되었으니 슬라이더 초기화
    initSlider();
});

// [슬라이더 초기화 함수]
function initSlider() {
    console.log("[AJAX Slider] initSlider() called.");
    const swiperWrapper = document.getElementById('sliderWrapper');
    const images = document.querySelectorAll('.user-main-slider-image');
    const paginationDots = document.querySelectorAll('.pagination-dot');
    const leftArrow = document.querySelector('.left-arrow');
    const rightArrow = document.querySelector('.right-arrow');

    let currentIndex = 1;
    const totalImages = images.length;

    // 클론 생성 (첫/마지막)
    if (totalImages > 1) {
        const firstClone = images[0].cloneNode(true);
        const lastClone = images[images.length - 1].cloneNode(true);
        swiperWrapper.appendChild(firstClone);
        swiperWrapper.insertBefore(lastClone, images[0]);
    }

    // 초기 위치
    swiperWrapper.style.transform = `translateX(-${100 * currentIndex}%)`;

    // 슬라이드 이동 함수
    function updateSliderPosition() {
        const offset = -currentIndex * 100;
        swiperWrapper.style.transition = 'transform 0.5s ease-in-out';
        swiperWrapper.style.transform = `translateX(${offset}%)`;

        // 페이지네이션 활성화
        paginationDots.forEach(dot => dot.classList.remove('active'));

        if (totalImages > 0) {
            let pageIndex = (currentIndex - 1 + totalImages) % totalImages;
            paginationDots[pageIndex].classList.add('active');
        }
    }

    // 클론 처리
    function handleClones() {
        if (currentIndex === 0) {
            swiperWrapper.style.transition = 'none';
            currentIndex = totalImages;
            swiperWrapper.style.transform = `translateX(-${100 * currentIndex}%)`;
        } else if (currentIndex === totalImages + 1) {
            swiperWrapper.style.transition = 'none';
            currentIndex = 1;
            swiperWrapper.style.transform = `translateX(-${100 * currentIndex}%)`;
        }
    }

    // 자동 슬라이드
    function autoSlide() {
        currentIndex++;
        updateSliderPosition();
        setTimeout(handleClones, 500);
    }

    // 좌우 화살표
    if (leftArrow) {
        leftArrow.addEventListener('click', () => {
            currentIndex--;
            if (currentIndex < 0) currentIndex = totalImages;
            updateSliderPosition();
            setTimeout(handleClones, 500);
        });
    }
    if (rightArrow) {
        rightArrow.addEventListener('click', () => {
            currentIndex++;
            if (currentIndex > totalImages + 1) currentIndex = 1;
            updateSliderPosition();
            setTimeout(handleClones, 500);
        });
    }

    // 자동 슬라이드 (3초)
    if (totalImages > 1) {
        let autoSlideInterval = setInterval(autoSlide, 3000);

        // 마우스 오버 -> 정지
        swiperWrapper.addEventListener('mouseover', () => {
            clearInterval(autoSlideInterval);
        });

        // 마우스 아웃 -> 재시작
        swiperWrapper.addEventListener('mouseout', () => {
            autoSlideInterval = setInterval(autoSlide, 3000);
        });
    }

    // 초기 슬라이더 위치 갱신
    updateSliderPosition();
}
$('#join').click(() => {
    location.href = "/mJoinForm";
});

$('#login').click(() => {
    location.href = "/mLoginForm";
});
document.querySelectorAll('.user-main-menu-item img').forEach(img => {
    img.addEventListener('click', function () {
        // 선택된 카테고리 가져오기
        const selectedCategory = this.getAttribute("data-category");

        if (!selectedCategory) {
            console.error("카테고리가 설정되지 않았습니다.");
            return;
        }

        // 모든 탭 비활성화
        document.querySelectorAll('.user-main-menu-item img').forEach(img => {
            img.classList.remove('active');
        });

        // 클릭한 탭 활성화
        this.classList.add('active');

        // 카테고리를 인코딩하여 URL로 이동
        const encodedCategory = encodeURIComponent(selectedCategory);
        location.href = `/storeList?category=${encodedCategory}`;
    });
});
