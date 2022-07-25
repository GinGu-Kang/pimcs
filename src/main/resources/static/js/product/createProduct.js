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

        let formData = getFormInputValue();

        let pass = true;
        if($("input[name='product.productName']").val() == ''){
            showMessage({element:$("input[name='product.productName']"), message: "제품명을 입력해주세요.", isError: true});
            pass = false;
        }
        if($("input[name='product.productName']").attr("data") != 'true'){
            pass = false;
        }
        if($("input[name='product.productCode']").val() == ''){
            showMessage({element:$("input[name='product.productCode']"), message: "제품코드를 입력해주세요.", isError: true});
            pass = false;
        }
        if($("input[name='product.productCode']").attr("data") != 'true'){
            pass = false;
        }
        if($("input[name='product.productWeight']").val() == ''){
            
            showMessage({element:$("input[name='product.productWeight']").parent(".input-group"), message: "제품무게를 입력해주세요.", isError: true});
            pass = false;
        }

        if(!pass) return;

        if($("[name='productImage']").val() != ''){ // 이미지파일 있으면
            let canvas = $("#edit-img").cropper('getCroppedCanvas');
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
                appendRegisteredContainer(resultData);
                initFormInputValue();
            });
        }else{
            let resultData = loadPostMultipartData({
                url: "/product/create",
                data: formData,
                header: {
                    'header': header,
                    'token': token
                }
            });
            appendRegisteredContainer(resultData);
            initFormInputValue();
        }
        
        
        
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
        $(".input-message").css("display","none");
        $(".card-container .img-view").empty();
        $(".card-container .img-view").text("이미지를 선택해주세요");
    }        
    

    $("input[name='product.productName']").focusout(function(){
        
        if($(this).val() == ""){
            showMessage({
                element: $(this),
                message: "제품명을 입력해주세요.",
                isError: true,
            })
            return;
        }
        hideMessage($(this));

        let response = loadGetData({
            url: "/product/check/name",
            data: {"productName": $(this).val()}
        });

        showMessage({
            element: $(this),
            message: response.message,
            isError: !response.result,
        });
        
        $(this).attr("data", response.result);
    });


    $("input[name='product.productCode']").focusout(function(){

        if($(this).val() == ""){
            showMessage({element: $(this), message: "제품코드를 입력해주세요.", isError: true});
            return;
        }
        hideMessage($(this));
        let response = loadGetData({
            url: "/product/check/code",
            data: {"productCode": $(this).val()}
        });
        showMessage({
            element: $(this),
            message: response.message,
            isError: !response.result,
        });
        $(this).attr("data", response.result);
    });

    $("input[name='product.productWeight']").focusout(function(){
        if($(this).val() == ""){
            
            showMessage({element: $(this).parent(".input-group"), message: "제품무게를 입력해주세요.", isError: true});
            return;
        }
        hideMessage($(this).parent(".input-group"));
    });



    const showMessage = function({element, message, isError}){
        let color = (isError) ? "rgb(255, 0, 62)" : "#a8c6ff";
        $(element).next(".input-message").css({
            "display": "block",
            "color": color
        });
        $(element).next(".input-message").text(message);
        $(element).css("border-color",color);
    }

    const hideMessage = function(element){
        $(element).next(".input-message").css("display","none");
        $(element).css("border-color","#a8c6ff");
    }


    $(".measurement-btn").click(function(){
        console.log("clicked");

        const serialNumber = prompt("매트 시리얼번호");
        if(serialNumber == ''){
            alert('매트 시리얼번호를 입력해주세요.');
            return;
        }

        const data = loadGetData({
            url: "/api/device",
            data:{
                "serialNumber": serialNumber
            }});
        
        console.log(data);
        if(!data){
            alert("상품을 매트에 올려주세요.");
            return;
        }
        $("input[name='product.productWeight']").val(data.inventoryWeight);

    });

});

