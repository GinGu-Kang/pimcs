package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.APIServiceUtils;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class APIService {
    private final ProductRepository productRepository;

    @Autowired
    public APIService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<ProductJsonAdapter> loadProductsService(Company company){
        APIServiceUtils apiServiceUtils = new APIServiceUtils();
        List<Product> products =productRepository.findByCompany(company);
        return apiServiceUtils.creteProductJsonAdapter(products);
    }

}
