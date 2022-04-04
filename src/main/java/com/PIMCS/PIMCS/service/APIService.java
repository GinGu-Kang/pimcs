package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.APIServiceUtils;
import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductPageJsonAdapter;
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


import java.util.List;

@Service
@Transactional
public class APIService {
    private final ProductRepository productRepository;
    private final MatRepository matRepository;

    @Autowired
    public APIService(ProductRepository productRepository, MatRepository matRepository) {
        this.productRepository = productRepository;
        this.matRepository = matRepository;
    }

    /**
     * 회사에 등록된 전체제품 로드
     */
    public List<ProductJsonAdapter> loadProductsService(Company company){
        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        List<Product> products =productRepository.findByCompany(company);
        return apiServiceUtils.creteProductJsonAdapter(products);
    }

    /**
     * 회사에 등록된 제품 Page 로드
     */
    public ProductPageJsonAdapter loadPageProductsService(Company company, Pageable pageable){
        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        Page<Product> pageProducts = productRepository.findByCompanyOrderByCreatedAtDesc(company,pageable);

        return apiServiceUtils.creteProductPageJsonAdapter(pageProducts);
    }

    public MatPageAdapter searchMatsService(SearchForm searchForm, Company company, Pageable pageable){

        MatPageAdapter matPageAdapter = null;
        switch (searchForm.getSearchType()){
            case "serialNumber":
            case "matVersion":
                Page<Mat> pageMats = matRepository.findByCompanyAndSerialNumberContaining(company,searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats,company);
                break;
            case "matLocation":
                Page<Mat> pageMats2 = matRepository.findByCompanyAndMatLocationContaining(company, searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats2,company);
                break;
            case "productCode":
                Page<Mat> pageMats3 = matRepository.findByCompanyAndProductProductCodeContaining(company, searchForm.getSearchQuery(),pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats3,company);
                break;
            case "productName":
                Page<Mat> pageMats4 = matRepository.findByCompanyAndProductProductNameContaining(company, searchForm.getSearchQuery(),pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats4,company);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + searchForm.getSearchType());
        }
        return matPageAdapter;
    }


    public MatPageAdapter loadPageMatsService(Company company, Pageable pageable){
        Page<Mat> pageMats = matRepository.findByCompanyOrderByIdDesc(company, pageable);

        return new APIServiceUtils().createMatPageAdapter(pageMats,company);
    }

    public MatPageAdapter belowThresholdMats(Company company, Pageable pageable){
        System.out.println("================");
        Page<Mat> pageMats = matRepository.findMatsBelowThreshold(company,pageable);
        System.out.println(pageMats.getTotalPages());
        System.out.println("================");
        return new APIServiceUtils().createMatPageAdapter(pageMats, company);
    }

}
