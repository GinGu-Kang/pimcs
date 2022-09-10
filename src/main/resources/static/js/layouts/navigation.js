
const URL_NAV_ITEM_MAPPING = {
    "/mats": "#inventory-list",
    "/products": "#product-list",
    "/mats/history-graph": "#inventory-list",
    "/mats/history": "#in-out-history",
    "/products/category/create": "#category-registration",
    "/companies/workers": "#company-worker",
    "/products/create": "#product-registration",
    "/mats/create": "#mat-registration",
    "/companies/details": "#companyInfo",
    
    "/orders/mats/create": "#orderMat",
    "/qnas": "#QnA",
    "/qna/question": "#QnA",
    "/qna/view": "#QnA",
    "/admin/qnas": "#adminQnA",
    "/admin/order/list": "#adminOrderMat",
    "/admin/companies": "#companyManagement",
    "/admin/email/frame/modify": "#emailFrame",
    "/admin/matcategories": "#matCategory",
    "/admin/matcategories": "#matCategoryRead",
    "/orders/history": "#order-history",
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


