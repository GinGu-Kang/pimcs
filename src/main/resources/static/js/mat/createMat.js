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
    
    if(validation.valid) 
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
    let data = $(this).attr("data");
    $("input[name='mat.calcMethod']").val(data);
    $(this).addClass("active");

    $(".threshold-unit").text(
        (data == 0) ? "g" : "개"
    );
});



/**
 * 시리얼 번호 체크 
 * @param {*} serialNum 
 * @returns ex) {"result": true, "message":"사용가능한 시리얼번호"}
 */
const checkSerialNumber = function(serialNum){
    return loadGetData({
        url: `/api/mats/${serialNum}/validations`,
        data: {}
    });
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

    const mailRecipientsStr = $(`input[name='mailRecipients']`).val();
    if(mailRecipientsStr == ""){
        alert("주문이메일을 주소를 입력해주세요.");
        return;
    }
    const regEmail = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/;
    
    const mailRecipients = mailRecipientsStr.split(",");
    for(const email of mailRecipients){
        if(email.trim() == ""){
            continue;
        }
        if(!regEmail.test(email.trim())){
            alert(`'${email}' 형식에 맞지않는 이메일입니다.`);
            return;
        }
    }

    
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    const orderMailList = []

    for(let i=0; i<mailRecipients.length; i++){
        orderMailList.push(mailRecipients[i].trim());
    }
    const saveMat = {
        "serialNumber": $(`input[name='mat.serialNumber']`).val(),
        "matLocation": $(`input[name='mat.matLocation']`).val(),
        "threshold" : $(`input[name='mat.threshold']`).val(),
        "boxWeight": $(`input[name='mat.boxWeight']`).val(),
        "calcMethod": $(`input[name='mat.calcMethod']`).val(),
        "productOrderCnt": $(`input[name='mat.productOrderCnt']`).val()
    }

    
    let resultData = loadPostData({
        url: "/mats",
        data: JSON.stringify({
            "mat": saveMat,   
            "productId": $("select[name='productId']").val(),
            "mailRecipients": orderMailList
        }),
        contentType: "application/json",
        header: {
            'header': header,
            'token': token
        }
    });
    initFormInputValue();
    $(".registered-mat-container").prepend(resultData);
    if(resultData != undefined){
        alert("등록 되었습니다.")
    }
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

//    $(".calc-method-btn").removeClass("active");
//    $(".calc-method-btn").first().addClass("active");

    $(".calc-method-btn").first().click();
}



$(document).on("click",".measurement-btn",function(){
    const defaultVal = $("input[name='mat.serialNumber']").val();
    const serialNumber = prompt("매트 시리얼번호", defaultVal);
    if(serialNumber == ''){
        alert('매트 시리얼번호를 입력해주세요.');
        return;
    }
    const data = loadGetData({
        url: `/api/devices/${serialNumber}`,
        data:{}
    });
    
    console.log(data);
    if(!data){
        alert("박스를 매트에 올려주세요.");
        return;
    }
    $("input[name='mat.boxWeight']").val(data.inventoryWeight);
});