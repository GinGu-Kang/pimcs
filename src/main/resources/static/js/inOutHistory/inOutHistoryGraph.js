
let nextIndex = 0;
const CHECKED_MAT_STORAGE_KEY = "checkedMat";

/**
 *  체크된 매트값 설정
 */
const setCheckedData = function(matGraphForms){
    matGraphForms = JSON.parse(matGraphForms.replaceAll("&quot;","\""));
    //GET방시 로드시
    if(matGraphForms['serialNumberList'] == null && matGraphForms['productNameList'] == null){
        matGraphForms = getLocalStorage(CHECKED_MAT_STORAGE_KEY);
    }
    console.log(matGraphForms['timeDimension']);
    //HOUR, DAY, WEEK, MONTH 설정
    if(matGraphForms['timeDimension'] != null){
        
        $(".time-dimension").removeClass("active");
        switch(matGraphForms['timeDimension']){
            
            case 'HOUR':
                
                $("#hour-btn").addClass("active");
                break;
            case 'DAY':
                $("#day-btn").addClass("active");
                break;
            case 'WEEK':
                $("#week-btn").addClass("active");
                break;
            case 'MONTH':
                $("#month-btn").addClass("active");
                break;
        }
    }

    //시작시간 설정
    if(matGraphForms['startDate'] != null){
        let startDateYearStr = `${matGraphForms['startDate'][0]}`;
        let startDateMonthStr = `${(matGraphForms['startDate'][1] < 10) ? '0'+matGraphForms['startDate'][1] : matGraphForms['startDate'][1]}`;
        
        if(matGraphForms['timeDimension'] == "HOUR" || matGraphForms['timeDimension'] == "DAY"){
            let startDateDayStr = `${(matGraphForms['startDate'][2] < 10) ? '0'+matGraphForms['startDate'][2] : matGraphForms['startDate'][2]}`;
            $("#start-date").val(`${startDateYearStr}-${startDateMonthStr}-${startDateDayStr}`);
        }else{
            $("#start-date").attr("type","month");
            $("#start-date").val(`${startDateYearStr}-${startDateMonthStr}`);
        }
        
        
    }
    //종료시간 설정
    if(matGraphForms['endDate'] != null){
        let endDateYearStr = `${matGraphForms['endDate'][0]}`;
        let endDateMonthStr = `${(matGraphForms['endDate'][1] < 10) ? '0'+matGraphForms['endDate'][1] : matGraphForms['endDate'][1]}`;
        
        if(matGraphForms['timeDimension'] == "HOUR" || matGraphForms['timeDimension'] == "DAY"){
            let endDateDayStr = `${(matGraphForms['endDate'][2] < 10) ? '0'+matGraphForms['endDate'][2] : matGraphForms['endDate'][2]}`;
            $("#end-date").val(`${endDateYearStr}-${endDateMonthStr}-${endDateDayStr}`);
        }else{
            $("#end-date").attr("type","month");
            $("#end-date").val(`${endDateYearStr}-${endDateMonthStr}`);
        }
        
    }
    

    localStorage.removeItem(CHECKED_MAT_STORAGE_KEY);
    setLocalStorage(CHECKED_MAT_STORAGE_KEY,matGraphForms);
    nextIndex = 0;
}

const setLocalStorage = function(key, value){

    localStorage.setItem(key,JSON.stringify(value));
}

const getLocalStorage = function(key){
    return JSON.parse(localStorage.getItem(key));
}


/**
 * 그래프 지연 로딩
 */
const lazyLoadGraph = new IntersectionObserver(entries => {
                    
    if (entries.some(entry => entry.intersectionRatio > 0)) {
        
        console.log("detect");
        
        if($(".time-dimension.active").text() == "HOUR"){
            resultData = loadGraphData("/inout/history/graph/hour",2, true);
    
        }else if($(".time-dimension.active").text() == "DAY"){
            resultData = loadGraphData("/inout/history/graph/day",2, true);
        }else if($(".time-dimension.active").text() == "WEEK"){
            resultData = loadGraphData("/inout/history/graph/week",2, true);
        }else if($(".time-dimension.active").text() == "MONTH"){
            resultData = loadGraphData("/inout/history/graph/month",2, true);
        }
        if(resultData != undefined)
            createGraphDay(resultData);
    }
});


/**
 * 그래프 데이터 로드
 */
const loadGraphData = function(url,size,isLazyLoad){
    
    if(!isLazyLoad) nextIndex =0;
    
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    let drawGraphMatForm = getLocalStorage(CHECKED_MAT_STORAGE_KEY);

    console.log(drawGraphMatForm);
    let serialNumberList = drawGraphMatForm['serialNumberList'];
    let productNameList = drawGraphMatForm['productNameList'];

    //storage 내용 업데이트
    let storage = getLocalStorage(CHECKED_MAT_STORAGE_KEY);
    let startDate = [];
    let endDate = [];
    for(let date of $("#start-date").val().split('-')){
        startDate.push(parseInt(date));
    }
    for(let date of $("#end-date").val().split('-')){
        endDate.push(parseInt(date));
    }
    storage['startDate'] = startDate;
    storage['endDate'] = endDate;
    storage['timeDimension'] = $(".time-dimension.active").text();
    setLocalStorage(CHECKED_MAT_STORAGE_KEY, storage);

    console.log("storage");
    console.log(storage);

    //지연로드할 그래프가 없으면 return undefined 
    if(nextIndex >= serialNumberList.length) return undefined;

    let formData = createForm(
        serialNumberList.slice(nextIndex, nextIndex+size),
        productNameList.slice(nextIndex, nextIndex+size)
    )
    let queryString = new URLSearchParams(formData).toString();
    
    $(".loading-spinner").show();
    let resultData = loadPostData({
        url: url,
        data: queryString,
        header: {
            'header': header,
            'token': token
        }
    });
    $(".loading-spinner").hide();
    nextIndex += size;
    
    return  resultData;
}

