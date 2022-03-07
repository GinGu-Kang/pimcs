package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Product;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.parameters.P;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

public class APIServiceUtils {

    public List<ProductJsonAdapter> creteProductJsonAdapter(List<Product> products){
        List<ProductJsonAdapter> result = new ArrayList<>();
        for(Product product : products){
            //Company 객체 생성
            ProductJsonAdapter.Company company = ProductJsonAdapter.Company.builder()
                    .id(product.getCompany().getId())
                    .companyCode(product.getCompany().getCompanyCode())
                    .companyName(product.getCompany().getCompanyName())
                    .companyAddress(product.getCompany().getCompanyAddress())
                    .contactPhone(product.getCompany().getContactPhone())
                    .ceoEmail(product.getCompany().getCeoEmail())
                    .build();

            ProductJsonAdapter.ProductCategory productCategory = ProductJsonAdapter.ProductCategory.builder()
                    .id(product.getProductCategory().getId())
                    .categoryName(product.getProductCategory().getCategoryName())
                    .build();

            ProductJsonAdapter productJsonAdapter = ProductJsonAdapter.builder()
                    .id(product.getId())
                    .company(company)
                    .productCategory(productCategory)
                    .productCode(product.getProductCode())
                    .productImage(product.getProductImage())
                    .productWeight(product.getProductWeight())
                    .productName(product.getProductName())
                    .createdAt(product.getCreatedAt())
                    .updatedate(product.getUpdatedate())
                    .build();

            result.add(productJsonAdapter);
        }

        return result;
    }
}
