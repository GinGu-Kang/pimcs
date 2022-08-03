
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");


function matCategoryModify(id) {

    var matCategoryName = $('#matCategoryName-'+id).prop('value');
    var matInformation = $('#matInformation-'+id).prop('value');
    var matPrice = $('#matPrice-'+id).prop('value');
    var maxWeight = $('#maxWeight-'+id).prop('value');
    var mappingSerialCode = $('#mappingSerialCode-'+id).prop('value');

    $.ajax({
        url: '/admin/matcategories',
        type: 'put',
        data: {
            id:id,
            matCategoryName: matCategoryName,
            matInformation: matInformation,
            matPrice: matPrice,
            maxWeight: maxWeight,
            mappingSerialCode:mappingSerialCode,
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },success:function(resultMap){
            alert(resultMap['msg'])

        },
        error: function () {
            alert("에러입니다");
        }
    });
}


function matCategoryRemove(Id) {
    $.ajax({
        url:'/admin/matcategories',
        type:'delete',
        data:{Id:Id},
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success:function(resultMap){ //컨트롤러에서 넘어온 cnt값을 받는다
            alert("삭제되었습니다.")
            $('#tr-'+Id).remove()
        },
        error:function(){
            alert("에러입니다");
        }
    });
};
