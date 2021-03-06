
const initDatePicker = function(){
    $('#start-date').datepicker({
        dateFormat: 'yy.mm.dd',
      });
      $('#end-date').datepicker({
        dateFormat: 'yy.mm.dd',
      });
}

const pagination = function({page,size,totalPages}){
    $("#desktop-pagination").twbsPagination('destroy'); // 페이지네이션 객체를 ram에서 소멸
    $("#mobile-pagination").twbsPagination('destroy');


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
                if(location.pathname == "/inout/history/search"){
                    let params =  new URLSearchParams(location.search);
                    let query = params.get('query');
                    let startDate = params.get('startDate');
                    let endDate = params.get('endDate');
                    location.href=`/inout/history/search?page=${clickedPage}&size=${size}&query=${query}&startDate=${startDate}&endDate=${endDate}`;
                }else{
                    location.href=`/inout/history?page=${clickedPage}&size=${size}`;
                }
            }
                
        }
    });

    $("#mobile-pagination").twbsPagination({
        totalPages: parseInt(totalPages),    
        visiblePages: 3,    
        startPage: parseInt(page),
        initiateStartPageClick:false,
        prev:'<',
        next:'>',
        first:'',
        last:'',
        onPageClick: function (event, clickedPage) {
            if(page != clickedPage){
                if(location.pathname == "/inout/history/search"){
                    let params =  new URLSearchParams(location.search);
                    let query = params.get('query');
                    let startDate = params.get('startDate');
                    let endDate = params.get('endDate');
                    location.href=`/inout/history/search?page=${clickedPage}&size=${size}&query=${query}&startDate=${startDate}&endDate=${endDate}`;
                }else{
                    location.href=`/inout/history?page=${clickedPage}&size=${size}`;
                }
            }
                
        }
    });
}

const initShowItems = function(){
    if(location.search != ""){
        let params =  new URLSearchParams(location.search);
        $('.show-item-select').val(params.get('size')).prop("selected",true);
        $('.mobile-pagination-container .show-item-select').val(params.get('size')).prop("selected",true);
    }
}

/**
 * show items per page 클릭시
 */
$(document).on("change",".show-item-select",function(){
    let params =  new URLSearchParams(location.search);
    let size = $(this).children("option:selected").val();

    if(location.pathname == "/inout/history/search"){
        let query = params.get('query');
        let startDate = params.get('startDate');
        let endDate = params.get('endDate');    
        location.href=`/inout/history/search?page=${1}&size=${size}&query=${query}&startDate=${startDate}&endDate=${endDate}`;
    }else{
        location.href=`/inout/history?page=${1}&size=${size}`;
    }
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
        query = params.get('query');
        startDate = params.get('startDate');
        endDate = params.get('endDate');    
        
        
    }
    location.href=`/download/inout/history/csv?query=${query}&startDate=${startDate}&endDate=${endDate}`;
});