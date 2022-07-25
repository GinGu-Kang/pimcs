
const URL_NAV_ITEM_MAPPING = {
    "/": "#inventory-list",
    "/product/read": "#product-list",
    "/inout/history/graph": "#inventory-list",
    "/inout/history": "#in-out-history",
    "/inout/history/search": "#in-out-history",
    "/product/category/create": "#category-registration",
    "/company/worker": "#company-worker",
    "/product/create": "#product-registration",
    "/mat/create": "#mat-registration",
    "/company/info": "#companyInfo",
    "/company/info/modify": "#companyInfo",
    "/order/mat": "#orderMat",
    "/qna/list": "#QnA",
    "/qna/question": "#QnA",
    "/qna/view": "#QnA",
    "/admin/qna/list": "#adminQnA",
    "/admin/order/list": "#adminOrderMat",
    "/admin/company/list": "#companyManagement",
    "/admin/email/frame/modify": "#emailFrame",
    "/admin/matcategory/add": "#matCategory",
    "/admin/matcategory/read": "#matCategoryRead",
    "/order/history": "#order-history",
    "/order/history/search": "#order-history",
    "/mat/log": "#matLog",
    "/mat/log/search": "#matLog",
    "/product/log": "#productLog",
    "/product/log/search": "#productLog"

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


