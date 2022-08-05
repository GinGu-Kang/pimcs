var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");


$("#createMatCategoryForm").submit(function (){

    console.log("hihi")
    alert("hi")

});



    // $.ajax({
    //     url: '/admin/matcategories',
    //     type: 'put',
    //     data: {
    //         id:id,
    //         matCategoryName: matCategoryName,
    //         matInformation: matInformation,
    //         matPrice: matPrice,
    //         maxWeight: maxWeight,
    //         mappingSerialCode:mappingSerialCode,
    //     },
    //     beforeSend: function (xhr) {
    //         xhr.setRequestHeader(header, token);
    //     },success:function(resultMap){
    //         alert(resultMap['msg'])
    //
    //     },
    //     error: function () {
    //         alert("에러입니다");
    //     }
    // });
