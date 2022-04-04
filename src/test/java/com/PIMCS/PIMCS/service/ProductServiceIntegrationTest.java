package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.TestUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ProductService productService;

    TestUtils testUtils = new TestUtils();

//    @Test
//    public void createProductTest(){
//
//        //given
//         Company company =  testUtils.createCompantObject();
//         Product product = testUtils.createProductObject(company);
//
//         //when
//         companyRepository.save(company);
//         String prodCode = productService.createProduct(product);
//
//         //then
//         Optional<Product> productOpt = productRepository.findByProductCode(prodCode);
//
//         if(!productOpt.isPresent()){
//             Assertions.fail("정상등록 되지 않았습니다.");
//         }
//
//    }
    @Test
    public void readProductTest(){
//        org.assertj.core.api.Assertions.assertThat(productService.readProduct().size()).isEqualTo(5);
//        productService.read().forEach(product -> {
//            System.out.println("productCode: "+ product.getProdCode() +", companyName: " + product.getCompany().getCompanyName());
//        });
    }


    @Test
    public void updateProductTest(){
        //given
        Product product = productRepository.findByProductCode("1642661076618").get();
        product.setProductCode("안녕");
        //when
        String prodCode = productService.updateProduct(product);
        //then
        Product updatedProduct = productRepository.findByProductCode(prodCode).get();
        org.assertj.core.api.Assertions.assertThat(updatedProduct.getProductName()).isEqualTo(product.getProductName());

    }

    @Test
    public void deleteProductTest(){
        //given
        Product product = productRepository.findByProductCode("1642661076618").get();

        //when
        String prodCode = productService.deleteProduct(product);
//
//        //then
        Optional<Product> deletedProduct = productRepository.findByProductCode(prodCode);
        if(deletedProduct.isPresent()){
           Assertions.fail("삭제실패");
        }

    }


    @Test
    public void fetchJoinTest(){
        //given
        Company company = companyRepository.findByCompanyCode("code123").get();
        List<Product> products = company.getProducts();

        products.forEach(product -> {
            System.out.println(product.getProductCode() + ", "+ product.getProductName());
        });

//        List<Company> companys = companyRepository.findAll();
//        companys.forEach(c ->{
//            c.getProducts().forEach(product -> {
//                System.out.println(product.getProdCode());
//            });
//        });


    }
}
