
const initDatePicker = function(){
    $('#start-date').datepicker({
        dateFormat: 'yy.mm.dd',
      });
      $('#end-date').datepicker({
        dateFormat: 'yy.mm.dd',
      });
}

const pagination = function({page,size,totalPages}){
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
            if(page != clickedPage)
                location.href=`/inout/history?page=${clickedPage}&size=${size}`;
        }
    });
}