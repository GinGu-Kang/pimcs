jQuery(function ($) {
    
    const IMG_EXT_REG = /(.*?)\.(jpg|jpeg|png|JPG|JPEG|PNG)$/;

    

    /**
     *  파일을 로드하면 모달을 show해서 이미지 편집 
     */
    $(document).on("change",".product-img-file",function(event){
        // $('.modal-body').empty().append('<img id="edit-img" style="display:block; max-width:100%;" src="">');
        var image = $('#edit-img');
        var imgFile = $(this).val();
        
        if(imgFile.match(IMG_EXT_REG)) { //유효한 이미지 확장자이면
        	var reader = new FileReader(); 
        	reader.onload = function(event) { 
        		// image.attr("src", event.target.result);
        		createCopper(image);
                imageCropModal.show();
                
            }; 
        	reader.readAsDataURL(event.target.files[0]);
        } else{
        	alert("이미지 파일(jpg, png형식의 파일)만 올려주세요");
        }
    });
    
    /**
     * cropper.js Cropper 객체생성
     * @param {*} image 이미지 태그
     */
    const createCopper = function(image){
        let width = $(".modal-body").width();
        let height = $(".modal-body").height();
        console.log(`width: ${width}, height: ${height}`);
        let cropper = image.cropper( {
            aspectRatio: 115 / 137,
            // highlight: false,
            // cropBoxMovable: false,
            // cropBoxResizable: false,
            dragMode: 'move',
            
            autoCropArea: 0.6,
            restore: false,
            guides: true,
            center: true,
            highlight: false,
            cropBoxMovable: false,
            cropBoxResizable: false,
            toggleDragModeOnDblclick: false,
       
            minContainerWidth: $(".modal-body").innerWidth(),
            minContainerHeight: $(".modal-body").innerHeight(),
   
        });
        
        // var contData = cropper.getContainerData(); //Get container data
        // cropper.setCropBoxData({ height:'50v', width: '100%' })
    }

    /**
     * ajax로 cardView html 요청해서 .card-container 태그에 추가
     */
    const appendCardView = function(){
        let index = $(".card").length;
        let cardViewHtml = loadGetData({
                                url: "/product/create/cardview",
                                data: {"index": index}
                            });
        
        $(".dynamic-cardview-container").append(cardViewHtml);
    }

    /**
     * 카드뷰 추가버튼 클릭시
     */
    $(document).on("click",".append-card-view-btn",function(){
        appendCardView();
    });

    appendCardView();
});

