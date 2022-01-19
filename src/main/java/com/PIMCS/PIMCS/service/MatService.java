package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.MatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatService {
    private final MatRepository matRepository;

    @Autowired
    public MatService(MatRepository matRepository) {
        this.matRepository = matRepository;
    }


    /**
     * 매트생성 서비스
     * @throws IllegalStateException 사용할수없는 시리얼번호인 경우 발생
     */
    public String createMat(Mat mat){

        //db에 삽입
        matRepository.save(mat);
        return mat.getSerialNumber();
    }

    /**
     * 매트읽기 서버스
     */
    public List<Mat> readMat(Mat mat){

        return null;
    }

    /**
     * 매트수정 서비스
     */
    public String updateMat(Mat mat){
        matRepository.save(mat);
        return mat.getSerialNumber();
    }

    /**
     * 매트삭제 서비스
     */
    public String deleteMat(Mat mat){
        return null;
    }

    /**
     * 매트 serial num 체크 서버스
     * @return hashMap
     *      hashMap.get('result'): true면 serialNumber 사용가능, false면 serialNumber 사용불가능
     */
    public HashMap<String,Boolean> checkMatSerialNumberService(String serialNumber){
        HashMap<String,Boolean> hashMap = new HashMap<>();
        Optional<Mat> optMat = matRepository.findBySerialNumber(serialNumber);

        if(optMat.isPresent()){ //null 값이아니면
            hashMap.put("result", false);
        }else{
            hashMap.put("result", true);
        }
        return hashMap;
    }

    /**
     * 검색(serialNumber,matLocation,productCode,matVersion) 서비스
     */
    public List<Mat> searchMat(SearchForm searchForm){

        List<Mat> matList = null;
        switch (searchForm.getSearchType()){
            case "serialNumber":
                matList = matRepository.findBySerialNumberContaining(searchForm.getSearchQuery());
                break;
            case "matLocation":
                matList = matRepository.findByMatLocationContaining(searchForm.getSearchQuery());
                break;
            case "productCode":
                System.out.println("개발중");
                break;
            case "matVersion":
                matList = matRepository.findBySerialNumberContaining(searchForm.getSearchQuery());
                break;

        }

        return matList;
    }


}
