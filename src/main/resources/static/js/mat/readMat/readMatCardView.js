/**
 * 카드뷰 데이터 초기화
 * @param {*} page 페이지네이션 현재 page
 * @param {*} size 한페이지당 개수
 */
const initCardView = function({page,size}){

    let matData = loadTableData({page: page, size: size});
    console.log(matData);
    createCardView(matData);

}

const createCardView = function(matData){
    $(".pimcs-card-container").empty();
    for(let mat of matData.data){
        let findMat = getStorageItem(mat.id);
        let isChecked = false;
        if(findMat != null){ // 로컬저장된 매트데이터가 있으면 저장된 check여부 적용
            isChecked = findMat.checked;
        }
        setStorage({mat:mat,isChecked: isChecked});

        let cardView = `<div class="pimcs-card">`;
            cardView+=      createCardViewHead(mat);
            cardView+=      createCardViewBody(mat);
            cardView+=      createCardViewFooter(mat);
            cardView+= `</div>`;
        $(".pimcs-card-container").append(cardView);
    }
}


const createCardViewHead = function(mat){
    const findMat = getStorageItem(mat.id);
    const checked = (findMat.checked) ? 'checked' : '';
    let cardViewHead = `<div class="pimcs-card-head d-flex">`;
        cardViewHead +=     `<div>`;
        cardViewHead +=         `<p class="card-product-name text-size-middle dot3">${mat.product.productName}</p>`;
        cardViewHead +=         `<p class="card-serial-number text-size-between-middle-samll dot3">${mat.serialNumber}</p>`;
        cardViewHead +=     `</div>`;
        cardViewHead +=     `<div class="d-flex justify-content-end">`;
        cardViewHead +=         `<input class="card-checked" type="checkbox" data=${mat.id} ${checked}/>`;
        cardViewHead +=     `</div>`;
        cardViewHead +=`</div>`

        return cardViewHead;

}

const createCardViewBody = function(mat){
    
    let cardViewBody = `<div class="pimcs-card-body d-flex">`;
        cardViewBody +=     `<div class="info-area">`;
        cardViewBody +=         `<div>`;
        cardViewBody +=             `<span class="label text-size-middle">상품위치:</span>`;
        cardViewBody +=             `<span class="value text-size-between-middle-samll">${mat.matLocation}</span>`;
        cardViewBody +=         `</div>`;
        cardViewBody +=         `<div class="d-flex flex-wrap align-content-center">`;
        cardViewBody +=             `<span class="label text-size-middle">제품코드:</span>`;
        cardViewBody +=             `<span class="value dot3 text-size-between-middle-samll">${mat.product.productCode}</span>`;
        cardViewBody +=         `</div>`;
        cardViewBody +=         `<div>`;
        cardViewBody +=             `<span class="label text-size-middle">기기버전:</span>`;
        cardViewBody +=             `<span class="value text-size-between-middle-samll">A3</span>`;
        cardViewBody +=         `</div>`;
        cardViewBody +=     `</div>`;
        cardViewBody +=     `<div class="img-area d-flext justify-content-end">`;
        cardViewBody +=         `<img src="${mat.product.productImage}"/>`;
        cardViewBody +=     `</div>`;
        cardViewBody +=`</div>`;
        return cardViewBody;

}

const createCardViewFooter = function(mat){
    let bg = "pimcs-card-bg-green";
    if(mat.threshold > mat.inventoryWeight){
        bg = "pimcs-card-bg-red";
    }
    let cardViewFooter = `<div class="pimcs-card-footer ${bg} text-size-middle d-flex">`;
        cardViewFooter+=    `<p>임계값/현재 재고:</p>`;
        cardViewFooter+=    `<p>${mat.threshold}/${mat.inventoryWeight}</p>`;
        cardViewFooter+= `</div>`;
    return cardViewFooter;
}

$(document).on("change",".card-checked",function(){
    let id = $(this).attr("data");
    let isChecked = $(this).is(":checked");
    let mat = getStorageItem(id);
    setStorage({mat:mat, isChecked: isChecked})
});