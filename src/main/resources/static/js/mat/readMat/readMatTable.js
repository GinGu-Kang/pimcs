
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
    //매트 테이블 구조 변경
    modifyDisplayColumns();
}

/**
 * display setting에서 check된 컬럼만 보여주기
 */
const modifyDisplayColumns = function(){
    //thead th태그 생성
    $("thead").empty();
    let trs = `<tr class='text-size-middle'>`;
    trs += createColumnThTag();
    trs += `</tr>`;
    $("thead").append(trs);

    //tbody 보여질 컬럼 class에 display 추가
    $("tbody td").removeClass("display");
    $("#displaySettingModal input:checked").each(function(index, element){
        let columnName = $(element).attr('columnName');
        $(`tbody td[columnName='${columnName}']`).addClass("display");
    });
}

/**
 * thead th태그 생성
 * @returns th태그들을 반환
 */
const createColumnThTag = function(){
    let ths = "";
    ths += `<th><input class='all-rows-checked' type='checkbox'/></th>`
    $("#displaySettingModal input:checked").each(function(index, element){
        let elementId = $(element).attr("id");
        let columnText = $(`label[for='${elementId}']`).text();
        ths += `<th>${columnText}</th>`;
    });
    return ths;
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
        row +=    `<td columnName='serialNumber'>${mat.serialNumber}</td>`;
        row +=    `<td columnName='matVersion'>A3</td>`;
        row +=    `<td columnName='productCode'>${mat.product.productCode}</td>`;
        row +=    `<td columnName='productName'>${mat.product.productName}</td>`;
        row +=    `<td columnName='productImage'>${mat.product.productImage}</td>`;
        row +=    `<td columnName='inventoryCnt'>${mat.inventoryWeight}</td>`;
        row +=    `<td columnName='calcMethod'>${mat.calcMethod}</td>`;
        row +=    `<td class='blue' columnName='threshold'>${mat.threshold}</td>`;
        row +=    `<td class='blue' columnName='inventoryWeight'>${mat.inventoryWeight}</td>`;
        row +=    `<td columnName='matLocation'>${mat.matLocation}</td>`;
        row +=    `<td columnName='productOrderCnt'>${mat.productOrderCnt}</td>`;
        row +=    `<td columnName='boxWeight'>${mat.boxWeight}</td>`;
        row +=    `<td class='amountBattery' columnName='battery' data=${mat.battery}>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=          `<div class='battery-small-rect'></div>`;
        row +=     `<td>`;
        row += `</tr>`
       
    return row;
}

