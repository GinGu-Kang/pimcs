let serialNumValidation;


/**
 * serial number input에서 focusout 발생시
 */
$(document).on("focusout","input[name='mat.serialNumber']",function(){
    let messageTag = $(this).next();

    if($(this).val() == ""){
        return {
            "message": "등록할 수 있는 시리얼번호 입니다.",
            "result":  false
        }
    }

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
 * "기기 등록하기"버튼 클릭시
 * ajax post요청으로 기기등록하고 등록한 기기를 registered-mat-container추가
 */
$(document).on("click",".register-btn",function(e){

    if($(`input[name='mat.serialNumber']`).val() == ""){
        alert("시리얼번호를 입력해주세요.");
        return;
    }
    if($(`input[name='mat.matLocation']`).val() == ""){
        alert("위치를 입력해주세요.");
        return;
    }
    if($(`input[name='mat.threshold']`).val() == ""){
        alert("임계값을 입력해주세요.");
        return;
    }
    if($(`input[name='mat.productOrderCnt']`).val() == ""){
        alert("상품주문갯수를 입력해주세요.");
        return;
    }
    if($(`input[name='mat.boxWeight']`).val() == ""){
        alert("박스무게를 입력해주세요.");
        return;
    }
    
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    let data = $("form.card-container").serialize();
    let resultData = loadPostData({
        url: "/mat/create",
        data: data,
        header: {
            'header': header,
            'token': token
        }
    });
    initFormInputValue();
    $(".registered-mat-container").prepend(resultData);
    alert("등록 되었습니다.")
});

/**
 * form input value 초기화
 */
const initFormInputValue = function(){
    serialNumValidation = undefined;
    $("form.card-container input").each(function(index, element){
        $(element).val("");
    });
    $(`input[name='mat.calcMethod']`).val(0);

    $(".input-message").css("display","none");

    $("select[name='productId'] option:eq(0)").prop("selected", true);

    $(".calc-method-btn").removeClass("active");
    $(".calc-method-btn").first().addClass("active");
}