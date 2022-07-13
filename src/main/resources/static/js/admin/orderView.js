
$("#orderCnt").text(totalCnt);
for(let i = 0; i < totalCnt; i++){
    $("#self-input-item").append($('<input th:type="text" class="form-control" placeholder="시리얼을 입력하세요">'));
}

$("#depositComplete, #depositIncomplete").on("click", function(){
    switch($(this).text()) {
        case "예":
            depositChange({isDeposit:true})
            break;
        case "아니오":
            depositChange({isDeposit:false})
            break;
    }
});

function depositChange({isDeposit}){

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");


    $.ajax({
        url:matOrderId,
        type:'put',
        data:{isDeposit:isDeposit},
        beforeSend : function(xhr)
        {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
            xhr.setRequestHeader(header, token);
        },
        success:function(){
            alert("변경되었습니다.")
            location.reload();
        },
        error:function(){
            alert("에러입니다");
        }
    });
};
function deliverySave(){
    let result = confirm("배송된 기기의 시리얼이 일치 합니까?");
    let duplicateCheck = 0;
    if(result){
        if(duplicateCheck){
            alert("저장되었습니다.");
        }else {
            alert("중복된 기기가 존재합니다.(WS@)!@))");
        }

        //  orderview로 새로고침 이후 맨 밑에 배송된 기기들 보여주기 이메일 전송하기
    } else {
        alert("취소되었습니다.");
    }

}



function inputFormChange(){
    let changeButton=$("#form-change-button")
    if(changeButton.text()=="CSV 입력"){
        $("#self-input").css("display","none")
        $("#csv-input").css("display","block")
        changeButton.text("직접 입력")
    }
    else{
        $("#csv-input").css("display","none")
        $("#self-input").css("display","block")
        changeButton.text("CSV 입력")
    }


}
