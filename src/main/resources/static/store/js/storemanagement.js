document.addEventListener("DOMContentLoaded", function () {
    // 사이드바 메뉴 펼치기/접기 기능
    const sections = document.querySelectorAll(".main-section");

    sections.forEach(section => {
        const titleElem = section.querySelector(".section-title");
        const subMenu = section.querySelector(".sub-menu");

        if (titleElem && subMenu) {
            titleElem.addEventListener("click", () => {
                const isOpen = section.classList.contains("open");

                // 모든 섹션을 닫음 (옵션)
                sections.forEach(sec => {
                    sec.classList.remove("open");
                    sec.querySelector(".section-title").setAttribute("aria-expanded", "false");
                    sec.querySelector(".sub-menu").classList.add("collapsed");
                    sec.querySelector(".sub-menu").setAttribute("aria-hidden", "true");
                });

                if (!isOpen) {
                    section.classList.add("open");
                    subMenu.classList.remove("collapsed");
                    section.querySelector(".section-title").setAttribute("aria-expanded", "true");
                    subMenu.setAttribute("aria-hidden", "false");
                }
            });

            // 키보드 접근성
            titleElem.addEventListener("keypress", (e) => {
                if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    titleElem.click();
                }
            });
        }
    });

    // 햄버거 메뉴 토글
    const hamburger = document.querySelector(".hamburger");
    const sidebar = document.querySelector(".sidebar");
    const mainContent = document.querySelector(".main-content");

    hamburger.addEventListener("click", () => {
        hamburger.classList.toggle("active");
        sidebar.classList.toggle("visible");
    });

    // 키보드 접근성 for hamburger
    hamburger.addEventListener("keypress", (e) => {
        if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            hamburger.click();
        }
    });

    // 클릭 외부 영역을 클릭하면 사이드바 닫힘 (모바일용)
    mainContent.addEventListener("click", () => {
        if (sidebar.classList.contains("visible")) {
            sidebar.classList.remove("visible");
            hamburger.classList.remove("active");
        }
    });

});

function toggleDarkMode() {
    document.body.classList.toggle('dark-mode');
}
