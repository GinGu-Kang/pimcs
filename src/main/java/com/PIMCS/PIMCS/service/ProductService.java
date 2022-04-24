package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.Utils.FileUtils;
import com.PIMCS.PIMCS.Utils.ProductServiceUtils;
import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, DynamoDBMapper dynamoDBMapper) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    /**
     * 상품생성 서비스
     */
    public List<ProductCategory> createProductFormService(Company company){
        return productCategoryRepository.findByCompany(company);
    }

    /**
     * 제품생성 만약 카테고리가 존재하지않으면 에러발생
     * @param productForm 제품생성 폼데이터
     * @return 제품객체
     */
    public Product createProduct(ProductForm productForm, Company company){

        Optional<ProductCategory> optProductCategory = productCategoryRepository.findById(productForm.getProductCategoryId());

        //파일 저장
        String productImagePath = null;
        if(productForm.getProductImage() != null) {
            try {
                productImagePath = FileUtils.uploadFile(productForm.getProductImage());
            } catch (Exception e) {
                throw new IllegalStateException("Failed to upload file.");
            }
        }
        //제품정보 저장, 만약카테고리 존재하지 않으면 IllegalStateException 발생
        if(optProductCategory.isPresent()){
            Product product = productForm.getProduct();
            product.setProductCategory(optProductCategory.get());
            product.setCompany(company);
            if(productImagePath != null)
                product.setProductImage("/product/image/"+productImagePath);
            else
                product.setProductImage("null");
            productRepository.save(product);
            return product;
        }else{
            throw new IllegalStateException("Category does not exist.");
        }
    }

    /**
     * 상품읽기 서비스
     */
    public List<Product> readProductService(Company company){
        List<Product> products = productRepository.findByCompany(company);
        return products;
    }


    /**
     * 상품수정 서비스
     */
    public HashMap<String,Object> updateProduct(Company company, UpdateProductFormList updateProductFormList){
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDBMapper);
        List<Product> findProducts = productRepository.findByCompany(company);
        ProductCategory updateProductCategory = null;
        if(updateProductFormList.getUpdateTargetColumn().equals("productCategory")){
            updateProductCategory = productCategoryRepository.getById(
                    updateProductFormList.getProductForms().get(0).getProductCategoryId()
            );

        }

        ProductServiceUtils productServiceUtils = new ProductServiceUtils();

        List<Product> saveProducts = new ArrayList<>();
        HashMap<String,Object> result = new HashMap<>();

        result.put("isSuccess", true);
        result.put("message","수정 완료했습니다.");

        for(ProductForm productForm : updateProductFormList.getProductForms()){
            Product product = productForm.getProduct();
            Product findProduct = productServiceUtils.findProduct(product, findProducts);

            if(findProduct == null){ //회사에 등록되지 않은 제품일때
                throw new IllegalStateException("Product does not exist.");
            }

            //제품코드 변경시 제품코드 중복이면
            if(updateProductFormList.getUpdateTargetColumn().equals("productCode") &&
               productServiceUtils.isDuplicateProductCode(product,findProducts)){
                result.put("isSuccess", false);
                result.put("message","중복된 제품코드입니다.");
                return result;
            }

            //제품명 변경시 제품명 중복이면
            if(updateProductFormList.getUpdateTargetColumn().equals("productName") &&
                productServiceUtils.isDuplicateProductName(product, findProducts)){
                result.put("isSuccess", false);
                result.put("message","중복된 제품명입니다.");
                return result;
            }
            //제품파일 변경시
            if(updateProductFormList.getUpdateTargetColumn().equals("productImage") && productForm.getProductImage() != null){
                //파일 저장
                String productImagePath = null;
                try {
                    productImagePath = FileUtils.uploadFile(productForm.getProductImage());
                    product.setProductImage("/product/image/"+productImagePath);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to upload file.");
                }
            }

            //카테고리 변경
            if(updateProductCategory != null){
                product.setProductCategory(updateProductCategory);
            }else{
                product.setProductCategory(findProduct.getProductCategory());
            }

            System.out.println("===");
            System.out.println(product.getId());
            System.out.println(product.getProductCode());
            product.setCompany(company);
            saveProducts.add(product);

        }
        //RDBMS에 제품 업데이트
        productRepository.saveAll(saveProducts);
        //dynamodb 제품 업데이트
        dynamoDBUtils.updateProduct(company, saveProducts);

        return result;
    }

    private HashMap<String, Object> updateProductCode( HashMap<String, Object> result, Product product, List<Product> findProducts){
        ProductServiceUtils productServiceUtils = new ProductServiceUtils();

        boolean isDulicate = productServiceUtils.isDuplicateProductCode(product,findProducts);
        if(isDulicate){
            result.put("isSuccess",false);
            result.put("message","중복된 제품코드입니다.");
        }
        return result;
    }


    /**
     * 상품삭제 서비스
     */
    public HashMap<String, Object> deleteProduct(Company company, ProductFormList productFormList){
        List<Product> findProducts = productRepository.findByCompany(company);
        List<Product> deleteProducts = new ArrayList<>();
        ProductServiceUtils productServiceUtils = new ProductServiceUtils();

        for(ProductForm productForm : productFormList.getProductForms()){
            Product product = productForm.getProduct();

            if(productServiceUtils.findProduct(product, findProducts) != null){
                deleteProducts.add(product);
            }else{
                throw new IllegalStateException("Product does not exist.");
            }
        }
        productRepository.deleteAllInBatch(deleteProducts);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("isSuccess",true);
        hashMap.put("message",deleteProducts.size()+"개 제품 삭제 완료하였습니다.");
        return hashMap;

    }

    /**
     * 제품 csv 다운로드
     */
    public void downloadProductCsvService(Company company, ProductCsvForm productCsvForm, Writer writer) throws IOException {

        String[] columns = {"제품코드","제품명","제품카테고리","제품이미지","제품무게"};
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        csvPrinter.printRecord(columns);

        List<Product> products = null;
        if(productCsvForm.getProductIdList() == null){
            products = productRepository.findByCompany(company);
        }else{
            products = productRepository.findByCompanyAndIdIn(company, productCsvForm.getProductIdList());
        }

        for(Product product : products){
            String[] record = {
                    product.getProductCode(),
                    product.getProductName(),
                    product.getProductCategory().getCategoryName(),
                    product.getProductImage(),
                    product.getProductWeight()+"g"
            };
            csvPrinter.printRecord(record);
        }
    }

    /**
     * 제품명 체크
     */
    public HashMap<String,Object> checkProductNameService(Company company,String productName){
        HashMap<String, Object> hashMap = new HashMap<>();

        Optional<Product> productOpt = productRepository.findByProductNameAndCompany(productName, company);
        if(productOpt.isPresent()){
            hashMap.put("result",false);
            hashMap.put("message", "사용할 수 없는 제품명입니다.");
        }else{
            hashMap.put("result", true);
            hashMap.put("message", "사용할 수 있는 제품명입니다.");
        }

        return hashMap;
    }

    /**
     * 제품코드 체크
     */
    public HashMap<String, Object> checkProductCodeSerivice(Company company, String productCode){
        HashMap<String, Object> hashMap = new HashMap<>();

        Optional<Product> productOpt = productRepository.findByProductCodeAndCompany(productCode, company);

        if(productOpt.isPresent()){
            hashMap.put("result",false);
            hashMap.put("message", "사용할 수 없는 제품코드입니다.");
        }else{
            hashMap.put("result", true);
            hashMap.put("message", "사용할 수 있는 제품코드입니다.");
        }

        return hashMap;
    }

}
