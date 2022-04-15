package com.PIMCS.PIMCS.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProductCategoryJsonAdapter {
    private int id;
    private String categoryName;
    private Company company;

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
}
