
jQuery(function ($) {
    

    $(document).on("click",".modal-completion-btn",function(){
        console.log("click");
        $('.img-view').empty().append(`<img id="preview-img" style="width:100%; height:100%" src=""/>`);
    	var image = $('#edit-img');
    	var result = $('#preview-img');
    	
    	let canvas = image.cropper('getCroppedCanvas');
    	result.attr('src',canvas.toDataURL("image/jpg"));
        imageCropModal.hide();
    });

    $(document).on("click",".modal-cancel-btn", function(){

    });
});