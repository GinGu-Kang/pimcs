package com.PIMCS.PIMCS;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;

import java.sql.Timestamp;


public class TestUtils {


    public Mat createMatObject(Product product){
        Mat mat = new Mat();
        mat.setSerialNumber(System.currentTimeMillis()+"");
//        mat.setSerialNumber("1642661076618");
        mat.setProduct(product);
        mat.setCompanyCode("code123");
        mat.setCalcMethod(1);
        mat.setThreshold(5);
        mat.setInventoryWeight(0);
        mat.setRecentlyNoticeDate(new Timestamp(System.currentTimeMillis()));
        mat.setIsSendEmail(0);
        mat.setMatLocation("화상실");
        mat.setProductOrderCnt(2);
        mat.setBoxWeight(5);
        mat.setBattery(10);
        return mat;
    }

    public Product createProductObject(Company company){

            Product product = new Product();
            product.setProdCode(System.currentTimeMillis()+"");
            product.setCompany(company);
            product.setProdCategoryId(1);
            product.setProdImage(null);
            product.setProdWeight(10);
            product.setProdName("컴퓨터"+System.currentTimeMillis());
            return product;
        }



    public Company createCompantObject(){
        Company company = new Company();
        company.setCompanyCode(System.currentTimeMillis()+"");
        company.setBusinessCategoryName("sw");
        company.setCompanyName("sw");
        company.setCompanyAddress("서울시");
        company.setContactPhone("010xxxx");
        company.setCeoEmail("2543817958@qq.com");
        return company;
    }
}
