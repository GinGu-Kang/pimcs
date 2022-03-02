let serialNumValidation;


/**
 * serial number input에서 focusout 발생시
 */
$(document).on("focusout","input[name='mat.serialNumber']",function(){
    let messageTag = $(this).next();
    let validation = checkSerialNumber($(this).val());

    if(validation.result) 
        $(messageTag).css("color","#6A82FB");
    else
        $(messageTag).css("color","#ff003e");    
    
    $(messageTag).text(validation.message);
    $(messageTag).css("display","block");
    
});

/**
 * 계산방식 버튼 클릭시
 */
$(document).on("click",".calc-method-btn",function(){
    $(".calc-method-btn").removeClass("active");
    $("input[name='mat.calcMethod']").val($(this).attr("data"));
    $(this).addClass("active");
});



/**
 * 시리얼 번호 체크 
 * @param {*} serialNum 
 * @returns ex) {"result": true, "message":"사용가능한 시리얼번호"}
 */
const checkSerialNumber = function(serialNum){
    
    if(serialNumValidation == null){
        let response = loadGetData({
                            url: "/mat/check/serialNum",
                            data: {"serialNum": serialNum}
                        });
        serialNumValidation = response;
    }
    return serialNumValidation;
}

/**
 * form 전송시 유효성 검사
 * 1. serial number
 * @returns 오류없으면 true, 있으면 false
 */
const isValid = function(){
    let serialNumResult = checkSerialNumber($("input[name='mat.serialNumber']").val());
    
    if(!serialNumResult.result){
        alert(serialNumResult.message)
        return false;
    }
    
    return true;
}