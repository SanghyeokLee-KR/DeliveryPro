

document.getElementById("arrow-container").addEventListener("click", function () {
    var arrow = document.getElementById("arrow");
    var card = document.querySelector(".card");

    // 현재 회전 각도를 확인하고 180도 회전
    var currentRotation = arrow.style.transform || "rotate(0deg)";
    var currentDeg = currentRotation === "rotate(180deg)" ? 0 : 180;

    // 회전 적용
    arrow.style.transform = "rotate(" + currentDeg + "deg)";

    // 카드 보이기 또는 숨기기 (toggle로 클래스 추가/제거)
    card.classList.toggle("show");
});


document.addEventListener("DOMContentLoaded", function () {
    var pointSpan = document.getElementById("point-span");
    var pointValue = pointSpan.textContent.trim(); // 포인트 값 가져오기

    if (pointValue.length > 5) {
        pointSpan.style.fontSize = "17px";
    } else if (pointValue.length > 3) {
        pointSpan.style.fontSize = "20px";
    } else {
        pointSpan.style.fontSize = "24px";
    }
});
