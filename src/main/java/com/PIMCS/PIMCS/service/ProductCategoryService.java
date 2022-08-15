package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<ProductCategory> readProductCategoryService(Company company){
        return productCategoryRepository.findByCompany(company);
    }

    public int createProductCategoryService(Company company, ProductCategory productCategory){
        productCategory.setCompany(company);
        productCategoryRepository.save(productCategory);

        return productCategory.getId();
    }

    public HashMap<String, String> deleteProductCategoryService(Company company, ProductCategory productCategory){

        Optional<ProductCategory> opt = productCategoryRepository.findByIdAndCompany(productCategory.getId(),company);
        if(opt.isPresent()){
            productCategoryRepository.delete(opt.get());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("message", "삭제완료 되었습니다.");
            return hashMap;
        }else {
            throw new IllegalStateException("Product category does not exist.");
        }
    }
}
