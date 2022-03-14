
const URL_NAV_ITEM_MAPPING = {
    "/": "#inventory-list",
    "/inout/history": "#in-out-history",
    "/product/category/create": "#category-registration",
    "/company/worker": "#company-worker",
    "/product/create": "#product-registration",
    "/mat/create": "#mat-registration"

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


