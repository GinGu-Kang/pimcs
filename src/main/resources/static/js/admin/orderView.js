
let token = $("meta[name='_csrf']").attr("content");
let header = $("meta[name='_csrf_header']").attr("content");
$(".orderCnt").text(totalCnt);

//저장시 유효성 검사
function deliverySave(){
    let result = confirm("배송된 기기의 시리얼이 일치 합니까?");
    var fileValue = $("input[name='input-device-item']").length;
    var deviceSerialList = new Array(fileValue);

    for(var i=0; i<fileValue; i++){
        if($("input[name='input-device-item']")[i].value!=""){
            deviceSerialList[i] = $("input[name='input-device-item']")[i].value;
        }else{
            return alert("빈칸을 입력할 수 없습니다.")
        }
    }

    if(result){
        console.log(typeof deviceSerialList)
        $.ajax({
            url:ownDeviceUrl,
            type:'post',
            data:{deviceSerialList:deviceSerialList,
                companyId:companyId},
            beforeSend : function(xhr)
            {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            },
            success:function(resultMap){

                if(resultMap['isError']=="true"){
                    alert(resultMap['message']);
                }else{
                    alert(resultMap['message']);
                    location.reload();
                }
            },
            error:function(){
                alert("에러입니다");
            }
        });
         // orderview로 새로고침 이후 맨 밑에 배송된 기기들 보여주기 이메일 전송하기
    } else {
        alert("취소되었습니다.");
    }
}

//모달 직접입력 창 밑 입력된 기기 시리얼 input 창 동적생성
for(let i = 0; i < totalCnt; i++){
    // $("#self-input-item").append($('<span>'+(i+1)+'번 </span><input  id=self-device-'+i+' th:type="text" class="form-control self-device-item" placeholder="시리얼을 입력하세요">'));
    $("#input-device").append($('<span>'+(i+1)+'번 </span> <input id=input-device-'+i+' name="input-device-item" th:type="text" class="form-control input-device-item" placeholder="시리얼을 입력하세요" required>'));
}
// //직접입력시 입력된 기기 input에 serial 입력
// $(".self-device-item").keyup(function (){
//     let inputDeviceId=$(this).prop("id").replace("self-device-","#input-device-");
//     $(inputDeviceId).val($(this).val());
// })

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




// function inputFormChange(){
//     let changeButton=$("#form-change-button")
//     if(changeButton.text()=="CSV 입력"){
//         $("#self-input").css("display","none")
//         $("#csv-input").css("display","block")
//         changeButton.text("직접 입력")
//     }
//     else{
//         $("#csv-input").css("display","none")
//         $("#self-input").css("display","block")
//         changeButton.text("CSV 입력")
//     }
//
//
// }

//엑셀을 읽어오는 함수
function readExcel() {
    let input = event.target;
    let reader = new FileReader();
    reader.onload = function () {
        let data = reader.result;
        let workBook = XLSX.read(data, { type: 'binary' });

        let rows = XLSX.utils.sheet_to_json(workBook.Sheets['Sheet1']);
        if(Object.values(rows).length ==totalCnt){
            for (let i in rows){
                $('#input-device-'+i).val(rows[i]['serial'])
            }
        }else {
            alert("입력된 값과 주문한 기기수량이 일치하지 않습니다.\n 입력된 기기 갯수: "+Object.values(rows).length
            +"\n총 주문 갯수: "+totalCnt)
        }

    };
    reader.readAsBinaryString(input.files[0]);
}

