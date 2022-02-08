package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MatService {
    private final MatRepository matRepository;
    private final ProductRepository productRepository;


    @Autowired
    public MatService(MatRepository matRepository, ProductRepository productRepository) {
        this.matRepository = matRepository;
        this.productRepository = productRepository;
    }

    /**
     * 매트 조회
     */
    public Page<Mat> readMatService(Company company, Pageable pageable){
        Page<Mat> mats = matRepository.findByCompany(company, pageable);
        return mats;
    }

    /**
     * 매트생성 서비스
     * @throws IllegalStateException 사용할수없는 시리얼번호인 경우 발생
     */
    public String createMat(Mat mat){
        //유효성 검사
        HashMap<String,Object> resultMap = checkMatSerialNumberService(mat.getSerialNumber());
        if(!(boolean)resultMap.get("result")){ //등록할수 없는 매트일때
            throw new IllegalStateException(resultMap.get("message").toString());
        }

        //db에 삽입
        matRepository.save(mat);
        return mat.getSerialNumber();
    }

    /**
     * 매트읽기 서버스
     */
    public List<Mat> readMat(){
        List<Mat> mats = matRepository.findAll();
        return mats;
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

        matRepository.delete(mat);
        return mat.getSerialNumber();
    }

    /**
     * 매트 serial num 체크 서버스
     * @return hashMap
     *      hashMap.get('result'): true면 serialNumber 사용가능, false면 serialNumber 사용불가능
     */
    public HashMap checkMatSerialNumberService(String serialNumber){
        HashMap hashMap = new HashMap<>();
        Optional<Mat> optMat = matRepository.findBySerialNumber(serialNumber);

        if(optMat.isPresent()){ //null 값이아니면
            hashMap.put("result", false);
            hashMap.put("message","매트가 이미 등록되어 있습니다.");
        }else{
            hashMap.put("result", true);
            hashMap.put("message","등록할 수 있는 시리얼번호 입니다.");
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

                List<Product> products =productRepository.findByProductCodeContaining(searchForm.getSearchQuery());
                List<Mat> finalMatList =  new ArrayList<>();
                products.forEach(p ->{
                    finalMatList.addAll(p.getMats());
                });
                matList = finalMatList;

                break;
            case "matVersion":
                matList = matRepository.findBySerialNumberContaining(searchForm.getSearchQuery());
                break;
        }

        return matList;
    }


}
