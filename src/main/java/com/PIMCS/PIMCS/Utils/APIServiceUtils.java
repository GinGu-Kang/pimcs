package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductCategoryJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.adapter.ProductPageJsonAdapter;
import com.PIMCS.PIMCS.domain.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ProductPageJsonAdapter creteProductPageJsonAdapter(Page<Product> pageProducts){
        List<ProductPageJsonAdapter.Product> result = new ArrayList<>();
        for(Product product : pageProducts){
            //Company 객체 생성
//            ProductPageJsonAdapter.Company company = null;
//            if(product.getCompany() != null) {
//                company = ProductPageJsonAdapter.Company.builder()
//                        .id(product.getCompany().getId())
//                        .companyCode(product.getCompany().getCompanyCode())
//                        .companyName(product.getCompany().getCompanyName())
//                        .companyAddress(product.getCompany().getCompanyAddress())
//                        .contactPhone(product.getCompany().getContactPhone())
//                        .ceoEmail(product.getCompany().getCeoEmail())
//                        .build();
//            }


            ProductPageJsonAdapter.ProductCategory productCategory = ProductPageJsonAdapter.ProductCategory.builder()
                    .id((product.getProductCategory() != null) ? product.getProductCategory().getId() : -1)
                    .categoryName((product.getProductCategory() != null) ? product.getProductCategory().getCategoryName() : "N/A")
                    .build();

            ProductPageJsonAdapter.Product product1 = ProductPageJsonAdapter.Product.builder()
                    .id(product.getId())
                    .company(null)
                    .productCategory(productCategory)
                    .productCode(product.getProductCode())
                    .productImage(product.getProductImage())
                    .productWeight(product.getProductWeight())
                    .productName(product.getProductName())
                    .createdAt(product.getCreatedAt())
                    .updatedate(product.getUpdatedate())
                    .build();


            result.add(product1);
        }
        ProductPageJsonAdapter productPageJsonAdapter = ProductPageJsonAdapter.builder()
                .pageNumber(pageProducts.getNumber() + 1)
                .pageSize(pageProducts.getSize())
                .totalPages(pageProducts.getTotalPages())
                .data(result)
                .build();
        return productPageJsonAdapter;
    }

    /**
     * mat 데이터를 json 직렬하기 편한 객체로 만들어준다
     */
    public MatPageAdapter createMatPageAdapter(Page<Mat> pageMats, Company company) {
        List<MatPageAdapter.Mat> resultMats = new ArrayList<>();
        //Entity 객체 사용하지않고 adapter 클래스를 만들어서 json 직렬화
        for (Mat mat : pageMats) {
            // adapter 상품데이터 추가
            MatPageAdapter.Product mProduct = null;
            if(mat.getProduct() != null){
                mProduct = MatPageAdapter.Product.builder()
                        .id(mat.getProduct().getId())
                        .productCode(mat.getProduct().getProductCode())
                        .productImage(mat.getProduct().getProductImage())
                        .productWeight(mat.getProduct().getProductWeight())
                        .productName(mat.getProduct().getProductName())
                        .build();
            }

            // adapter 회사데이터 추가
            MatPageAdapter.Company mCompany = MatPageAdapter.Company.builder()
                    .id(company.getId())
                    .companyCode(company.getCompanyCode())
                    .companyName(company.getCompanyName())
                    .companyAddress(company.getCompanyAddress())
                    .contactPhone(company.getContactPhone())
                    .ceoEmail(company.getCeoEmail())
                    .build();
            // adapter mat데이터 추가
            MatPageAdapter.Mat matPageAdapter = MatPageAdapter.Mat.builder()
                    .id(mat.getId())
                    .product(mProduct)
                    .company(mCompany)
                    .serialNumber(mat.getSerialNumber())
                    .calcMethod(mat.getCalcMethod())
                    .threshold(mat.getThreshold())
                    .inventoryWeight(mat.getInventoryWeight())
                    .recentlyNoticeDate(mat.getRecentlyNoticeDate())
                    .matLocation(mat.getMatLocation())
                    .productOrderCnt(mat.getProductOrderCnt())
                    .boxWeight(mat.getBoxWeight())
                    .communicationStatus(mat.getCommunicationStatus())
                    .currentInventory(mat.getCurrentInventory())
                    .build();
            resultMats.add(matPageAdapter);
        }
        MatPageAdapter matPageAdapter = MatPageAdapter.builder()
                .pageNumber(pageMats.getNumber() + 1)
                .pageSize(pageMats.getSize())
                .totalPages(pageMats.getTotalPages())
                .data(resultMats)
                .build();
        return matPageAdapter;
    }

    public List<ProductCategoryJsonAdapter> createProductCategoryJsonAdapter(List<ProductCategory> productCategories, Company company){
        List<ProductCategoryJsonAdapter> productCategoryJsonAdapters = new ArrayList<>();

        for(ProductCategory productCategory : productCategories){
            ProductCategoryJsonAdapter.Company pcjaCompany = ProductCategoryJsonAdapter.Company.builder()
                    .id(company.getId())
                    .companyCode(company.getCompanyCode())
                    .companyName(company.getCompanyName())
                    .companyAddress(company.getCompanyAddress())
                    .contactPhone(company.getContactPhone())
                    .ceoEmail(company.getCeoEmail())
                    .build();

            ProductCategoryJsonAdapter productCategoryJsonAdapter = ProductCategoryJsonAdapter.builder()
                    .id(productCategory.getId())
                    .categoryName(productCategory.getCategoryName())
                    .company(pcjaCompany)
                    .build();
            productCategoryJsonAdapters.add(productCategoryJsonAdapter);
        }

        return productCategoryJsonAdapters;
    }
}
