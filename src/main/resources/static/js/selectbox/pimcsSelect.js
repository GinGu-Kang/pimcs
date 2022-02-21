
(function($){

    $(document).ready(function(){
        new SelectBox();
    });

    const SelectBox = function(){   
        this.select = $(".pimcs-select");
        
        if($(this.select).length == 0) return;
        this.options = this.getOptionsValue();
        if(this.options.length == 0) throw Error("option태그가 존재하지 않습니다.");
        
        $(this.select).css("display","none");
        $(this.select).after(this.getCustomSelect());

        var _this = this;
        //select box 클릭시 option보여주기 이벤트
        $(document).on("click",".pimcs-custom-select",function(e){
            if(!$(this).hasClass("active")) // .pimcs-custom-sele active class 없으면
                _this.activate($(this));
            else
                _this.deactivate($(this));
        });
        
        //select 아이템 선택시
        $(document).on("click",".option-item",function(){
            $(this).closest("div.pimcs-custom-select").children("button.pimcs-custom-select-btn").text($(this).text());
            $(_this.select).children(`option:nth-of-type(${$(this).attr('index')})`).attr("selected","true");
            _this.deactivate($(_this.select));
        });


        $("body").click(function(e){
            if(_this.isCapturing(e.target.className)) return //캡쳐링 방지
            
            _this.deactivate(null);
            $("ul[aria-labelledby='dropdownMenuButton1']").removeClass("show")
        });
    }

    var activate = function(element){
        $(element).addClass("active");
        $(element).children("ul.option-item-list").css("max-height",500);
    }

    var deactivate = function(element){
        if(element != null){
            $(element).removeClass("active");
            $(element).children("ul.option-item-list").css("max-height",0);
        }else{
            $(".pimcs-custom-select").removeClass("active");
            $(".pimcs-custom-select .option-item-list").css("max-height",0);
        }
    }

    /**
     * @param className target 태그 classNames
     * @returns true면 캡쳐링 발생, false면 발생하지 않음
     */
    var isCapturing = function(className){
        if(typeof(className) != "string") return true;
        if(className.search("pimcs-custom-select-btn") != -1) return true;
        else if(className.search("pimcs-custom-select") != -1) return true;
        else if(className.search("option-item") != -1) return true;
        else return false;
    }

    var getCustomSelect = function(){
        
        var classes = $(this.select).attr("class");

        var html = `<div class='pimcs-custom-select ${classes}'>`;
            html +=     `<button class='pimcs-custom-select-btn'>${this.options[0]}</button>`;
            html +=      `<ul class='option-item-list' style='top: ${$(this.select).height()+2}px;'>`;
            html +=         this.createOptionTag();
            html +=       `</ul>`;
            html += `</div>`;

        return html;
    }
    
    var getOptionsValue = function(){
        var options = [];
        $(this.select).children("option").each(function(index,option){
            options.push($(option).text());
        });
        return options;
    }

    var createOptionTag = function(){
        var html = "";
        this.options.forEach((element,index) =>{
            html += `<li class='option-item' index=${index+1}> ${element} </li>`
        });
        console.log(this.options);

        return html;
    }
    

    SelectBox.prototype = {
        constructor: SelectBox,
        getOptionsValue: getOptionsValue,
        getCustomSelect: getCustomSelect,
        createOptionTag: createOptionTag,
        isCapturing: isCapturing,
        activate: activate,
        deactivate: deactivate
    }
    

})(window.jQuery);