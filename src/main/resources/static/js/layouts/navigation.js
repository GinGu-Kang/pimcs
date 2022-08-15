
const URL_NAV_ITEM_MAPPING = {
    "/mats": "#inventory-list",
    "/products": "#product-list",
    "/mats/history-graph": "#inventory-list",
    "/mats/history": "#in-out-history",
    "/products/category/create": "#category-registration",
    "/company/worker": "#company-worker",
    "/products/create": "#product-registration",
    "/mats/create": "#mat-registration",
    "/company/info": "#companyInfo",
    "/company/info/modify": "#companyInfo",
    "/order/mat": "#orderMat",
    "/qnas": "#QnA",
    "/qna/question": "#QnA",
    "/qna/view": "#QnA",
    "/admin/qnas": "#adminQnA",
    "/admin/orders": "#adminOrderMat",
    "/admin/companies": "#companyManagement",
    "/admin/order-mail-frame/create": "#emailFrame",
    "/admin/matcategories": "#matCategory",
    "/admin/matcategories": "#matCategoryRead",
    "/order/history": "#order-history",
    "/order/history/search": "#order-history",
    "/mats/log": "#matLog",
    "/products/log": "#productLog",
    

}
jQuery(function ($) {
    initNavigation();    
});

const initNavigation = function(){
    
    const navItem = URL_NAV_ITEM_MAPPING[location.pathname];

    $(navItem).parents("details").each(function(index, element){
        $(element).attr("open","");
        if($(element).children(".sub-summary").length != 0){
            $(element).children(".sub-summary").addClass("active");
        }
    });
    

    $(navItem).addClass("active");
}


