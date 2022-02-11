package com.PIMCS.PIMCS.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MatPageAdapter {
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private List<Mat> data;
    @Data
    @Builder
    @AllArgsConstructor
    public static  class  Mat {
        private int id;
        private Product product;
        private Company company;
        private String serialNumber;
        private int calcMethod;
        private int threshold;
        private int inventoryWeight;
        private Timestamp recentlyNoticeDate;
        private String matLocation;
        private int productOrderCnt;
        private int boxWeight;
        private int battery;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Product{
        private int id;
        private String productCode;
        private String productImage;
        private int productWeight;
        private String productName;
    }
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
