

/**
 *  update mat modal들 클릭시
 *  클릭한 버튼에 매칭된 modal 보여주기 
 */
$(document).on("click",".update-dropdown-item",function(){
    if($(".all-rows-checked").is(":checked")){
        loadMatAll();
    }

    let items = createCheckedItem($(this).attr("data"));
    if(items == ""){
        alert("매트를 체크해주세요.");
        return;
    }
    $(".update-mat-modal .target-list-container").empty();
    $(".update-mat-modal .target-list-container").append(items);

    let targetModal = $(this).attr("target");
    const updateModal = new bootstrap.Modal(document.getElementById(targetModal));
    updateModal.show();
});



/**
 * 체크된 매트 시리얼번호와 변경될 값으로 li태그 만듬
 * @param {*} oldKey 변경할 컬럼명
 * @returns li태그
 */
const createCheckedItem = function(oldKey){
    let li = "";
    

    for(let mat of getCheckedItems()){
        
        if(oldKey == "calcMethod"){
            let calcMthodStr = (mat[oldKey] == "0") ? "무게(g)" : "갯수(개)";
            li += `<li>${mat['serialNumber']}&nbsp;/&nbsp;${calcMthodStr}</li>`;

        }else if(oldKey == "product"){
            li += `<li>${mat['serialNumber']}&nbsp;/&nbsp;${getProductAttribute(mat,"productName")}</li>`;
        }else if(oldKey == "transfer"){
            li += `<li>${mat['serialNumber']}</li>`;
        }else {
            li += `<li>${mat['serialNumber']}&nbsp;/&nbsp;${mat[oldKey]}</li>`;
        }
        
    }
    return li;
}




const updateMatFormData = function({columnName, replaceValue}){
    let formData = new FormData();
    let checkedMats = getCheckedItems();
    
    for(let i=0; i < checkedMats.length; i++){
        let mat = checkedMats[i];
        if(columnName != null && replaceValue != null)
            mat[columnName] = replaceValue;
        formData.append(`matForms[${i}].mat.id`, mat.id);
        formData.append(`matForms[${i}].productId`, mat.product.id);
        formData.append(`matForms[${i}].mat.serialNumber`, mat.serialNumber);
        formData.append(`matForms[${i}].mat.calcMethod`, mat.calcMethod);
        formData.append(`matForms[${i}].mat.threshold`, mat.threshold);
        formData.append(`matForms[${i}].mat.inventoryWeight`, mat.inventoryWeight);
        formData.append(`matForms[${i}].mat.matLocation`, mat.matLocation);
        formData.append(`matForms[${i}].mat.currentInventory`, mat.currentInventory);
        formData.append(`matForms[${i}].mat.productOrderCnt`, mat.productOrderCnt);
        formData.append(`matForms[${i}].mat.boxWeight`, mat.boxWeight);


    }
    
    return formData;
}