/**
 * 일별 그래프생성
 */
const createGraphDay = function(graphDataArr){
    console.log(graphDataArr);
    $(".canvas-container").empty();
    for(let i=0; i<graphDataArr.length; i++){
        let graphData = graphDataArr[i];
        const config = getGraphConfig({
            label: graphData['productName'],
            labels: graphData['labels'],
            graphData: graphData['data']
        })     
    
        const uuid = getUUID();
        let canvas = `<canvas id="${uuid}"></canvas>`
        $(".canvas-container").append(canvas);
    
        new Chart(
                    document.getElementById(uuid),
                    config
                );

    }
}
const getUUID = function(){
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );    
}

/**
 * 그래프 설정 가져오기
 * @param  label 그래프명
 * @param  labels x축
 * @param  graphData 그래프값
 * @returns graph config
 */
const getGraphConfig = function({label, labels, graphData}){
    
    const data = {
        labels: labels,
        datasets: [{
        label: label,
        backgroundColor: 'rgb(255, 99, 132)',
        borderColor: '#209cee',
        data: graphData
        }]
    };

    return {
        type: 'line',
        data: data,
        options: {}
    };
}
const createForm = function(serialNumberSubList, productNameSubList){
    if(serialNumberSubList.length != productNameSubList.length){
        throw new Error("Can not create form.");
    }

    let formData = new FormData();
    
    let startDateStr = $("#start-date").val();
    let endDateStr = $("#end-date").val();
    if(startDateStr.length == 7) startDateStr += "-01";
    if(endDateStr.length == 7){
        let curDate = new Date();
        let endDate = new Date(endDateStr);
        if(endDate.getMonth() == curDate.getMonth()){
            if(curDate.getDate() < 10){
                endDateStr += `-0${curDate.getDate()}`;
            }else{
                endDateStr += `-${curDate.getDate()}`;
            }
        }
    }

    formData.append("startDate",startDateStr);
    formData.append("endDate",endDateStr);
    formData.append("timeDimension",$(".time-dimension.active").text());
    for(let i=0; i<serialNumberSubList.length ; i++){
        formData.append(`serialNumberList[${i}]`,serialNumberSubList[i]);
        formData.append(`productNameList[${i}]`,productNameSubList[i]);
    }
    return formData;
}



/**
 *  조회버튼 클릭시
 */
$(document).on("click",".inquiry-btn",function(e){
    e.preventDefault();
    let startDate = new Date($("#start-date").val());
    let endDate = new Date($("#end-date").val());
    let curDate = new Date();

    
    if(startDate > endDate){
        alert("시작날짜가 종료날짜보다 큽니다.");
    }
    if(startDate > curDate){
        alert("시작날짜가 현재날짜보다 큽니다.")
    }
    if(endDate > curDate){
        alert("종료날짜가 현재날짜보다 큽니다.")
    }
    let resultData;
    if($(".time-dimension.active").text() == "HOUR"){
        let dayMS = 24 * 60 * 60 * 1000;
        console.log("hihihihii");
        let diffMS = endDate - startDate;
        if(diffMS > dayMS * 3){ //차이가 3일보다 크면
            alert("시별조회는 3일이하만 조회할수있습니다.");
        }else{
            resultData = loadGraphData("/inout/history/graph/hour",2, false);

        }
    }else if($(".time-dimension.active").text() == "DAY"){
        resultData = loadGraphData("/inout/history/graph/day",2, false);
    }else if($(".time-dimension.active").text() == "WEEK"){
        resultData = loadGraphData("/inout/history/graph/week",2, false);
    }else if($(".time-dimension.active").text() == "MONTH"){
        resultData = loadGraphData("/inout/history/graph/month",2, false);
    }

    

    


    if(resultData != undefined)
            createGraphDay(resultData);
});


/**
 * HOUR, DAY, WEEK, MONTH버튼 클릭
 */
$(document).on("click",".time-dimension",function(e){
    e.preventDefault();
    $(".time-dimension").removeClass("active");
    $(this).addClass("active");

    let timeDimension = $(this).text();
    if(timeDimension == "HOUR"){
        $("#start-date").attr("type","date");
        $("#end-date").attr("type","date");
        let curDate = new Date();
        $("#end-date").val(curDate.toISOString().split('T')[0]);
        $("#start-date").val(curDate.toISOString().split('T')[0]);

    }else if(timeDimension == "DAY"){
        $("#start-date").attr("type","date");
        $("#end-date").attr("type","date");

        let curDate = new Date();
        $("#end-date").val(curDate.toISOString().split('T')[0]);
        curDate.setDate(1);
        $("#start-date").val(curDate.toISOString().split('T')[0]);

    }else if(timeDimension == "WEEK"){
        $("#start-date").attr("type","month");
        $("#end-date").attr("type","month");
        
        let start = new Date();
        start.setMonth(start.getMonth()-1);
        $("#start-date").val(toISOMonth(start));
        $("#end-date").val(toISOMonth(new Date()));
    }else if(timeDimension == "MONTH"){
        $("#start-date").attr("type","month");
        $("#end-date").attr("type","month");

        let end = new Date();
        let start = new Date(end.getFullYear(),0);

        $("#start-date").val(toISOMonth(start));
        $("#end-date").val(toISOMonth(end));
    }

});

const toISOMonth = function(date){
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    if(month < 10){
        month = "0"+month;
    }
    return `${year}-${month}`;
}