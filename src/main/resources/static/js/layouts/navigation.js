
const URL_NAV_ITEM_MAPPING = {
    "/": "#inventory-list",
    "/product/category/create": "#category-registration"

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


