document.addEventListener("DOMContentLoaded", function () {
    // ===========================================
    // 1) 사이드바 메뉴 펼치기/접기
    // ===========================================
    const sections = document.querySelectorAll(".main-section");
    sections.forEach(section => {
        const titleElem = section.querySelector(".section-title");
        const subMenu = section.querySelector(".sub-menu");

        if (titleElem && subMenu) {
            titleElem.addEventListener("click", () => {
                const isOpen = section.classList.contains("open");

                // 모든 섹션 닫기 (옵션)
                sections.forEach(sec => {
                    sec.classList.remove("open");
                    sec.querySelector(".section-title").setAttribute("aria-expanded", "false");
                    sec.querySelector(".sub-menu").classList.add("collapsed");
                    sec.querySelector(".sub-menu").setAttribute("aria-hidden", "true");
                });

                // 현재 섹션만 열기
                if (!isOpen) {
                    section.classList.add("open");
                    subMenu.classList.remove("collapsed");
                    titleElem.setAttribute("aria-expanded", "true");
                    subMenu.setAttribute("aria-hidden", "false");
                }
            });

            // 키보드 접근성 (Enter/Space)
            titleElem.addEventListener("keypress", (e) => {
                if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    titleElem.click();
                }
            });
        }
    });

    // ===========================================
    // 2) 햄버거 메뉴 토글 (모바일)
    // ===========================================
    const hamburger = document.querySelector(".hamburger");
    const sidebar = document.querySelector(".sidebar");
    const mainContent = document.querySelector(".main-container");

    if (hamburger) {
        hamburger.addEventListener("click", () => {
            hamburger.classList.toggle("active");
            sidebar.classList.toggle("visible");
        });

        // 키보드 접근성
        hamburger.addEventListener("keypress", (e) => {
            if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                hamburger.click();
            }
        });
    }

    // 클릭 시 사이드바 닫힘 (모바일용)
    if (mainContent) {
        mainContent.addEventListener("click", () => {
            if (sidebar && sidebar.classList.contains("visible")) {
                sidebar.classList.remove("visible");
                hamburger.classList.remove("active");
            }
        });
    }

    // ===========================================
    // 3) 섹션 전환(콘텐츠 로드) 로직
    // ===========================================
    let currentSection = "defaultContent";

    function loadContent(section) {
        // 모든 섹션 숨기기
        const sectionDivs = document.querySelectorAll('.content-section');
        sectionDivs.forEach(div => div.style.display = 'none');

        // 선택한 섹션 표시
        const selectedSection = document.getElementById(section);
        if (selectedSection) {
            selectedSection.style.display = 'block';
            currentSection = section;

            // URL 업데이트
            window.history.pushState({}, '', `?currentSection=${section}`);
        }
    }

    // 페이지 로드 시 URL 파라미터에 따라 섹션 표시
    const urlParams = new URLSearchParams(window.location.search);
    const sectionParam = urlParams.get('currentSection') || "defaultContent";
    loadContent(sectionParam);

    // 뒤로/앞으로 가기 시 섹션 표시
    window.addEventListener('popstate', () => {
        const urlParams = new URLSearchParams(window.location.search);
        const sec = urlParams.get('currentSection') || "defaultContent";
        loadContent(sec);
    });

    // ===========================================
    // 4) 회원 목록 렌더링 (예시)
    // ===========================================
    const members = [
        {id: 1, name: '홍길동', email: 'hong@example.com', joinDate: '2024-01-01', status: 'active'},
        {id: 2, name: '김철수', email: 'kim@example.com', joinDate: '2023-12-15', status: 'inactive'},
        {id: 3, name: '이영희', email: 'lee@example.com', joinDate: '2024-01-10', status: 'active'},
        {id: 4, name: '박지민', email: 'park@example.com', joinDate: '2023-11-20', status: 'inactive'},
    ];

    function renderMemberList(filteredMembers) {
        const memberListElement = document.getElementById('memberList');
        if (!memberListElement) return;

        memberListElement.innerHTML = ''; // 기존 목록 비우기

        filteredMembers.forEach(member => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${member.id}</td>
                <td>${member.name}</td>
                <td>${member.email}</td>
                <td>${member.joinDate}</td>
                <td>${member.status === 'active' ? '활성' : '비활성'}</td>
            `;
            memberListElement.appendChild(row);
        });
    }

    function filterMembersByStatus() {
        const statusFilter = document.getElementById('statusFilter').value;
        const filteredMembers = members.filter(member => {
            if (statusFilter === '') return true;
            return member.status === statusFilter;
        });
        renderMemberList(filteredMembers);
    }

    function searchMembers() {
        const searchQuery = document.getElementById('searchMember').value.toLowerCase();
        const filteredMembers = members.filter(member => {
            return (
                member.name.toLowerCase().includes(searchQuery) ||
                member.email.toLowerCase().includes(searchQuery)
            );
        });
        renderMemberList(filteredMembers);
    }

    // 초기 전체 목록 표시
    renderMemberList(members);

    // ===========================================
    // 5) 다크 모드 버튼 & 아이콘 교체
    // ===========================================
    const darkModeBtn = document.getElementById("darkModeBtn");
    const darkModeIcon = document.getElementById("darkModeIcon");

    // 페이지 로드 시 localStorage로 테마 복원
    if (localStorage.getItem('theme') === 'dark') {
        document.body.classList.add('dark-mode');
        if (darkModeIcon) {
            darkModeIcon.src = "/admin/img/icons/moon.svg";
            darkModeIcon.alt = "다크 모드 아이콘";
        }
    }

    // 다크모드 토글
    if (darkModeBtn) {
        darkModeBtn.addEventListener("click", function () {
            toggleDarkMode();
        });
    }
});

// 다크 모드 토글 함수
function toggleDarkMode() {
    const body = document.body;
    const icon = document.getElementById('darkModeIcon');

    body.classList.toggle('dark-mode');

    if (body.classList.contains('dark-mode')) {
        // 다크모드 ON
        if (icon) {
            icon.src = "/admin/img/icons/moon.svg";
            icon.alt = "다크 모드 아이콘";
        }
        localStorage.setItem('theme', 'dark');
    } else {
        // 다크모드 OFF
        if (icon) {
            icon.src = "/admin/img/icons/sun.svg";
            icon.alt = "라이트 모드 아이콘";
        }
        localStorage.removeItem('theme');
    }
}
