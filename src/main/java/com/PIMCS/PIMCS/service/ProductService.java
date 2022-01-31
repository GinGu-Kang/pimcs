package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.form.SearchForm;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품생성 서비스
     */
    public String createProduct(Product product){

        productRepository.save(product);
        return product.getProdCode();
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
        return product.getProdCode();
    }

    /**
     * 상품삭제 서비스
     */
    public String deleteProduct(Product product){

        productRepository.delete(product);
        return product.getProdCode();
    }

    /**
     * 상품조회 서비스
     * product(상품코드, 상품명, 상품카테고리, 상품위치)
     */
    public List<Product> searchProducts(SearchForm searchForm){

        return null;
    }

}
