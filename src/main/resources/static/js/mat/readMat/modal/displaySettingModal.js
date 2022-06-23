
const COOKIE_NAME = "displaySetting";

//display setting 기본으로 체크되는 값
const displaySettingDefaultCheckedColumnNames = [
    "serialNumber", //시리얼 번호
    "matVersion",  // 기기 버전
    "productName", //제품명
    "productImage",
    "threshold", //임계값
    "inventoryCnt", // 현재재고
    "productWeight",
    "inventoryWeight",//현재무게
    "communicationStatus"
//    "battery", //잔여배터리

]

/**
 * display setting modal 초기화
 */
const initDisplaySettingModal = function(){
    let columnNames = [];
    let values = getCookie(COOKIE_NAME);
    
    if(values != null) columnNames = JSON.parse(values);
    else columnNames = displaySettingDefaultCheckedColumnNames;

    columnNames.forEach(function(element){
        console.log(element)
        $(`input[columnName=${element}]`).prop("checked",true);
    });
}

/**
 * display setting 보기항목 체크시
 */
$(document).on("change",".displaySettingCheckbox",function(){
    let columnNames = []

    $("#displaySettingModal input:checked").each(function(index, element){
        columnNames.push($(element).attr("columnName"));
    });

    setCookie(COOKIE_NAME,JSON.stringify(columnNames),7);
    modifyDisplayColumns();
});