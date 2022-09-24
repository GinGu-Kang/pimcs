//파라미터 값 가져오기
const searchParams = new URLSearchParams(location.search);

var keyword = searchParams.get('keyword');
var selectOption = searchParams.get('selectOption');
console.log(keyword)

currentPage = parseInt(currentPage) + 1;
$("#desktop-pagination").twbsPagination({
    totalPages:totalPage,
    visiblePages:size,
    startPage: currentPage,
    initiateStartPageClick:false,
    prev:'<',
    next:'>',
    first:'',
    last:'',
    onPageClick: function (event, page) {
        if (keyword!=null)
            location.href=`${location.pathname}?page=${page}&size=${size}&keyword=${keyword}&selectOption=${selectOption}`;
        else{
            location.href=`${location.pathname}?page=${page}&size=${size}`
        }
    }
});