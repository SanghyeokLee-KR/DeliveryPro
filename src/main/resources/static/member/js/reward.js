$(document).ready(function () {
    // AJAX 요청
    $.ajax({
        url: '/memReward',
        method: 'POST',
        dataType: 'json',
        success: function (response) {
            console.log('리워드 데이터 :', response);

            var rewardAmount = response.rewardAmount;

            // 배지 이미지 및 텍스트/배경색 업데이트
            updateBadge(rewardAmount);

            // totalAmount 및 nextAmount 업데이트
            updateAmountDisplay(rewardAmount);

            // 프로그래스 바 업데이트
            updateProgressBar(rewardAmount);
        },
        error: function (xhr, status, error) {
            console.error('AJAX 요청 실패:', status, error);
        }
    });

    // AJAX 요청
    $.ajax({
        url: '/updateReward',
        method: 'POST',
        dataType: 'text',
        success: function (response) {
            console.log(response);
        },
        error: function (xhr, status, error) {
            console.error('AJAX 요청 실패:', status, error);
        }
    });

    // 금액에 따른 배지 이미지 및 텍스트/배경색 업데이트 함수
    function updateBadge(rewardAmount) {
        var { text, bgColor } = getGradeInfo(rewardAmount);

        // 배지 이미지 설정
        var badgeImage = getBadgeImage(rewardAmount);
        $('.rewardBadge').attr('src', badgeImage);

        // 배지 텍스트 및 배경색 설정
        $('.value_badge').text(text).css('background-color', bgColor);

        // 회원 등급 텍스트 설정
        $('#grade-text').text(text);
    }

    // 금액에 따른 총액과 다음 등급 금액을 업데이트하는 함수
    function updateAmountDisplay(rewardAmount) {
        $('#totalAmount').text(rewardAmount.toLocaleString() + '원');
        var nextAmount = calculateNextAmount(rewardAmount);
        $('#nextAmount').text(nextAmount);
    }

    // 다음 등급까지 금액을 계산하는 함수
    function calculateNextAmount(totalAmount) {
        const levels = [
            { threshold: 50000, text: '50,000원' },
            { threshold: 100000, text: '100,000원' },
            { threshold: 200000, text: '200,000원' }
        ];

        for (const level of levels) {
            if (totalAmount < level.threshold) {
                return (level.threshold - totalAmount).toLocaleString() + '원';
            }
        }

        return "최고 등급입니다";
    }

    // 회원 등급 텍스트와 배경색을 결정하는 함수
    function getGradeInfo(totalAmount) {
        if (totalAmount < 50000) {
            return { text: "Welcome", bgColor: "#55b1e4" };
        } else if (totalAmount < 100000) {
            return { text: "Family", bgColor: "#6DBB4E" };
        } else if (totalAmount < 200000) {
            return { text: "VIP", bgColor: "#F79304" };
        } else {
            return { text: "VVIP", bgColor: "#8A2BE2" };
        }
    }

    // 금액에 따른 배지 이미지를 반환하는 함수
    function getBadgeImage(rewardAmount) {
        if (rewardAmount < 50000) {
            return "/member/img/grade/등급-40.svg";
        } else if (rewardAmount < 100000) {
            return "/member/img/grade/등급-39.svg";
        } else if (rewardAmount < 200000) {
            return "/member/img/grade/등급-38.svg";
        } else {
            return "/member/img/grade/등급-37.svg";
        }
    }

    // 프로그래스 바 업데이트 함수
    function updateProgressBar(rewardAmount) {
        let maxAmount, progressColor;

        if (rewardAmount < 50000) {
            maxAmount = 50000;
            progressColor = "#55b1e4"; // 하늘색
        } else if (rewardAmount < 100000) {
            maxAmount = 100000;
            progressColor = "#6DBB4E"; // 녹색
        } else if (rewardAmount < 200000) {
            maxAmount = 200000;
            progressColor = "#F79304"; // 주황색
        } else {
            maxAmount = 200000;
            progressColor = "#8A2BE2"; // 빨강색
        }

        let percentage = (rewardAmount / maxAmount) * 100;
        if (percentage > 100) percentage = 100;

        $("#progress").css({
            "width": percentage + "%",
            "background-color": progressColor
        });
    }
});
