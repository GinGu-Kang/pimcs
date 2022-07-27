


$("input:checkbox[name='serial']").on("click",function (){
    console.log($("input:checkbox[name='serial']:checked")[1].value)
})


//저장시 유효성 검사
$("input:checkbox[name='serial']").on("click",function (){
    let result = confirm("기기를 삭제하시겠습니까?");
    let fileValue = $("input:checkbox[name='serial']:checked").length;
    let deviceSerialList = [];

    for(var i=0; i<fileValue; i++){
        if($("input:checkbox[name='serial']:checked")[i].value!=null){
            deviceSerialList[i] = $("input:checkbox[name='serial']:checked")[i].value;
        }else{
            return alert("빈칸을 입력할 수 없습니다.")
        }
    }
    console.log(deviceSerialList);
    // if(result){
    //     console.log(typeof deviceSerialList)
    //     $.ajax({
    //         url:ownDeviceUrl,
    //         type:'post',
    //         data:{deviceSerialList:deviceSerialList,
    //             companyId:companyId},
    //         beforeSend : function(xhr)
    //         {   /*데이터를 전송하기 전에 헤더에 csrf값을 설정한다*/
    //             xhr.setRequestHeader(header, token);
    //         },
    //         success:function(resultMap){
    //
    //             if(resultMap['isError']=="true"){
    //                 alert(resultMap['message']);
    //             }else{
    //                 alert(resultMap['message']);
    //                 location.reload();
    //             }
    //         },
    //         error:function(){
    //             alert("에러입니다");
    //         }
    //     });
    //     // orderview로 새로고침 이후 맨 밑에 배송된 기기들 보여주기 이메일 전송하기
    // } else {
    //     alert("취소되었습니다.");
    // }
})