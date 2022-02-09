

/**
 * twbsPagination 활성화
 * @param  totalPages 전체페이지
 * @param  currentPage 현재페이지
 * @param size 한페이지에 보여지는 개수
 */
const pagination = function({totalPages,currentPage,size}){
    
    $(".pagination").twbsPagination({
        totalPages:totalPages,    
        visiblePages:5,    
        startPage: (currentPage != 0) ? currentPage : 1,
        initiateStartPageClick:false,
        prev:'<',
        next:'>',
        first:'',
        last:'',
        onPageClick: function (event, page) {
            if(currentPage != page){
                window.location.href=`${location.pathname}?page=${page}&size=${size}`;
            }
        }
    });
}

/**
 * 배터리양을 그래프화 
 */
const amountBatteryToGraph = function(){

    $(".amountBattery").each(function(index, element){
        let amountBattery = parseInt($(element).attr("data"));
        
        // 1-20 , 21-40, 41-60, 61-80, 81-100
        if(amountBattery >= 81){
            $(element).children(".battery-small-rect").css("background"," #2dc300");
        }else if(amountBattery >= 61){
            $(element).children(".battery-small-rect").css("background","#ffd052");
            $(element).children(".battery-small-rect:last-child").css("background","#ebebeb");
        }else if(amountBattery >= 41){
            $(element).children(".battery-small-rect:nth-child(1)").css("background","#ffd052");
            $(element).children(".battery-small-rect:nth-child(2)").css("background","#ffd052");
            $(element).children(".battery-small-rect:nth-child(3)").css("background","#ffd052");
        }else if(amountBattery >= 21){
            $(element).children(".battery-small-rect:nth-child(1)").css("background","#ea2400");
            $(element).children(".battery-small-rect:nth-child(2)").css("background","#ea2400");

        }else if(amountBattery >= 1){
            $(element).children(".battery-small-rect:nth-child(1)").css("background","#ea2400");
        }

    });
}

const loadTableData = function(){

}


