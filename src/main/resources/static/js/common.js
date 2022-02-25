


/**
 * 쿠키 저장
 * @param {*} name 쿠키 키값 
 * @param {*} value 저장될 값
 * @param {*} exp  쿠키유효기간(일), exp=1 1일
 */
const setCookie = function(name, value, exp) {
    const date = new Date();
    date.setTime(date.getTime() + exp*24*60*60*1000);
    document.cookie = name + '=' + value + ';expires=' + date.toUTCString() + ';path=/';
};

/**
 * 쿠키 가져오기
 * @param {*} name 쿠키 키값
 * @returns 저장된값이 있으면 값반환, 없으면 null
 */
const getCookie = function(name) {
    let value = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return value? value[2] : null;
};

/**
 * 쿠키 삭제
 * @param {*} name 쿠키 키값
 */
var deleteCookie = function(name) {
    document.cookie = name + '=; expires=Thu, 01 Jan 1999 00:00:10 GMT;';
}
    
/**
 * 비동기 통신 get메소드로 데이터 요청
 * @param {*} url 요청할 데이터 url 
 * @param {*} data 요청 파라미터
 * @returns 요청한 데이터 반환
 */    
const loadGetData = function({url, data}){

    let resultData = {};
    $.ajax({
        url: url,
        type:'GET',
        data:data,
        async: false, // 동기식으로 동작
        success:function(response){
            resultData = response;    
        },
        error:function(){
            alert("오류발생");
        }
    });
    return resultData;
}



    
    
    
    
    