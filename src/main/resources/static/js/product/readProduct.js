//수정 hover시 이벤트처리
$(document).on("mouseover",".modify-btn",function(){
    $(".modify-dropdown").css("display","block");
    $(this).addClass("hover");
});

$(document).on("mouseleave",".modify-btn",function(){
     $(".modify-dropdown").css("display","none");
     $(this).removeClass("hover");
});

/**
* 전체행체크시 이벤트 
*/
$(document).on("change",".all-rows-checked",function(){
   
    let isAllChecked = $(this).is(":checked")
    $(".row-checked").prop("checked", isAllChecked);
 
    if(isAllChecked == true){
        loadMatAll();
    }else{
         let checkedList = getCheckedItems();
         checkedList.forEach(function(product){
                 setStorage({product: product, isChecked: false});
         });
    }
    let inventoryCnt = getCheckedItems().length;
    $(".inventory-cnt-viewer").text(formatKoKr(inventoryCnt));
 });

/**
* 전체데이터 로드해서 sessionStorage에 저장
*/
const loadMatAll = function(){

    let totalPages = $("#desktop-pagination").data().twbsPagination.options.totalPages;
    let pageSize   = $(".show-item-select option:selected").val();
    totalPages = parseInt(totalPages);
    pageSize = parseInt(pageSize);

    let resultData = loadGetData({
        url: "/api/page/products",
        data: {
            "page": 1,
            "size": pageSize * totalPages
        }
    });
    
    if(resultData != undefined){
        for(let product of resultData.data){

            setStorage({product:product,isChecked: true});
        }
    }
}

/**
 * 체크박스 체크시
 */
$(document).on("change",".row-checked",function(){
    let productId = $(this).parents('tr').first().attr('data');
    setStorage({
        product: getStorageItem(productId),
        isChecked: $(this).is(":checked")
    });

    //체크한 제품과 session storage저장된 갯수가 같으면 전체체크
    let checkedCnt = getCheckedItems().length;
    if(checkedCnt == sessionStorage.length)
        $(".all-rows-checked").prop("checked",true);
    else
        $(".all-rows-checked").prop("checked",false);

    $(".inventory-cnt-viewer").text(formatKoKr(checkedCnt));
});


/**
 * TABLE에 있는 제품데이터 초기화
 */
const initProductsData = function({page,size}){
    
    let resultData = loadProductsData({page:page, size:size});
    console.log(resultData);
    if(resultData == undefined) return;

    pagination({
        totalPages: resultData.totalPages,
        currentPage: resultData.pageNumber,
        size: resultData.pageSize
    });
    $("tbody").empty();
    for(let product of resultData.data){

        let findProduct = getStorageItem(product.id);
        if(findProduct){
            console.log(`id: ${product.id}, ${findProduct.checked}`);
            setStorage({
                product: product,
                isChecked: findProduct.checked
            });
        }else{
            setStorage({
                product: product,
                isChecked: false
            })
        }
        $("tbody").append(createTableRow(product));
        
    }
}


/**
 * twbsPagination 활성화
 * @param  totalPages 전체페이지
 * @param  currentPage 현재페이지
 * @param size 한페이지에 보여지는 개수
 */
 const pagination = function({totalPages,currentPage,size}){
    $("#desktop-pagination").twbsPagination('destroy'); // 페이지네이션 객체를 ram에서 소멸
    if(totalPages == 0 || currentPage == 0) return;
    //데스크탑용 페이지네이션 생성
    $("#desktop-pagination").twbsPagination({
        totalPages:totalPages,    
        visiblePages:5,    
        startPage: (currentPage != 0) ? currentPage : 1,
        initiateStartPageClick:false,
        prev:'<',
        next:'>',
        first:'',
        last:'',
        onPageClick: function (event, page) {
            if(currentPage != page){
                initProductsData({page:page, size:size})
            }
        }
    });
}
/**
 * 상품테이블 행생성
 */
const createTableRow = function(product){
    let findProduct = getStorageItem(product.id);
    let checked = '';
    
    if(findProduct) checked =(findProduct.checked) ? 'checked' : '';

    let tr = `<tr class='text-size-between-middle-samll' data=${product.id}>`;
        tr +=   `<td><input class="row-checked" ${checked} type="checkbox"></td>`
        tr +=   `<td>${product.productCode}</td>`;
        tr +=   `<td>${product.productName}</td>`;
        tr +=   `<td>${product.productCategory.categoryName}</td>`;
        tr +=   `<td><img class="td-small-image" src="${product.productImage}"/></td>`;
        tr +=   `<td>${product.productWeight}g</td>`;
        tr += `</tr>`;
     return tr;   
}

const loadProductsData = function({page, size}){
    const resultData = loadGetData({
        url: "/api/page/products",
        data: {"page":page, "size":size} 
    });
    return resultData;
}

/**
 * @param product 제품데이터
 * @param isChecked 제품이 체크박스를 통해 체크되었는 여부
 */
 const setStorage = function({product, isChecked}){
    product['checked'] = isChecked;
    sessionStorage.setItem(product.id,JSON.stringify(product));
}

/**
 * 체크된 아이템들 찾기
 * @returns 체크된 아이템 list
 */
const getCheckedItems = function(){
    let checkedItems=[];
    for(let productId of Object.keys(sessionStorage)){
        let product = getStorageItem(productId);
        if(product['checked'])
            checkedItems.push(product);
    }
    return checkedItems;
}

/**
 * sessionStorage 아이템 가져오기
 */
const getStorageItem = function(key){
    return JSON.parse(sessionStorage.getItem(key));
}