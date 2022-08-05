
jQuery.fn.serializeObject = function() {
    var obj = null;
    try {
        if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
            var arr = this.serializeArray();
            if (arr) {
                obj = {};
                jQuery.each(arr, function() {
                    obj[this.name] = this.value;
                });
            }//if ( arr ) {
        }
    } catch (e) {
        alert(e.message);
    } finally {
    }

    return obj;
};


$("#submit-btn").on("click",function (){

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    let formData = $('#createMatCategoryForm').serializeObject();
    let resultData = loadPostDataToJson({
        url: "/admin/matcategories",
        header: {
            'header': header,
            'token': token
        },
        data: formData

    });
    if(resultData!=null){
        alert("저장되었습니다.")
        location.replace("/admin/matcategories");
    }


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
