const showItemsSelectbox = ".show-item-select";

/**
 * 세션스토리지 비우기 및 테이블데이터 초기화
 */
const init = function(){
    // 체크된 매트데이터 전체 삭제
    sessionStorage.clear();
    //display setting modal 초기화
    initDisplaySettingModal();
    

}


/**
 * 
 * @param mat 매트데이터
 * @param isChecked 매트가 체크박스를 통해 체크되었는 여부
 */
const setStorage = function({mat, isChecked}){
    mat['checked'] = isChecked;
    sessionStorage.setItem(mat.id,JSON.stringify(mat));
}
/**
 * 체크된 아이템들 찾기
 * @returns 체크된 아이템 list
 */
const getCheckedItems = function(){
    let checkedItems=[];
    for(let matId of Object.keys(sessionStorage)){
        let mat = getStorageItem(matId);
        if(mat['checked'])
            checkedItems.push(mat);
    }
    return checkedItems;
}

/**
 * 
 * @param columnName
 * @param replaceValue
 */
const updateMat = function({columnName, replaceValue}){

    //sessionStorage 데이터 업데이트
    for(let mat of getCheckedItems()){
        mat[columnName] = replaceValue;
        if(columnName == "product"){
            $(`tr[data='${mat.id}'] > td[columnname='productCode']`).text(mat[columnName].productCode);
            $(`tr[data='${mat.id}'] > td[columnname='productName']`).text(mat[columnName].productName);
            $(`tr[data='${mat.id}'] > td[columnname='productImagex']`).text(mat[columnName].productImage);
        }else{
            $(`tr[data='${mat.id}'] > td[columnname='${columnName}']`).text(mat[columnName]);
        }
        
        $(`.row-checked[data=${mat.id}]`).click();
        
        setStorage({
            mat: mat,
            isChecked: false
        });
    }
}

const getStorageItem = function(key){
    return JSON.parse(sessionStorage.getItem(key));
}


/**
 * twbsPagination 활성화
 * @param  totalPages 전체페이지
 * @param  currentPage 현재페이지
 * @param size 한페이지에 보여지는 개수
 */
const pagination = function({totalPages,currentPage,size}){
    $("#desktop-pagination").twbsPagination('destroy'); // 페이지네이션 객체를 ram에서 소멸
    $("#mobile-pagination").twbsPagination('destroy');
    //데스크탑용 페이지네이션 생성
    $("#desktop-pagination").twbsPagination({
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
                if($(".card-viewer-btn").hasClass("card-viewer-active")){
                    initCardView({page:page, size:size});
                }else{
                    initTableData({page:page, size:size});
                }
            }
        }
    });
    $("#mobile-pagination").twbsPagination({
        totalPages:totalPages,    
        visiblePages:3,    
        startPage: (currentPage != 0) ? currentPage : 1,
        initiateStartPageClick:false,
        prev:'<',
        next:'>',
        first:'',
        last:'',
        onPageClick: function (event, page) {
            if(currentPage != page){
                if($(".card-viewer-btn").hasClass("card-viewer-active")){
                    initCardView({page:page, size:size});
                }else{
                    initTableData({page:page, size:size});
                }
            }
        }
    });
}

/**
 *매트조회 테이블데이터 로드
 *@param  data 요청데이터 {page:1, size:10}
 */
 const loadTableData = function(data){
    let resultData = {};
    
    const url = "/api/mats"
    $.ajax({
        url: url,
        type:'GET',
        data:data,
        async: false, // 동기식으로 동작
        success:function(response){
            resultData = response;    
            //페이지네이션 생성 및 재생성
            pagination({
                totalPages: resultData.totalPages,
                currentPage: resultData.pageNumber,
                size: resultData.pageSize
            });
        },
        error:function(){
            alert("오류발생");
        }
    });
    return resultData;
}
/**
 * 테이블로 보기 클릭시
 * 1. card-viewer-btn에서 card-viewer-active 클래스 제거
 * 2. table-viewer-btn에 table-viewer-active 클래스 추가
 */
$(document).on("click",".table-viewer-btn",function(){
    //table보기버튼을 활성화, card뷰보기버튼을 비활성화
    $(".card-viewer-btn").removeClass("card-viewer-active");
    $(".table-viewer-btn").addClass("table-viewer-active");

    $(".pimcs-table").addClass("content-display");
    $(".pimcs-card-container").removeClass("content-display");
    initTableData({page:1, size:10});
});

/**
 * 카드뷰로 보기 클릭시
 */
$(document).on("click",".card-viewer-btn", function(){
    //table보기버튼을 비활성화, card뷰보기버튼을 활성화
    $(".table-viewer-btn").removeClass("table-viewer-active");
    $(".card-viewer-btn").addClass("card-viewer-active");

    $(".pimcs-table").removeClass("content-display");
    $(".pimcs-card-container").addClass("content-display");


    initCardView({page:1, size: 10})
});


$(document).on("mouseover",".modify-btn",function(){
    $(".modify-dropdown").css("display","block");
    $(this).addClass("hover");
});

$(document).on("mouseleave",".modify-btn",function(){
     $(".modify-dropdown").css("display","none");
     $(this).removeClass("hover");
});