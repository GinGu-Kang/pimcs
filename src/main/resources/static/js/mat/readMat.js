const showItemsSelectbox = ".show-item-select";

/**
 * 세션스토리지 비우기 및 테이블데이터 초기화
 */
const init = function(){

    sessionStorage.clear();
    initTableData({page:1, size:10});
}

/**
 * 테이블 데이터초기화
 * @param page 페이지번호
 * @param size 한페이지당 개수
 */
const initTableData = function({page,size}){
    
    //ajax로 mat테이블 데이터 로드
    let tableData = loadTableData({page: page, size: size});
    //행생성해서 tbody에 삽입
    createTableRow(tableData);
}

/**
 * 전체행체크시 이벤트 
 */
 
$(document).on("change",".all-rows-checked",function(){
    
    let isAllChecked = $(this).is(":checked")
    $(".row-checked").prop("checked", isAllChecked);

    $(".row-checked").each(function(index, element){
        let id = $(element).attr("data");
        let mat = getStorageItem(id);
        setStorage({mat: mat, isChecked: isAllChecked});
    });
});

/**
 * 체크박스 체크시 발생하는 이벤트
 */
$(document).on("change",".row-checked",function(){
    //전체checkbox가 체크되면 전체체크박스 활성화
    let showItemsCnt = parseInt($(showItemsSelectbox).children("option:selected").val());
    if($(".row-checked:checked").length == showItemsCnt)
        $(".all-rows-checked").prop("checked",true);
    else
        $(".all-rows-checked").prop("checked",false);

    let id = $(this).attr("data");
    let isChecked = $(this).is(":checked");
    let mat = getStorageItem(id);
    setStorage({mat:mat, isChecked: isChecked})

});

/**
 *  show item per page selectbox 선택시
 */
$(document).on("change", showItemsSelectbox, function(){
    let size = parseInt($(this).children("option:selected").val());
    initTableData({page: 1, size: size});
});

/**
 * 
 * @param mat 매트데이터
 * @param isChecked 매트가 체크박스를 통해 체크되었는 여부
 */
const setStorage = function({mat, isChecked}){
    mat['checked'] = isChecked;
    sessionStorage.setItem(mat.id,JSON.stringify(mat));
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
    $(".pagination").twbsPagination('destroy'); // 페이지네이션 객체를 ram에서 소멸
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
                console.log(page);
                initTableData({page:page, size:size});
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
        
        // 1~20 -> 1 , 21-40 -> 2, 41-60-> 3, 61-80 -> 4, 81-100 -> 5
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
 * 매트테이블 행생성
 * @param tableData Mat 및 page데이터
 */
const createTableRow = function(tableData){
    $("tbody").empty();
    let checkedCount = 0; // 몇개행이 체크되었는지
    for(let mat of tableData.data){
        let findMat = getStorageItem(mat.id);
        let isChecked = false;
        if(findMat != null){ // 로컬저장된 매트데이터가 있으면 저장된 check여부 적용
            isChecked = findMat.checked;
            if(findMat.checked) checkedCount += 1;
        }
        setStorage({mat:mat,isChecked: isChecked});
        $("tbody").append(createMatRow(mat));    
    }
    let showItemsCnt = parseInt($(showItemsSelectbox).children("option:selected").val());
    //보여지는 행수와 체크된 행수가 같으면 전체체크박스 활상화
    if(showItemsCnt == checkedCount){
        $(".all-rows-checked").prop("checked",true);
    }else{
        $(".all-rows-checked").prop("checked",false);
    }
    // 배터리양 그래프화
    amountBatteryToGraph();

}

const createMatRow = function(mat){
    const findMat = getStorageItem(mat.id);
    const checked = (findMat.checked) ? 'checked' : '';
    
    let row = `<tr class='text-size-between-middle-samll'>`;
        row +=    `<td><input class='row-checked' type='checkbox' data=${mat.id} ${checked}/></td>`;
        row +=    `<td>${mat.serialNumber}</td>`;
        row +=    `<td>A3</td>`;
        row +=    `<td>${mat.product.productName}</td>`;
        row +=    `<td class='blue'>${mat.threshold}</td>`;
        row +=    `<td class='blue'>${mat.inventoryWeight}</td>`;
        row +=    `<td>${mat.inventoryWeight}</td>`;
        row +=    `<td class='amountBattery' data=${mat.battery}>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=     `<td>`;
        row += `</tr>`
    return row;
}


