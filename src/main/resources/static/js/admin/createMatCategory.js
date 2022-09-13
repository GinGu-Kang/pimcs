

$("#submit-btn").on("click",function (){
    let formData = $('#createMatCategoryForm').serializeObject();
    let resultData = loadDataToJson({
        type:'post',
        url: "/admin/matcategories",
        data: formData
    });

    if(resultData!=null){
        alert("저장되었습니다.")
        location.replace("/admin/matcategories");
    }
});

