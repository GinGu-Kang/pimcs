const MOBILE_WIDTH = 768;

$(document).on("click",".profile-icon-box",function(){
    console.log("click");
    let dropDownMenu = $("ul[aria-labelledby='dropdownMenuButton1']");

    if($(dropDownMenu).hasClass("show")){
        $(dropDownMenu).removeClass("show");
    }else{
        $(dropDownMenu).addClass("show");
    }
});


$(document).on("click",".menu-icon-box", function(){

    if($(document).width() <= MOBILE_WIDTH){
        if($("nav").hasClass("open"))
            $("nav").removeClass("open");
        else
            $("nav").addClass("open");
        
    }
});

$(document).on("click","nav",function(){
    if($(document).width() <= MOBILE_WIDTH){
        $(".menu-icon-box").removeAttr("open");
        $("nav").removeClass("open");
    }
});