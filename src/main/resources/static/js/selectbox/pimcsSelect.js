
(function($){

    $(document).ready(function(){
        new SelectBox();
    });

    const SelectBox = function(){
        this.select = $(".pimcs-select");

        $(this.select).css("display","none");
        
        $(this.select).after(this.getCustomSelect());

    }

    var getCustomSelect = function(){
        
        var options = this.getOptionsValue();
        if(options.length == 0) throw Error("option태그가 존재하지 않습니다.");
        

        var classes = $(this.select).attr("class");

        var html = `<div class='pimcs-custom-select ${classes}'>`;
            html +=     `<button class='pimcs-custom-select-btn'>${options[0]}</button>`;

            html += `</div>`;

        return html;
    }
    
    var getOptionsValue = function(){
        var options = [];
        $(this.select).children("option").each(function(index,option){
            options.push($(option).val());
        });
        return options;
    }

    SelectBox.prototype = {
        constructor: SelectBox,
        getOptionsValue: getOptionsValue,
        getCustomSelect: getCustomSelect,
        

    }
    

})(window.jQuery);