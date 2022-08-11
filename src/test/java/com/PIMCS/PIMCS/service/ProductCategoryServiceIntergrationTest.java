package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ProductCategoryServiceIntergrationTest {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private  GenerateEntity generateEntity;

    @Autowired
    private CompanyRepository companyRepository;

    private  Company company;

    private  ProductCategory productCategory;

    private  List<ProductCategory> productCategories;

    private  int maxSize = 200;

    @BeforeAll
    public void start(){
        company = generateEntity.createCompany(true);
    }

    @BeforeEach
    public void setup(){
        productCategories = new ArrayList<>();
        for(int i=0; i<maxSize; i++){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategoryName(UUID.randomUUID().toString().substring(0,30));
            productCategories.add(productCategory);
        }
    }

    @Test
    public void create(){
        for(ProductCategory productCategory : productCategories){
            productCategoryService.createProductCategoryService(company,productCategory);
        }


        for(ProductCategory productCategory : productCategories){
            ProductCategory find = productCategoryRepository.findById(productCategory.getId()).orElse(null);
            Assertions.assertNotNull(find);
        }
    }

    @Test
    public void delete(){
        create();
        for(ProductCategory productCategory : productCategories){
            productCategoryService.deleteProductCategoryService(company,productCategory);
        }

        for(ProductCategory productCategory : productCategories){
            ProductCategory find = productCategoryRepository.findById(productCategory.getId()).orElse(null);
            Assertions.assertNull(find);
        }
    }




    @AfterAll
    public void completeAllTest(){
        companyRepository.delete(company);
    }
}
