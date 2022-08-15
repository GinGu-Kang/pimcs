package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.APIServiceUtils;
import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductCategoryJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductPageJsonAdapter;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.domain.Redis.Device;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import com.PIMCS.PIMCS.repository.MatRepository;
import com.PIMCS.PIMCS.repository.OwnDeviceRepository;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.PIMCS.PIMCS.repository.Redis.DeviceRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class APIService {
    private final ProductRepository productRepository;
    private final MatRepository matRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final DynamoDBMapper dynamoDBMapper;
    private final DynamoQuery dynamoQuery;
    private final DeviceRepository deviceRepository;
    private final OwnDeviceRepository ownDeviceRepository;

    @Autowired
    public APIService(ProductRepository productRepository, MatRepository matRepository, ProductCategoryRepository productCategoryRepository, DynamoDBMapper dynamoDBMapper, DynamoQuery dynamoQuery, DeviceRepository deviceRepository, OwnDeviceRepository ownDeviceRepository) {
        this.productRepository = productRepository;
        this.matRepository = matRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoQuery = dynamoQuery;
        this.deviceRepository = deviceRepository;
        this.ownDeviceRepository = ownDeviceRepository;
    }

    /**
     * 회사에 등록된 전체제품 로드
     */
    public List<ProductJsonAdapter> findProductsService(Company company) {
        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        List<Product> products = productRepository.findByCompany(company);
        return apiServiceUtils.creteProductJsonAdapter(products);
    }

    /**
     * 회사에 등록된 제품 Page 로드
     */
    public ProductPageJsonAdapter findPageProductsService(Company company, Pageable pageable) {
        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        Page<Product> pageProducts = productRepository.findByCompanyOrderByCreatedAtDesc(company, pageable);

        return apiServiceUtils.creteProductPageJsonAdapter(pageProducts);
    }


    /**
     * 매트검색 API
     */
    public MatPageAdapter findMatListByAllService(SearchForm searchForm, Company company, Pageable pageable) {

        MatPageAdapter matPageAdapter = null;
        switch (searchForm.getSearchType()) {
            case "serialNumber":
            case "matVersion":
                Page<Mat> pageMats = matRepository.findByCompanyAndSerialNumberContaining(company, searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats, company, dynamoDBMapper, dynamoQuery);
                break;
            case "matLocation":
                Page<Mat> pageMats2 = matRepository.findByCompanyAndMatLocationContaining(company, searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats2, company, dynamoDBMapper, dynamoQuery);
                break;
            case "productCode":
                Page<Mat> pageMats3 = matRepository.findByCompanyAndProductProductCodeContaining(company, searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats3, company, dynamoDBMapper, dynamoQuery);
                break;
            case "productName":
                Page<Mat> pageMats4 = matRepository.findByCompanyAndProductProductNameContaining(company, searchForm.getSearchQuery(), pageable);
                matPageAdapter = new APIServiceUtils().createMatPageAdapter(pageMats4, company, dynamoDBMapper, dynamoQuery);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + searchForm.getSearchType());
        }
        return matPageAdapter;
    }

    public ProductPageJsonAdapter searchProductsService(SearchForm searchForm, Company company, Pageable pageable) {
        ProductPageJsonAdapter productPageJsonAdapter = null;

        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        switch (searchForm.getSearchType()) {
            case "productCode":
                Page<Product> pageProducts = productRepository.findByCompanyAndProductCodeContainingOrderByCreatedAtDesc(company, searchForm.getSearchQuery(), pageable);
                productPageJsonAdapter = apiServiceUtils.creteProductPageJsonAdapter(pageProducts);
                break;
            case "productName":
                Page<Product> pageProducts2 = productRepository.findByCompanyAndProductNameContainingOrderByCreatedAtDesc(company, searchForm.getSearchQuery(), pageable);
                productPageJsonAdapter = apiServiceUtils.creteProductPageJsonAdapter(pageProducts2);
                break;
            case "productCategory":
                Page<Product> pageProducts3 = productRepository.findByCompanyAndProductCategoryCategoryNameContainingOrderByCreatedAtDesc(company, searchForm.getSearchQuery(), pageable);
                productPageJsonAdapter = apiServiceUtils.creteProductPageJsonAdapter(pageProducts3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + searchForm.getSearchType());
        }
        return productPageJsonAdapter;
    }


    public MatPageAdapter findMatListService(Company company, Pageable pageable){
        Page<Mat> pageMats = matRepository.findByCompanyOrderByIdDesc(company, pageable);

        return new APIServiceUtils().createMatPageAdapter(pageMats,company, dynamoDBMapper,dynamoQuery);
    }

    public MatPageAdapter findMatListByThresholdLte(Company company, Pageable pageable){
        Page<Mat> pageMats = matRepository.findMatsBelowThreshold(company,pageable);
        System.out.println(pageMats);
        return new APIServiceUtils().createMatPageAdapter(pageMats, company, dynamoDBMapper,dynamoQuery);
    }

    public List<ProductCategoryJsonAdapter> findProductsCategoryService(Company company){
        List<ProductCategory> productCategories = productCategoryRepository.findByCompany(company);
        return new APIServiceUtils().createProductCategoryJsonAdapter(productCategories, company);
    }

    public List<OrderMailRecipients> findMatsEmailService(List<String> serialNumbers, Company company){
        return OrderMailRecipients.findBySerialNumbers(dynamoQuery, serialNumbers, company);
    }

    public Device findDeviceListBySerialNumberService(String serialNumber){

        Optional<Device> opt = deviceRepository.findById(serialNumber);
        return opt.orElse(null);
    }


    public ValidationForm findMatListBySerialNumberService(Company company, String serialNumber){
//        HashMap hashMap = new HashMap<>();
        Optional<Mat> optMat = matRepository.findBySerialNumber(serialNumber);
        OwnDevice ownDevice = ownDeviceRepository.findByCompanyAndSerialNumber(company,serialNumber).orElse(null);

        if(optMat.isPresent()){ //null 값이아니면
            return  new ValidationForm(false, "매트가 이미 등록되어 있습니다.");
        }
        if(ownDevice == null) {
            return new ValidationForm(false, "소유 기기가 아니거나 존재하지않는 기기입니다.");
        }
        return new ValidationForm(true, "등록할 수 있는 시리얼번호 입니다.");
    }
}
