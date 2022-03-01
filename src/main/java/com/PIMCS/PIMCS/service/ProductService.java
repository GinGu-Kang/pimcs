package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.FileUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.form.ProductForm;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    /**
     * 상품생성 서비스
     */
    public List<ProductCategory> createProductFormService(Company company){
        return productCategoryRepository.findByCompany(company);
    }

    /**
     * 제품생성 만약 카테고리가 존재하지않으면 에러발생
     * @param productForm 제품생성 폼데이터
     * @return 제품객체
     */
    public Product createProduct(ProductForm productForm, Company company){

        Optional<ProductCategory> optProductCategory = productCategoryRepository.findById(productForm.getProductCategoryId());

        //파일 저장
        String productImagePath = null;
        try {
            productImagePath = FileUtils.uploadFile(productForm.getProductImage());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload file.");
        }

        //제품정보 저장, 만약카테고리 존재하지 않으면 IllegalStateException 발생
        if(optProductCategory.isPresent()){
            Product product = productForm.getProduct();
            product.setProductCategory(optProductCategory.get());
            product.setCompany(company);
            product.setProductImage("/product/image/"+productImagePath);
            productRepository.save(product);
            return product;
        }else{
            throw new IllegalStateException("Category does not exist.");
        }
    }

    /**
     * 상품읽기 서비스
     */
    public List<Product> readProduct(){
        return productRepository.findAll();
    }

    /**
     * 상품수정 서비스
     */
    public String updateProduct(Product product){

        productRepository.save(product);
        return product.getProductCode();
    }

    /**
     * 상품삭제 서비스
     */
    public String deleteProduct(Product product){

        productRepository.delete(product);
        return product.getProductCode();
    }

    /**
     * 상품조회 서비스
     * product(상품코드, 상품명, 상품카테고리, 상품위치)
     */
    public List<Product> searchProducts(SearchForm searchForm){

        return null;
    }

}
