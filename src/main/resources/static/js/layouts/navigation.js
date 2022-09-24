
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
    "/admin/user": "#adminUsers",
    "/admin/qnas": "#adminQnA",
    "/admin/orders": "#adminOrderMat",
    "/admin/companies": "#companyManagement",
    // "/admin/companies/\d": "#companyManagement",
    "/admin/order-mail-frame/create": "#emailFrame",
    "/admin/matcategories/create": "#matCategory",
    "/admin/matcategories": "#matCategoryRead",
    "/orders/history": "#order-history",
    "/mats/log": "#matLog",
    "/products/log": "#productLog",
    
    

}
jQuery(function ($) {
    initNavigation();    
});

const initNavigation = function(){
    let navItem = undefined;    
    if (location.pathname in URL_NAV_ITEM_MAPPING){
        navItem = URL_NAV_ITEM_MAPPING[location.pathname];
    }else{
        for(const key of Object.keys(URL_NAV_ITEM_MAPPING)){
            if(location.pathname.startsWith(key)){
                console.log(key);
                navItem = URL_NAV_ITEM_MAPPING[key];
                break;
            }
        }
    }
    console.log(navItem);

    $(navItem).parents("details").each(function(index, element){
        $(element).attr("open","");
        if($(element).children(".sub-summary").length != 0){
            $(element).children(".sub-summary").addClass("active");
        }
    });
    

    $(navItem).addClass("active");
}


