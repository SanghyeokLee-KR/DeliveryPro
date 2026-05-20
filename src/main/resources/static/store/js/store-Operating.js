// 영업시간 수정
document.getElementById("store-operating-modify").addEventListener("click", function() {
    var editForm = document.getElementById("store-operating-edit");
    var modifyLink = document.getElementById("store-operating-modify");

    // 숨겨져 있으면 보이게, 보이면 숨기기
    if (editForm.style.display === "none" || editForm.style.display === "") {
        editForm.style.display = "block";
        modifyLink.textContent = "숨김";  // 텍스트를 "숨김"으로 변경
    } else {
        editForm.style.display = "none";
        modifyLink.textContent = "수정";  // 텍스트를 "수정"으로 변경
    }
});

// 정기 휴무일 수정
document.getElementById("store-holiday-modify").addEventListener("click", function() {
    var editForm = document.getElementById("store-holiday-edit");
    var modifyLink = document.getElementById("store-holiday-modify");

    // 숨겨져 있으면 보이게, 보이면 숨기기
    if (editForm.style.display === "none" || editForm.style.display === "") {
        editForm.style.display = "block";
        modifyLink.textContent = "숨김";  // 텍스트를 "숨김"으로 변경
    } else {
        editForm.style.display = "none";
        modifyLink.textContent = "수정";  // 텍스트를 "수정"으로 변경
    }
});

// 임시 휴무일 수정
document.getElementById("temporary-holiday-modify").addEventListener("click", function() {
    var editForm = document.getElementById("temporary-holiday-edit");
    var modifyLink = document.getElementById("temporary-holiday-modify");

    // 숨겨져 있으면 보이게, 보이면 숨기기
    if (editForm.style.display === "none" || editForm.style.display === "") {
        editForm.style.display = "block";
        modifyLink.textContent = "숨김";  // 텍스트를 "숨김"으로 변경
    } else {
        editForm.style.display = "none";
        modifyLink.textContent = "수정";  // 텍스트를 "수정"으로 변경
    }
});