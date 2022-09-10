


    //체크시 tr색 변환
    $('.check-worker').change(function (){
        changeTrColor()
    })
    //체크된 tr 색변환
    function changeTrColor(){
        let uncheckedTr=$('.check-worker').parents('tr')
        let checkedTr=$('.check-worker:checked').parents('tr')
        uncheckedTr.css('background-color','white')
        checkedTr.css('background-color','#ececec')
        console.log(checkedTr)
    }





    //선택된 사원 삭제
    function removeSelectWorker(){
        var result = confirm("정말 사원을 삭제하시겠습니까?");
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        let checkWorkerTr=$('.check-worker:checked').parents('tr')
        let checkWorkerString=$('.check-worker:checked').parents('tr').find('.email').text()
        let selectWorkersEmail=checkWorkerString.slice(0,-1).split(" ");
        if(result){
            
            const res = deleteRequest({
                url: "/companies/workers",
                data: {"selectWorkersEmail":selectWorkersEmail},
                header: {
                    'header': header,
                    'token': token
                }
            });
            if(res.success == true){
                alert(res.message);
                checkWorkerTr.remove();
            }

        }else{
            alert("취소 하였습니다.")
        }


    }



    //check 된 갯수 display
    function checkCount(){
    var checkDisplay=$('#check-display')
    var checkCount=$("input:checkbox[name='check-worker']:checked").length
    var checkboxCount=$("input:checkbox[name='check-worker']").length
    var checkHead=$("#check-worker-head")

    if(checkCount==checkboxCount){
    checkHead.prop('checked',true)
    }else{
        checkHead.prop('checked',false)
    }
        checkDisplay.text("선택된 사원: "+checkCount)
    }
    //전체 체크박스 체크
    function checkAll(){
    var checkHeadState=$('#check-worker-head').is(":checked");
    var checkbox=$("input:checkbox[name='check-worker']")


    if(checkHeadState){
    checkbox.prop('checked', true)
    }else{
        checkbox.prop('checked', false)
}
    checkCount();   //체크된 갯수 체크
    changeTrColor();//체크된 tr 색변경
}


    //권한 수정 삭제
    function checkEvent(checkbox){
    var emailTd = $(checkbox).parent().parent().children(".email");
    var email = $(emailTd).attr('id');
    var authority = $(checkbox).attr('class');
    if ($(checkbox).is(':checked')){
    giveAuthority({
    email:email,
    authority: authority,
    checkbox: $(checkbox)
    });
        alert("권한이 설정되었습니다.")
    }
        //throw $(e).prop("checked",true)
        else{
        // console.log($(checkbox))
        removeAuthority({
        email:email,
        authority: authority,
        checkbox: $(checkbox)
    });
        alert("권한이 삭제되었습니다.")
    }

    }
    //권한주기
    function giveAuthority({email,authority, checkbox}){

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
        url:'/companies/worker/authority',
        type:'post',
        data:{email:email,authority:authority},
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success:function (response){
            
            if(!response.success){
                alert(response.message);
            }
        },
        error:function(request,status,error){
            alert("권한 설정에 실패하였습니다.")
            $(checkbox).prop("checked",false);
        }
        });

    }
    //권한뺏기
    function removeAuthority({email,authority, checkbox}){

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url:'/companies/worker/authority',
            type:'delete',
            data:{email:email,authority:authority},
            beforeSend : function(xhr)
            {
                xhr.setRequestHeader(header, token);
            },
            success:function (response){
            
                if(!response.success){
                    alert(response.message);
                }
            },
            error:function(request,status,error){
                alert("권한 설정에 실패하였습니다.")
                $(checkbox).prop("checked",true);
            }
        });

    };
