// src/store/js/alarm.js

document.addEventListener("DOMContentLoaded", function () {
    console.log("Alarm.js loaded.");

    const notificationIcon = document.getElementById('notificationToggle');
    const notificationList = document.getElementById('sonNotificationList');

    // 알림 컨테이너 요소
    const notificationContainer = document.getElementById('storeOwnerNotification');
    if (!notificationContainer) {
        console.error("Notification container not found.");
        return;
    }
    console.log("Notification container found.");

    // 데이터 속성에서 사용자 정보 가져오기
    const userType = notificationContainer.getAttribute('data-user-type');
    const userId = notificationContainer.getAttribute('data-user-id');
    console.log(`User Type: ${userType}, User ID: ${userId}`);

    if (!userId) {
        console.error("pre_store_id not found in session.");
        return;
    }

    if (notificationIcon && notificationList) {
        notificationIcon.addEventListener('click', function () {
            const isVisible = notificationList.style.display === 'block';
            notificationList.style.display = isVisible ? 'none' : 'block';
        });
    } else {
        console.warn("Notification icon or list not found.");
    }

    // 초기 알림 목록 로드
    loadExistingNotifications(userType, userId);

    // SockJS/Stomp 연결
    if (typeof SockJS === 'undefined') {
        console.error("SockJS is not loaded.");
        return;
    }
    if (typeof Stomp === 'undefined') {
        console.error("Stomp.js is not loaded.");
        return;
    }
    const socket = new SockJS('/ws-notifications');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        const destination = `/topic/notifications/${userType}/${userId}`;
        stompClient.subscribe(destination, function (notification) {
            console.log("Received notification via WebSocket:", notification);
            try {
                const notificationData = JSON.parse(notification.body);
                displayNotification(notificationData); // ★ 여기서 바로 'displayNotification' 호출
            } catch (e) {
                console.error("Error parsing notification data:", e);
            }
        });
    }, function (error) {
        console.error("WebSocket connection error:", error);
    });

    /**
     * 1) 기존 알림들(안 읽은 것 등)을 초기 로딩
     */
    function loadExistingNotifications(recipientType, recipientId) {
        console.log(`Fetching existing notifications for ${recipientType} with ID ${recipientId}`);
        fetch(`/api/notifications/${recipientType}/${recipientId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Existing notifications fetched:", data);
                if (data.length > 0) {
                    data.forEach(notification => {
                        displayNotification(notification);
                    });
                    // 알림 컨테이너 영역 보이기
                    notificationContainer.parentElement.style.display = 'block';
                } else {
                    console.log("No notifications to display.");
                }
            })
            .catch(error => {
                console.error("Error fetching existing notifications:", error);
            });
    }

    /**
     * 2) 알림 하나를 화면에 표시 (★ 목록 + 토스트)
     */
    function displayNotification(notification) {
        console.log("Displaying notification:", notification);

        // (A) 알림 목록에 추가
        const notificationList = document.getElementById('sonNotificationList');
        if (!notificationList) {
            console.warn("No #sonNotificationList element found. Skipping list append.");
        } else {
            const listItem = document.createElement('li');
            listItem.className = 'son_notification_item';
            listItem.setAttribute('data-notification-id', notification.id);

            // 읽음 상태라면 클래스로 표시
            if (notification.status === "읽음") {
                listItem.classList.add('read');
            }

            listItem.innerHTML = `
                <div class="son-message">${notification.category}: ${notification.message}</div>
                <div class="son-timestamp">
                    ${new Date(notification.createdAt).toLocaleString()}
                </div>
            `;

            // 클릭 시 '읽음' 처리
            listItem.addEventListener('click', function () {
                markAsRead(notification.id, listItem);
                // 추가 로직 (예: 모달 열기)
                alert(`알림 상세: ${notification.message}`);
            });

            // 새 알림을 상단에 추가
            notificationList.prepend(listItem);

            // 알림 컨테이너 보이기
            notificationContainer.parentElement.style.display = 'block';
        }

        // (B) 하단 토스트 알림 (3초 후 사라짐)
        showToast(notification.message);
    }

    /**
     * 3) 알림을 읽음 처리하는 함수 (PUT /api/notifications/{id}/read)
     */
    function markAsRead(notificationId, listItem) {
        console.log(`Marking notification ID ${notificationId} as read.`);
        fetch(`/api/notifications/${notificationId}/read`, {method: 'PUT'})
            .then(response => {
                if (response.ok) {
                    console.log(`Notification ID ${notificationId} marked as read.`);
                    listItem.classList.add('read');
                } else {
                    console.error("Failed to mark notification as read.");
                }
            })
            .catch(error => {
                console.error("Error marking notification as read:", error);
            });
    }

    /**
     * 4) 토스트 표시 함수
     * @param {String} msg - 예: "회원 이상혁님의 새로운 주문이 들어왔습니다."
     */
    function showToast(msg) {
        const toastContainer = document.getElementById("toastContainer");
        if (!toastContainer) {
            console.warn("toastContainer not found! Cannot display toast.");
            return;
        }
        // 토스트 영역 보이기
        toastContainer.style.display = "block";

        // 토스트 아이템 생성 (div)
        const toast = document.createElement("div");
        toast.className = "toast-item";

        // 인라인 SVG 예시 (원하시는 로고로 교체 가능)
        const svgLogo = `
      <svg class="toast-logo" viewBox="0 0 24 24" fill="#fff" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10"/>
          <!-- 그냥 예시로 동그라미 -->
      </svg>
    `;

        // 최종 HTML 구조
        toast.innerHTML = `
      <div class="toast-header">
        ${svgLogo}
        <span class="toast-name">味추홀</span>
      </div>
      <hr class="toast-hr">
      <div class="toast-message">${msg}</div>
    `;

        // 디버그 출력
        console.log("[Toast] Creating toast with message:", msg);

        // 컨테이너에 추가
        toastContainer.appendChild(toast);

        // 5초 후 제거 (fadeout 포함)
        setTimeout(() => {
            toast.remove();
            // 남은 토스트가 없으면 영역 숨김
            if (!toastContainer.hasChildNodes()) {
                toastContainer.style.display = "none";
            }
        }, 5000);
    }

});
