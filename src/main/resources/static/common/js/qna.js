
let list = []
let page = 1       // 페이지 번호
let limit = 5      // 한페이지에 출력될 데이터 갯수
const block = 5    // 한페이지에 출력될 페이지 갯수
let count = 0      // 전체 데이터 갯수

$(()=>{
    $.ajax({
        url : "boardList",
        type : "post",
        dataType : "json",

        success : (result) => {
            list = result
            pagingList(page, list)

            console.log(list)
        },
        error : ()=>{
            alert('게시글 목록 불러오기 실패!')
        }
    })
})

// JavaScript 에서 페이징 처리 하기
function pagingList(page, list){
    count = list.length        // 전체 게시글 갯수

    let maxPage = Math.ceil(count/limit);
    if(page > maxPage){
        page = maxPage
    }

    let startRow = (page - 1) * limit  // 0 5 10 ...
    let endRow = page * limit - 1      // 4 9 14 ...
    if(endRow >= count){
        endRow = count-1
    }

    let startPage = (Math.ceil(page/block)-1) * block + 1   // 1 1 1 1 1 6  6  6  6  6 ..
    let endPage = startPage + block - 1                     // 5 5 5 5 5 10 10 10 10 10
    if(endPage > maxPage){
        endPage = maxPage
    }

    let output = ""

    for(let i=startRow; i<=endRow; i++){

        output += `
        <tr>
        <td>${list[i].boardId}</td>
        <td><a href="/qnaview/${list[i].boardId}">${list[i].boardTitle}</a></td>
        <td>${list[i].nickname}</td>
        <td>${formatDate(list[i].boardCreatedAt)}</td>
        <td><span class="status pending">${list[i].boardAnswerStatus}</span></td>
        </tr>
        `;
    }

    $('tbody').empty()
    $('tbody').append(output)

    // numbering 페이징 처리
    let pageNum = "";

    // [이전] 버튼
    for(let i = startPage; i <= endPage; i++){
        if(page === i){
            pageNum += `<button class="active"> ${i} </button>`
        } else {
            pageNum += `<a href="#" data-page="${i}"> ${i} </a>`
        }
    }


    $('#numbering').empty()
    $('#numbering').append(pageNum)

    $(document).on("click", "#numbering a", function(e){
        e.preventDefault();
        page = parseInt($(this).data('page'))
        pagingList(page, list)
    })
    function formatDate(dateStr) {
        const dateObj = new Date(dateStr);
        const year = dateObj.getFullYear();
        // 월은 0부터 시작하므로 +1 후 두자리 형식으로 변환
        const month = ('0' + (dateObj.getMonth() + 1)).slice(-2);
        const day = ('0' + dateObj.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

}

// 게시글 갯수 변경
$('#limit').change(()=>{
    page = 1
    limit = parseInt($('#limit').val())
    pagingList(page, list)
})

// 검색
$('#searchBtn').click(()=>{
    let category = $('#searchCategory').val()
    let keyword = $('#searchKeyword').val()

    $.ajax({
        type: "POST",
        url: "searchList",
        data : { "searchCategory": category, "searchKeyword" : keyword },
        dataType: "json",
        success : function (result){
            list = result
            pagingList(page, list)
        },
        error : function(){
            alert('검색 목록 불러오기 실패!')
        }
    })

})