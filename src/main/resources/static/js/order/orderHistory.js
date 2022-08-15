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
                const params =  new URLSearchParams(location.search);
                if(params.get("query") || params.get("startDate") || params.get("endDate")){
                    
                    let query = params.get('query');
                    let startDate = params.get('startDate');
                    let endDate = params.get('endDate');
                    location.href=`/order/history?page=${clickedPage}&size=${size}&query=${query}&startDate=${startDate}&endDate=${endDate}`;
                }else{
                    location.href=`/order/history?page=${clickedPage}&size=${size}`;
                }
            }
                
        }
    });
}


$(document).on("click","#order-inquiry-btn",function(e){
    e.preventDefault();

    if($("#start-date").val() != '' || $("#end-date").val() != ''){

        if($("#start-date").val() == ''){
            alert("시작날짜를 선택해주세요.");
            return false;
        }
        if($("#end-date").val() == ''){
            alert("종료날짜를 선택해주세요.");
            return false;
        }

        if($("#start-date").val() > $("#end-date").val()){
            alert("시작날짜가 종료날짜 보다 더 큽니다.");
            return false;
        }

    }

    $("#order-history-form").submit();

});


/**
 * csv다운로드 버튼 클릭시
 */
 $(document).on("click",".csv-download-btn",function(){
    let query = '';
    let startDate = '';
    let endDate = '';  

    if(location.search != ''){
        let params =  new URLSearchParams(location.search);
        query = params.get('query') == null ? '' : params.get('query');
        startDate = params.get('startDate') == null ? '' : params.get('startDate');
        endDate = params.get('endDate') == null ? '' : params.get('endDate');
        
        
    }
    location.href=`/order/csv/history?query=${query}&startDate=${startDate}&endDate=${endDate}`;
});