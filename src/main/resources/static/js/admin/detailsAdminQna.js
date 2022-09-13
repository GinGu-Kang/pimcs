$("#submit-btn").on("click",function (){
    let questionId=$('#questionId').val()
    let comment=$('#comment').val()
    let id=$('#id').val()

    data={question:{id:questionId},id:id,comment:comment}

    let resultData = loadDataToJson({
        type:'post',
        url: "/admin/qnas/answer",
        data: data
    });
    if(resultData!=null){
        alert("저장되었습니다.")
        location.reload()
    }
});