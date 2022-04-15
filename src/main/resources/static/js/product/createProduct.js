jQuery(function ($) {
    
    const IMG_EXT_REG = /(.*?)\.(jpg|jpeg|png|JPG|JPEG|PNG)$/;

    

    /**
     *  파일을 로드하면 모달을 show해서 이미지 편집 
     */
    $(document).on("change",".product-img-file",function(event){
        $('.modal-body').empty().append('<img id="edit-img" style="display:block; max-width:100%;" src="">');
        var image = $('#edit-img');
        var imgFile = $(this).val();
        
        console.log("file change");
        if(imgFile.match(IMG_EXT_REG)) { //유효한 이미지 확장자이면
        	var reader = new FileReader(); 
        	reader.onload = function(event) { 
        		image.attr("src", event.target.result);
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
        
    }
    /**
     *  저장하기 클릭시 1개제품시 ajax를 이용해서 저장
     */
    $(document).on("click",".save-product-btn",function(){
        let token = $("meta[name='_csrf']").attr("content");
        let header = $("meta[name='_csrf_header']").attr("content");

        let canvas = $("#edit-img").cropper('getCroppedCanvas');
        let result = $("#preview-img").attr("src",canvas.toDataURL("image/jpeg"));
        let formData = getFormInputValue();
        canvas.toBlob(function(blob){
            
            formData.append( "productImage", blob);
            let resultData = loadPostMultipartData({
                                url: "/product/create",
                                data: formData,
                                header: {
                                    'header': header,
                                    'token': token
                                }
                            });
            console.log(resultData);
            appendRegisteredContainer(resultData);
            initFormInputValue();
        });
        
        
        
    });

    /**
     * 파일 제외한 모든 form input value를 FormData객체로 만들어서 return
     */
    const getFormInputValue = function(){
        let formData = new FormData();
        $("form.card-container").serializeArray().forEach(function(element){
            formData.append(element['name'],element['value']);
        });
        return formData;
    }

    /**
     * 등록된 상품영역에 등록
     * @param {*} html 저장한 제품 cardview
     */
    const appendRegisteredContainer = function(html){
        $(".registered-card-veiw").prepend(html);
    }

    /**
     * form input값 및 selectbox값 초기화
     */
    const initFormInputValue = function(){
        $("form.card-container input").each(function(index, element){
            $(element).val("");
        });
        $("form.card-container select option").prop("selected", false);
    }        

});

