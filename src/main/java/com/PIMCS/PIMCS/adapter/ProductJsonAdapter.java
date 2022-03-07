package com.PIMCS.PIMCS.adapter;


import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
public class ProductJsonAdapter {
    private int id;
    private Company company;
    private ProductCategory productCategory;
    private String productCode;
    private String productImage;
    private int productWeight;
    private String productName;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedate;
    @Data
    @Builder
    @AllArgsConstructor
    public static  class  Company{
        private int id;
        private String companyCode;
        private String companyName;
        private String companyAddress;
        private String contactPhone;
        private String ceoEmail;
    }
    @Data
    @Builder
    @AllArgsConstructor
    public static class ProductCategory {
        private int id;
        private String categoryName;

    }
}
