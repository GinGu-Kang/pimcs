const MOBILE_WIDTH = 768;

$(document).on("click",".profile-icon-box",function(){
    if($("ul[aria-labelledby='dropdownMenuButton1']").hasClass("show"))
        closeProfile();
     else
        openProfile();
});


$(document).on("click","header .close-container",function(){
    closeProfile();
});


/**
 * menu icon click시 navigation open
 */
$(document).on("click",".menu-icon-box", function(){

    if($(document).width() <= MOBILE_WIDTH){
        if($("nav").hasClass("open"))
            closeNavigation();
        else 
            openNavigation();
    }
});

$(document).on("click","nav",function(){
    if($(document).width() <= MOBILE_WIDTH){
        closeNavigation();
    }
});

/**
 * navigation open시 profile doropdown close
 */
const openNavigation = function(){
    $("nav").addClass("open");
    closeProfile();
}

const closeNavigation = function(){
    $(".menu-icon-box").removeAttr("open");
    $("nav").removeClass("open");
}

/**
 * profile open시 navigation close 단 모바일때만 
 */
const openProfile = function(){
    
    $("ul[aria-labelledby='dropdownMenuButton1']").addClass("show");
    $("header .close-container").css("display","block");
    if($(document).width() <= MOBILE_WIDTH)
        closeNavigation();
}

const closeProfile = function(){
    $("ul[aria-labelledby='dropdownMenuButton1']").removeClass("show");
    $("header .close-container").css("display","none");
}