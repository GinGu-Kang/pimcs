

/**
 * 테이블 데이터초기화
 * @param page 페이지번호
 * @param size 한페이지당 개수
 */
 const initTableData = function({page,size}){
    
    //ajax로 mat테이블 데이터 로드
    let tableData = loadTableData({page: page, size: size});
    console.log(tableData);
    $(".inventory-cnt-viewer").text(0);
    //행생성해서 tbody에 삽입
    createTableRow(tableData);
    //매트 테이블 구조 변경
    modifyDisplayColumns();
}

/**
 * display setting에서 check된 컬럼만 보여주기
 */
const modifyDisplayColumns = function(){
    let checked = ($(".all-rows-checked").is(":checked")) ? "checked" : "";
    //thead th태그 생성
    $("thead").empty();
    let trs = `<tr class='text-size-middle'>`;
    trs += createColumnThTag(checked);
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
const createColumnThTag = function(checked){
    let ths = "";
    ths += `<th><input class='all-rows-checked' ${checked} type='checkbox'/></th>`
    $("#displaySettingModal input:checked").each(function(index, element){
        let elementId = $(element).attr("id");
        let columnText = $(`label[for='${elementId}']`).text();
        ths += `<th>${columnText}</th>`;
    });
    return ths;
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
 * 매트테이블 행생성
 * @param tableData Mat 및 page데이터
 */
 const createTableRow = function(tableData){
    $("caption").empty();
    $("tbody").empty();
  
    for(let mat of tableData.data){
        let findMat = getStorageItem(mat.id);
        let isChecked = false;
        if($(".all-rows-checked").is(":checked")){
            isChecked = true;
        }else if(findMat != null){ // 로컬저장된 매트데이터가 있으면 저장된 check여부 적용
            isChecked = findMat.checked;
        }
        setStorage({mat:mat,isChecked: isChecked});
        $("tbody").append(createMatRow(mat, tableData.data.length));    
    }
}

const createMatRow = function(mat, length){
    const findMat = getStorageItem(mat.id);
    let  checked;
    let calcMethod;
    let currentInventory;
    let threshold;
    let importantInfoColor = "blue";

    if($(".all-rows-checked").is(":checked")){ //전체체크가 되어있으면
        checked = "checked";
    }else{
       checked = (findMat.checked) ? 'checked' : '';
    }
    
    if(mat.calcMethod == 0) {
        calcMethod = "무게(g)";
        currentInventory = mat.currentInventory+"g";
        threshold = mat.threshold +"g";
    }else{
        calcMethod = "갯수(개)";
        currentInventory = mat.currentInventory+"개";
        threshold = mat.threshold +"개";
    } 

    if(mat.threshold > mat.currentInventory){
        importantInfoColor = "red";
    }

    let bigImgClass = 'td-big-image';
    if(length >= 10 && length-4 <= $("tbody > tr").length){
        bigImgClass = 'td-big-image-last';
    }

    let communicationStatus = `<i class="bi bi-wifi" style="font-size:20px; color:#4282ff"></i>`
    if(mat.communicationStatus == 0){
        communicationStatus = `<i class="bi bi-wifi-off" style="font-size:20px; color:red"></i>`
    }

    console.log("----")
    console.log()

    let row = `<tr class='text-size-between-middle-samll' data=${mat.id}>`;
        row +=    `<td><input class='row-checked' type='checkbox' data=${mat.id} ${checked}/></td>`;
        row +=    `<td columnName='serialNumber'>${mat.serialNumber}</td>`;
        row +=    `<td columnName='matVersion'>A3</td>`;
        row +=    `<td columnName='productCode'>${(mat.product != null ? mat.product.productCode : 'N/A')}</td>`;
        row +=    `<td columnName='productName'>${(mat.product != null ? mat.product.productName : 'N/A')}</td>`;
        row +=    `<td columnName='productImage'>`;
        row +=       `<img class='td-small-image' src='${mat.product != null ? mat.product.productImage : null}'/>`;
        row +=        `<img class='${bigImgClass}' src='${mat.product != null ? mat.product.productImage : null}'/>`;
        row +=    `</td>`;

        row +=    `<td class='${importantInfoColor}' columnName='inventoryCnt'>${currentInventory}</td>`;
        row +=    `<td class='${importantInfoColor}' columnName='threshold'>${threshold}</td>`;
        row +=    `<td columnName='calcMethod'>${calcMethod}</td>`;
        row +=    `<td columnName='productWeight'>${(mat.product != null ? mat.product.productWeight+"g" : 'N/A')}</td>`
        row +=    `<td  columnName='inventoryWeight'>${mat.inventoryWeight}g</td>`;
        row +=    `<td columnName='matLocation'>${mat.matLocation}</td>`;
        row +=    `<td columnName='productOrderCnt'>${mat.productOrderCnt}</td>`;
        row +=    `<td columnName='boxWeight'>${mat.boxWeight}</td>`;
        row +=    `<td columnName='communicationStatus'>${communicationStatus}</td>`

        row +=    `<td columnName='mailRecipients'>${getArrayToString(mat.orderMailRecipients)}</td>`
       
    return row;
}

const getArrayToString = (array)=>{
    for(const o of array){
        return o.mailRecipients.toString();
    }
    return "N/A";
}


/**
* 전체행체크시 이벤트 
*/
$(document).on("change",".all-rows-checked",function(){
   
    let isAllChecked = $(this).is(":checked")
    $(".row-checked").prop("checked", isAllChecked);
 
    if(isAllChecked == true){
        loadMatAll();
    }else{
         let checkedList = getCheckedItems();
         checkedList.forEach(function(mat){
                 setStorage({mat: mat, isChecked: false});
         });
    }
    let inventoryCnt = getCheckedItems().length;
    $(".inventory-cnt-viewer").text(formatKoKr(inventoryCnt));
 });
 
 
 /**
 * 체크박스 체크시 발생하는 이벤트
 */
 $(document).on("change",".row-checked",function(){
    
    
    let id = $(this).attr("data");
    let isChecked = $(this).is(":checked");
    let mat = getStorageItem(id);
    setStorage({mat:mat, isChecked: isChecked})
    let checkedCnt = getCheckedItems().length;
    if(checkedCnt == sessionStorage.length)
        $(".all-rows-checked").prop("checked",true);
    else
        $(".all-rows-checked").prop("checked",false);
     
     $(".inventory-cnt-viewer").text(formatKoKr(checkedCnt));
 });