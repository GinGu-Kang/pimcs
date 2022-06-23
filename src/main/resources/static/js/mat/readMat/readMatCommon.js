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

const initSearchMatForm = function(){
    const params = isSearchMat();

    if(params != null){
        $("select[name='searchType']").val(params['searchType']).attr("selected", "selected");
        $("input[name='searchQuery']").val(params['searchQuery']);
    }

}



/**
 * 검색 종류 select 변경시
 * @param {*} select  select 태그
 */
const changeSearchType = function(select){
    
    let selectedValue = $(select).children("option:selected").val();

    switch(selectedValue){
        case "serialNumber":
            $("input[name='searchQuery']").attr("placeholder","Search by serial number");
            break;
        case "matLocation":
            $("input[name='searchQuery']").attr("placeholder","Search by mat location");
            break;
        case "productCode":
            $("input[name='searchQuery']").attr("placeholder","Search by product code");
            break;
        case "productName":
            $("input[name='searchQuery']").attr("placeholder","Search by product name");
            break;
        case "matVersion":
            $("input[name='searchQuery']").attr("placeholder","Search by mat version");
            break;

    }
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

const getProductAttribute= function(mat, attrName){
    if(mat.product != null){
        return mat.product[attrName];
    }else{
        
        return (attrName == 'productImage') ? null: 'N/A';
    }
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
            $(`tr[data='${mat.id}'] > td[columnname='productImage'] > img`).attr("src",mat[columnName].productImage);
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
* 전체데이터 로드해서 sessionStorage에 저장
*/
const loadMatAll = function(){

    let totalPages = $("#desktop-pagination").data().twbsPagination.options.totalPages;
    let pageSize   = $(".show-item-select option:selected").val();
    totalPages = parseInt(totalPages);
    pageSize = parseInt(pageSize);

    let resultData = loadGetData({
        url: "/api/page/mats",
        data: {
            "page": 1,
            "size": pageSize * totalPages
        }
    });
    
    if(resultData != undefined){
        for(let mat of resultData.data){

                setStorage({mat:mat,isChecked: true});
        }
    }
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
    if(totalPages == 0 || currentPage == 0) return;

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
 * 검색 인지
 */
const isSearchMat = function(){
    let params = new URLSearchParams(location.search);
    if(params.get('searchType') != null && params.get('searchQuery') != null){
        return {
            "searchType": params.get('searchType'),
            "searchQuery": params.get('searchQuery')
         };
    }
    return null;
}

const isBelowThreshold = function(){
    let params = new URLSearchParams(location.search);
    if(params.get('belowThreshold') && params.get('belowThreshold') == 'true'){
        return true;
    }
    return false;
}

/**
 *매트조회 테이블데이터 로드
 *@param  data 요청데이터 {page:1, size:10}
 */
 const loadTableData = function(data){
    let resultData = {};
    let loadDataAPIUrl = "/api/page/mats";
    if(isSearchMat() != null){
        loadDataAPIUrl = "/api/search/mats" + location.search;
    }else if(isBelowThreshold()){
        loadDataAPIUrl = "/api/below/threshold/mats";
    }
    
    $.ajax({
        url: loadDataAPIUrl,
        type:'GET',
        data:data,
        async: false, // 동기식으로 동작
        success:function(response){
            resultData = response;    
            
            console.log(resultData);
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

$(document).on("mouseover",".mobile-modify-btn",function(){
    $(".mobile-modify-dropdown").css("display","block");
    $(this).addClass("hover");
});

$(document).on("mouseleave",".modify-btn",function(){
     $(".modify-dropdown").css("display","none");
     $(this).removeClass("hover");
});
$(document).on("mouseleave",".mobile-modify-btn",function(){
    $(".mobile-modify-dropdown").css("display","none");
    $(this).removeClass("hover");
});

$(document).on("mouseover",".td-small-image",function(){
    // $(this).addClass("hover");
    $(this).next().css("display","block");
    
});

$(document).on("mouseleave",".td-small-image",function(){
    console.log("mouse");
    // $(this).removeClass("hover");
    $(this).next().css("display","none");
    

});

/**
* dictionary to query string
*/
const serialize = function(obj) {
  var str = [];
  for (var p in obj)
    if (obj.hasOwnProperty(p)) {
      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
    }
  return str.join("&");
}

/**
* 다운로드 csv버튼 클릭시
*/
$(document).on("click",".download-csv-btn",function(){
    $("#csv-download-form-data").empty();
    let checkedItems = getCheckedItems();
    if(checkedItems.length == 0){
        alert("매트를 선택해주세요.");
        return;
    }



    let checkedColumnName = {};
    let checkedMatId = {};
    $("#displaySettingModal input[type='checkbox']:checked").each(function(index, element){
        checkedColumnName[`checkedColumnNames[${index}]`] = $(element).attr('columnname');
        $("#csv-download-form-data").append(`<input type="hidden" name="checkedColumnNames[${index}]" value="${$(element).attr('columnname')}"/>`);
    });

    if($(".all-rows-checked").is(":checked")){
      $("#csv-download-form").submit();
    }else{
        
         for(let i=0; i<checkedItems.length; i++){
              let mat = checkedItems[i];
               $("#csv-download-form-data").append(`<input type="hidden" name="checkedMatId[${i}]" value="${mat['id']}"/>`);
         }
         $("#csv-download-form").submit();
    }

});

/**
 * 그래프버튼 클릭시
 */
$(document).on("click",".graph-btn",function(){
    let checkedItems = getCheckedItems();
    if(checkedItems.length == 0){
        alert("매트를 선택해주세요.");
        return;
    }
    for(let i=0; i<checkedItems.length; i++){
        let mat = checkedItems[i];
        $("#graphForm").append(`<input type="hidden" name="serialNumberList[${i}]" value="${mat['serialNumber']}"/>`);
        $("#graphForm").append(`<input type="hidden" name="productNameList[${i}]" value="${mat['product']['productName']}"/>`);
    }
    
    $("#graphForm").submit();

});


/**
 *  show item per page selectbox 선택시
 */
 $(document).on("change", showItemsSelectbox, function(){
    let size = parseInt($(this).children("option:selected").val());
    if($(".card-viewer-btn").hasClass("card-viewer-active")){
        initCardView({page:1, size:size});
    }else{
        initTableData({page:1, size:size});
    }
    
});

/**
 * 전체행체크시 이벤트 
 */
 $(document).on("change",".all-rows-checked",function(){
    
    let isAllChecked = $(this).is(":checked")
    $(".row-checked").prop("checked", isAllChecked);

    let checkedList = getCheckedItems();
    checkedList.forEach(function(mat){
          setStorage({mat: mat, isChecked: isAllChecked});
    });
});


/**
 * 체크박스 체크시 발생하는 이벤트
 */
 $(document).on("change",".row-checked",function(){
    //전체checkbox가 체크되면 전체체크박스 활성화
    // let showItemsCnt = parseInt($(showItemsSelectbox).children("option:selected").val());
    
    let id = $(this).attr("data");
    let isChecked = $(this).is(":checked");
    let mat = getStorageItem(id);
    setStorage({mat:mat, isChecked: isChecked})

    if(getCheckedItems().length == sessionStorage.length)
        $(".all-rows-checked").prop("checked",true);
    else
        $(".all-rows-checked").prop("checked",false);
});


/**
 * 매트 삭제버튼 클릭시
 */
$(document).on("click",".delete-btn",function(){
    if(getCheckedItems().length == 0){
        alert("매트를 체크해주세요.");
        return;
    }
    if(!confirm(`${getCheckedItems().length}개 매트를 삭제하겠습니까?`)) return;
    
    let formData = getDeleteMatForm();
    let queryString = new URLSearchParams(formData).toString();
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    let resultData = loadPostData({
        url: "/mat/delete",
        data: queryString,
        header: {
            'header': header,
            'token': token
        }
    });

    if(resultData != undefined){
        alert(resultData.message);
        let pageSize   = $(".show-item-select option:selected").val();
        let currentPage = $("#desktop-pagination").data().twbsPagination.options.startPage;
        if($(".card-viewer-btn").hasClass("card-viewer-active")){
            initCardView({page:currentPage, size:pageSize});
        }else{
            initTableData({page:currentPage, size:pageSize});
        }
    }else{
        alert("삭제실패 했습니다.")
    }

});

/**
 * 체크된 데이로 FormData생성
 */
const getDeleteMatForm = function(){
    let formData = new FormData();
    if($(".all-rows-checked").is(":checked")){
        loadMatAll();
    }
    let checkedItems = getCheckedItems();
    for(let i=0; i<checkedItems.length; i++){
        let mat = checkedItems[i];
        formData.append(`matForms[${i}].mat.id`, mat.id);
    }
    return formData;
}