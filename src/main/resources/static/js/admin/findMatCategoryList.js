
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

function matCategoryModify(id) {
    let matCategoryName = $('#matCategoryName-'+id).prop('value');
    let matInformation = $('#matInformation-'+id).prop('value');
    let matPrice = $('#matPrice-'+id).prop('value');
    let maxWeight = $('#maxWeight-'+id).prop('value');
    let mappingSerialCode = $('#mappingSerialCode-'+id).prop('value');

    resultData=loadDataToJson({
        type:'put',
        url:'/admin/matcategories',
        data: {
            id:id,
            matCategoryName: matCategoryName,
            matInformation: matInformation,
            matPrice: matPrice,
            maxWeight: maxWeight,
            mappingSerialCode:mappingSerialCode,
        }
    })
    if(resultData){
        alert("수정되었습니다.")
    }
}


function matCategoryRemove(id) {
    let data={id:id}

    resultData=loadDataToJson({
        url:'/admin/matcategories',
        type:'delete',
        data:data})
    if(resultData){
        alert("삭제되었습니다.")
        $('#tr-'+id).remove()
    }
}