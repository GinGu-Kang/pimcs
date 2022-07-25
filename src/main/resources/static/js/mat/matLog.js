const pagination = function({page,size,totalPages}){
    $("#desktop-pagination").twbsPagination('destroy'); // 페이지네이션 객체를 ram에서 소멸
    
    $("#desktop-pagination").twbsPagination({
        totalPages: parseInt(totalPages),    
        visiblePages: 5,    
        startPage: parseInt(page),
        initiateStartPageClick:false,
        prev:'<',
        next:'>',
        first:'',
        last:'',
        onPageClick: function (event, clickedPage) {
            if(page != clickedPage){
                
                if(location.pathname == "/mat/log/search"){
                    let params =  new URLSearchParams(location.search);
                    let query = params.get('query');
                    let startDate = params.get('startDate');
                    let endDate = params.get('endDate');
                    location.href=`/mat/log/search?page=${clickedPage}&query=${query}&startDate=${startDate}&endDate=${endDate}`;
                }else{
                    location.href=`/mat/log?page=${clickedPage}`;
                }

            }
                
        }
    });
}
