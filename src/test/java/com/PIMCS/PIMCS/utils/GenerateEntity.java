package com.PIMCS.PIMCS.utils;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateEntity {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    public Company createCompany(boolean isSave){

        Company company = new Company();
        company.setCompanyCode(String.valueOf(UUID.randomUUID()).substring(0,29));
        company.setBusinessCategoryId(null);
        company.setCompanyName(String.valueOf(UUID.randomUUID()));
        company.setCompanyAddress("abcd");
        company.setContactPhone("1234");
        company.setCeoEmail("test@pimcs.com");
        company.setCeoName("jsiojio");
        if(isSave) companyRepository.save(company);

        return company;
    }

    public ProductCategory createProductCategory(Company company, boolean isSave){

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName(UUID.randomUUID().toString().substring(0,30));
        if(company != null)
            productCategory.setCompany(company);
        else
            productCategory.setCompany(createCompany(isSave));


        if(isSave) productCategoryRepository.save(productCategory);
        return productCategory;


    }


    public User createUser(boolean isSave){
        User user = new User();
        user.setEmail(UUID.randomUUID()+"@pimcs.com");
        user.setCompany(createCompany(isSave));
        user.setPassword(String.valueOf(UUID.randomUUID()));
        user.setName("test");
        user.setPhone("1234");
        user.setDepartment("sw");
        user.setEnabled(true);

        return user;
    }



    public Product createProduct(Company company,  boolean isSave){

        Product product = new Product();
        product.setProductCode(String.valueOf(UUID.randomUUID()).substring(0,30));
        product.setProductCategory(null);
        product.setCompany((company == null) ? createCompany(isSave) : company);
        product.setProductImage(null);
        product.setProductWeight(2);
        product.setProductName(String.valueOf(UUID.randomUUID()).substring(0,30));

        if(isSave) productRepository.save(product);

        return product;

    }
}
