
let productCategories = []
const PRODUCT_CATEGORIES_CLASS = ".categories-name-text";
const PRODUCT_CATEGORIES_CONTAINER_CLASS = ".categories";

/**
 *  .product-categories 태그에서 제품카테고리 정보를 읽어서 리스트에 저장
 */
const init = function(){
    $(PRODUCT_CATEGORIES_CLASS).each(function(index, element){
        productCategories.push({
            "id": $(element).attr("data"),
            "categoryName": $(element).text()
        });
    });
}

/**
 * 검색창에 입력값이 변경되었을때
 * 해당 검색키워드를 포함하는 category 찾고
 * html 태그 값을넣어 다시 렌더링한다
 */
$(document).on("keyup","input.category-name-search",function(){
    
    let findCategories = searchCategory($(this).val());
    
    $(PRODUCT_CATEGORIES_CONTAINER_CLASS).empty();
    findCategories.forEach(function(element){
        $(PRODUCT_CATEGORIES_CONTAINER_CLASS).append(createCategoryTag(element));
    });
    
});

/**
 * 카테고리 검색
 * @param {*} query 검색쿼리
 * @returns 검색된 카테고리 
 */
const searchCategory = function(query){
    result = []
    
    productCategories.forEach(function(element){
        if(element['categoryName'].includes(query)){
            result.push(element);
        }
    });
    return result;
}

/**
 * 카테고리 html 생성
 * @param {*} category category데이터
 * @returns  생성된 html반환
 */
const createCategoryTag = function(category){
    let html = `<li class="category d-flex">`;
        html +=     `<p data="${category['id']}" class="categories-name-text text-size-between-middle-samll">`;
        html +=         `${category['categoryName']}`;
        html +=     `</p>`;
        html +=     `<i class="bi bi-x-lg delete-category-btn text-size-between-middle-samll" data="${category['id']}"/>`;
        return html;
}



$(document).on("click",".delete-category-btn", function(){

    if (!confirm("삭제하시겠습니까?")) {
        return;
    } 
   
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    const data = new FormData();
    data.append("id",$(this).attr("data"));

    let resultData = deleteRequest({
        url: "/products/category",
        data: new URLSearchParams(data).toString(),
        header: {
            'header': header,
            'token': token
        }
    });

    if(resultData != undefined){
        alert(resultData.message);
        window.location.reload();
    }
    
});