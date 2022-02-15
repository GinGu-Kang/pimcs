

$(document).on("click",".profile-icon-box",function(){
    console.log("click");
    let dropDownMenu = $("ul[aria-labelledby='dropdownMenuButton1']");

    if($(dropDownMenu).hasClass("show")){
        $(dropDownMenu).removeClass("show");
    }else{
        $(dropDownMenu).addClass("show");
    }
});

