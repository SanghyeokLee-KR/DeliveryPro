function openAdCreateModal() {
    console.log("openAdCreateModal called");
    const modal = document.getElementById('advCreateModal');
    modal.classList.add('active');
}
function closeAdCreateModal() {
    const modal = document.getElementById('advCreateModal');
    modal.classList.remove('active');
}

// 수정 모달
function openAdEditModal(advId) {
    console.log("openAdEditModal called, advId=", advId);
    const modal = document.getElementById('advEditModal');
    modal.classList.add('active');

    // 광고 가져오기
    fetch('/admin/advertisements/' + advId)
        .then(res => res.json())
        .then(data => {
            document.getElementById('editAdvId').value = data.advId;
            document.getElementById('editAdvTitle').value = data.advTitle;
            document.getElementById('editAdvImageUrl').value = data.advImageUrl || '';
        })
        .catch(err => console.error(err));
}
function closeAdEditModal() {
    const modal = document.getElementById('advEditModal');
    modal.classList.remove('active');
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const createModal = document.getElementById('advCreateModal');
    const editModal = document.getElementById('advEditModal');
    if (event.target === createModal) {
        createModal.classList.remove('active');
    }
    if (event.target === editModal) {
        editModal.classList.remove('active');
    }
}
