document.querySelectorAll('.detail-link').forEach(link => {
    link.addEventListener('click', () => {
        const targetId = link.getAttribute('data-target');
        const targetBox = document.getElementById(targetId);

        if (targetBox.style.display === 'none') {
            targetBox.style.display = 'block';
            link.textContent = '접기'; // 버튼 텍스트 변경
        } else {
            targetBox.style.display = 'none';
            link.textContent = '자세히'; // 버튼 텍스트 복구
        }
    });
});
