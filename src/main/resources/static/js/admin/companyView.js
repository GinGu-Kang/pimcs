
let token = $("meta[name='_csrf']").attr("content");
let header = $("meta[name='_csrf_header']").attr("content");

/*체크된 기기삭제*/
$(".del").on("click",function (){
    let result = confirm("회사의 소유 기기를 삭제하시겠습니까?");
    let fileValue = $("input:checkbox[name='serial']:checked").length;
    let ownDeviceList = [];

    if(fileValue!=0){
        for(var i=0; i<fileValue; i++){
            if($("input:checkbox[name='serial']:checked")[i].value!=null){
                ownDeviceList[i] = $("input:checkbox[name='serial']:checked")[i].value;
            }else{
                return alert("빈칸을 입력할 수 없습니다.")
            }
        }
    }else{
        return alert("선택된 기기가 없습니다.")
    }

    console.log(ownDeviceList);
    if(result){
        $.ajax({
            url:"/admin/owndevices",
            type:'delete',
            data:{ownDeviceList:ownDeviceList},
            beforeSend : function(xhr)
            {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
                xhr.setRequestHeader(header, token);
            },
            success:function(resultMap){
                location.reload();
            },
            error:function(){
                alert("에러입니다");
            }
        });
        // orderview로 새로고침 이후 맨 밑에 배송된 기기들 보여주기 이메일 전송하기
    } else {
        alert("취소되었습니다.");
    }
})

/*기기추가*/
$("#add-mat-button").on("click",function () {
    let deviceSerial= $("#input-serial").val()

    $.ajax({
        url: `/admin/company/owndevice`,
        type: 'get',
        data: {deviceSerial: deviceSerial,
            companyId:companyId},
        beforeSend: function (xhr) {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
            xhr.setRequestHeader(header, token);
        },
        success: function (resultMap) {
            if(resultMap['isError']=="true"){
                alert(resultMap['message']);
            }else{
                alert(resultMap['message']);
                $("#input-serial").val("")
            }
        },
        error: function () {
            alert("에러입니다");
        }
    })

})

/*모달 닫기시 새로고침*/
$(".add-mat-close").on("click", function (){
    location.reload();
})