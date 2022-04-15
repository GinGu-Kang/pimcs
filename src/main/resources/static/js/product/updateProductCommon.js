/**
 *  update modal들 클릭시
 *  클릭한 버튼에 매칭된 modal 보여주기 
 */
 $(document).on("click",".update-dropdown-item",function(){

    let items = createCheckedItem($(this).attr("data"));
    if(items == ""){
        alert("제품을 체크해주세요.");
        return;
    }
    $(".update-mat-modal .target-list-container").empty();
    $(".update-mat-modal .target-list-container").append(items);

    let targetModal = $(this).attr("target");
    const updateModal = new bootstrap.Modal(document.getElementById(targetModal));
    updateModal.show();
});


/**
 * 체크된 제품와 변경될 값으로 li태그 만듬
 * @param {*} oldKey 변경할 컬럼명
 * @returns li태그
 */
 const createCheckedItem = function(oldKey){
    let li = "";
    // if($(".all-rows-checked").is(":checked")){
    //     loadMatAll();
    // }
    for(let product of getCheckedItems()){
        if(oldKey == "productName"){
            li += `<li>${product[oldKey]}</li>`;
        }else if(oldKey == "productCategory"){
            li += `<li>${product['productName']}&nbsp;/&nbsp;${product[oldKey]['categoryName']}</li>`;
        }else if(oldKey == "productImage"){
            li += `<li class='d-flex align-items-center'>${product['productName']}&nbsp;/&nbsp;<img width='50' height='50' src='${product[oldKey]}'/></li>`;
        }else if(oldKey == "productWeight"){
            li += `<li>${product['productName']}&nbsp;/&nbsp;${product[oldKey]}g</li>`;
        }else{
            li += `<li>${product['productName']}&nbsp;/&nbsp;${product[oldKey]}</li>`;
        }
        
    }
    return li;
}

/**
 * 제품변경 폼생성
 */
const updateFormData = function({columnName, replaceValue}){
    let formData = new FormData();
    let checkedProducts = getCheckedItems();

    formData.append("updateTargetColumn",columnName);

    for(let i=0; i<checkedProducts.length; i++){
        let product = checkedProducts[i];
        if(columnName != null && replaceValue != null) 
            product[columnName] = replaceValue;

        // console.log(product);
        // console.log(replaceValue);
        console.log(product.productImage);
        formData.append(`productForms[${i}].product.id`,product.id);
        formData.append(`productForms[${i}].product.productCode`,product.productCode);
        formData.append(`productForms[${i}].product.productName`,product.productName);
        formData.append(`productForms[${i}].productCategoryId`,product.productCategory.id);
        formData.append(`productForms[${i}].product.productWeight`,product.productWeight);
        if(columnName == "productImage"){
            formData.append(`productForms[${i}].productImage`,product.productImage);
        }else{
            formData.append(`productForms[${i}].product.productImage`,product.productImage);
        }


    }

    return formData;
}


/**
 *  파일을 로드하면 모달을 show해서 이미지 편집
 */
 $(document).on("change",".product-img-file",function(event){
    const IMG_EXT_REG = /(.*?)\.(jpg|jpeg|png|JPG|JPEG|PNG)$/;
    $('#imageCropModal .modal-body').empty().append('<img id="edit-img" style="display:block; max-width:100%;" src="">');
    var image = $('#edit-img');
    var imgFile = $(this).val();

    console.log("file change");
    if(imgFile.match(IMG_EXT_REG)) { //유효한 이미지 확장자이면
        var reader = new FileReader();
        reader.onload = function(event) {
            image.attr("src", event.target.result);
            createCopper(image);
            const imageCropModal = new bootstrap.Modal(document.getElementById('imageCropModal'), {keyboard: false});
            imageCropModal.show();

        };
        reader.readAsDataURL(event.target.files[0]);
    } else{
        alert("이미지 파일(jpg, png형식의 파일)만 올려주세요");
    }
});

/**
 * cropper.js Cropper 객체생성
 * @param {*} image 이미지 태그
 */
 const createCopper = function(image){
    let width = $(".modal-body").width();
    let height = $(".modal-body").height();
    console.log(`width: ${width}, height: ${height}`);
    let cropper = image.cropper( {
        aspectRatio: 115 / 137,
        dragMode: 'move',
        autoCropArea: 0.6,
        restore: false,
        guides: true,
        center: true,
        highlight: false,
        cropBoxMovable: false,
        cropBoxResizable: false,
        toggleDragModeOnDblclick: false,
        minContainerWidth: $(".modal-body").innerWidth(),
        minContainerHeight: $(".modal-body").innerHeight(),

    });
    
}
