package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.adapter.MatPageAdapter;
import com.PIMCS.PIMCS.adapter.ProductJsonAdapter;
import com.PIMCS.PIMCS.domain.BusinessCategory;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Page;
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

    /**
     * mat 데이터를 json 직렬하기 편한 객체로 만들어준다
     */
    public MatPageAdapter createMatPageAdapter(Page<Mat> pageMats, Company company) {
        List<MatPageAdapter.Mat> resultMats = new ArrayList<>();
        //Entity 객체 사용하지않고 adapter 클래스를 만들어서 json 직렬화
        for (Mat mat : pageMats) {
            // adapter 상품데이터 추가
            MatPageAdapter.Product mProduct = MatPageAdapter.Product.builder()
                    .id(mat.getProduct().getId())
                    .productCode(mat.getProduct().getProductCode())
                    .productImage(mat.getProduct().getProductImage())
                    .productWeight(mat.getProduct().getProductWeight())
                    .productName(mat.getProduct().getProductName())
                    .build();
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
                    .battery(mat.getBattery())
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
}
