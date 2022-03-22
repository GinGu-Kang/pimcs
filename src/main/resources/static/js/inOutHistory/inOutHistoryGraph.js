
let checkedMatSerialNumberList;
let nextIndex = 0;

const setCheckedData = function(matForms){
    matForms = JSON.parse(matForms.replaceAll("&quot;","\""));

    checkedMatSerialNumberList = matForms['serialNumberList'];
    console.log(checkedMatSerialNumberList);
}

const lazyLoadGraph = new IntersectionObserver(entries => {
                    
    if (entries.some(entry => entry.intersectionRatio > 0)) {
        
        console.log("detect");
    }
});


const loadGraph = function(size){
    

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $(".loading-spinner").show();
    let resultData = loadPostData({
        url: "",
        data: queryString,
        header: {
            'header': header,
            'token': token
        }
    });
    $(".loading-spinner").hide();
    nextIndex += size;
}