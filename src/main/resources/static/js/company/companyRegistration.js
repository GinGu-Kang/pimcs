function check_password(){
    var checkText=$('#pw-check-text');
    var verifyText=$('#pw-verify-text');
    var password =$('#password').val();
    var passwordVerify =$('#password-verify').val();
    var eng = password.search(/[a-z]/g);
    var spe=password.search(/[`~!@@#$%^&*|₩₩₩'₩";:₩/?]/g);
    var num=password.search(/[0-9]/g);

    if(spe == -1 || num == -1 || eng==-1 || password.length <= 8 || password.length>=16){
        checkText.css("color","red");
        checkText.css("display","inline-block");
        checkText.text("8~16자 영문, 숫자, 특수문자를 포함해서 사용하세요.");
        return false
    }else {
        checkText.css("color","#6A82FB");
        checkText.text("사용 가능한 비밀번호 입니다.");
    }

    if(password !='' & passwordVerify!=''){
        if(password==passwordVerify){
            verifyText.css("color","#6A82FB");
            verifyText.css("display", "inline-block");
            verifyText.text("확인 되었습니다.");
        }else{
            verifyText.css("color","red");
            verifyText.css("display", "inline-block");
            verifyText.text("비밀번호가 일치하지 않습니다.");
            return false
        }
    }else if(password=='' || passwordVerify==''){
        return false
    }

    return true
}


function checkForm(){

    if($('.email_ok').css("display")=="none"){
        $('#email').focus();
        alert("이메일을 확인 해주세요.");
        return false
    }else if(!check_password()){
        $('#password').focus();
        alert("비밀번호를 확인 해주세요.");
        return false
    } else{
        return true
    }

}
function checkEmail(){
    var email = $('#email').val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var form = $("#signUp")


    $.ajax({
        url:'/auth/idCheck',
        type:'post',
        data:{email:email},
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success:function(isEmail){ //컨트롤러에서 넘어온 cnt값을 받는다
            if(isEmail){ //cnt가 1이 아니면(=0일 경우) -> 사용 가능한 아이디
                $('.email_already').css("display", "none");
                $('.email_ok').css("display","inline-block");

            } else { // cnt가 1일 경우 -> 이미 존재하는 아이디
                $('.email_already').css("display","inline-block");
                $('.email_ok').css("display", "none");
            }
        },
        error:function(){
            alert("에러입니다");
        }
    });
};

function checkCompany(){
    var company = $('#company').val();
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    var form = $("#signUp")


    $.ajax({
        url:'/auth/companyCheck',
        type:'post',
        data:{company:company},
        beforeSend : function(xhr)
        {
            xhr.setRequestHeader(header, token);
        },
        success:function(isCompany){ //컨트롤러에서 넘어온 cnt값을 받는다
            if(!isCompany){ //cnt가 1이 아니면(=0일 경우) -> 사용 가능한 아이디

                $('.company_already').css("display", "none");
                $('.company_ok').css("display","inline-block");

            } else { // cnt가 1일 경우 -> 이미 존재하는 아이디
                $('.company_already').css("display","inline-block");
                $('.company_ok').css("display", "none");

            }
        },
        error:function(){
            alert("에러입니다");
        }
    });
};





