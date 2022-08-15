package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.Interface.FileStorage;
import com.PIMCS.PIMCS.Utils.DynamoDBUtils;
import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.Utils.ProductServiceUtils;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.noSqlDomain.*;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
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
    private final DynamoQuery dynamoQuery;

    @Qualifier("fileStorage")
    private final FileStorage fileStorage;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, DynamoDBMapper dynamoDBMapper, DynamoQuery dynamoQuery, FileStorage fileStorage) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.dynamoDBMapper = dynamoDBMapper;
        this.dynamoQuery = dynamoQuery;
        this.fileStorage = fileStorage;
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
    public Product createProductService(ProductForm productForm, Company company, User user){

        Optional<ProductCategory> optProductCategory = productCategoryRepository.findById(productForm.getProductCategoryId());

        //파일 저장
        String productImagePath = null;
        if(productForm.getProductImage() != null) {
            try {
                productImagePath = fileStorage.save(productForm.getProductImage());
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
                product.setProductImage("/product/images/"+productImagePath);
            else
                product.setProductImage("null");
            //rdbms 저장
            productRepository.save(product);
            //dynamodb 저장
            DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class,product.getId());
            if(dynamoProduct != null){ // dynamodb에 상품이 등록되어 있으면 삭제
                dynamoDBMapper.delete(dynamoProduct);
            }
            DynamoProduct.save(dynamoDBMapper,product); // dynamoProduct 저장

            //로깅
            List<Product> logProducts = new ArrayList<>();
            logProducts.add(product);
            ProductLog.batchSave(logProducts, user, "생성", dynamoDBMapper);

            return product;
        }else{
            throw new IllegalStateException("Category does not exist.");
        }


    }

    /**
     * 상품읽기 서비스
     */
    public List<Product> findProductListService(Company company){
        List<Product> products = productRepository.findByCompany(company);
        return products;
    }


    /**
     * 상품수정 서비스
     */
    public HashMap<String,Object> updateProductsService(Company company, UpdateProductFormList updateProductFormList, User user){

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
                    productImagePath = fileStorage.save(productForm.getProductImage());
                    product.setProductImage("/product/images/"+productImagePath);
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

           product.setCompany(company);
            saveProducts.add(product);

        }
        //RDBMS에 제품 업데이트
        productRepository.saveAll(saveProducts);

        //dynamodb 업데이트
        DynamoProduct.update(dynamoDBMapper, dynamoQuery, saveProducts, company);

        //로깅
        ProductLog.batchSave(saveProducts, user, "수정", dynamoDBMapper);
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
    public HashMap<String, Object> deleteProduct(Company company, ProductFormList productFormList, User user){
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
        DynamoProduct.delete(dynamoDBMapper, dynamoQuery, deleteProducts, company);

        //로깅
        ProductLog.batchSave(deleteProducts, user, "삭제", dynamoDBMapper);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("isSuccess",true);
        hashMap.put("message",deleteProducts.size()+"개 제품 삭제 완료하였습니다.");
        return hashMap;

    }

    /**
     * 제품 csv 다운로드
     */
    public void downloadProductCsvService(Company company, ProductCsvForm productCsvForm, Writer writer) throws IOException {

        String[] columns = {"제품코드","제품명","제품카테고리","제품무게"};
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
                    product.getProductWeight()+"g"
            };
            csvPrinter.printRecord(record);
        }
    }

    /**
     * 제품명 체크
     */
    public ValidationForm checkProductsByProductNameService(Company company,String productName){

        Optional<Product> productOpt = productRepository.findByProductNameAndCompany(productName, company);
        if(productOpt.isPresent()){
            return new ValidationForm(false, "사용할 수 없는 제품명입니다.");
        }else{
            return new ValidationForm(true, "사용할 수 있는 제품명입니다.");
        }


    }

    /**
     * 제품코드 체크
     */
    public ValidationForm checkProductsByProductCodeService(Company company, String productCode){
        Optional<Product> productOpt = productRepository.findByProductCodeAndCompany(productCode, company);

        if(productOpt.isPresent()){
            return new ValidationForm(false, "사용할 수 없는 제품코드입니다.");
        }else{
            return new ValidationForm(true, "사용할 수 있는 제품코드입니다.");
        }
    }

    /**
     * 상품로그
     */
    public DynamoResultPage productLogService(Company company, Pageable pageable){
        DynamoDBQueryExpression<ProductLog> queryExpression = ProductLog.queryExpression(company, false);
        DynamoDBQueryExpression<ProductLog> countQueryExpression = ProductLog.queryExpression(company, true);
        return dynamoQuery.exePageQuery(ProductLog.class, queryExpression, countQueryExpression, pageable);
    }

    /**
     * 상품로그 검색
     */
    public DynamoResultPage searchProductLogService(Company company, InOutHistorySearchForm searchForm, Pageable pageable){
        DynamoDBQueryExpression<ProductLog> queryExpression = ProductLog.searchQueryExpression(company, searchForm);
        DynamoDBQueryExpression<ProductLog> countQueryExpression = ProductLog.searchQueryExpression(company, searchForm);
        System.out.println(queryExpression);

        return dynamoQuery.exePageQuery(ProductLog.class, queryExpression, countQueryExpression, pageable);
    }

    /**
     * 제품관리 로그 csv 다운로드
     */
    public void downloadProductsLogCsvService(Company company,InOutHistorySearchForm searchForm, Writer writer) throws IOException{

        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
        String[] columns = {"상품명","상품코드","사용자 이름", "사용자 이메일", "action", "일시"};
        DynamoDBQueryExpression<ProductLog> queryExpression = ProductLog.queryExpression(company, true);

        List productLogList;

        if(searchForm.getQuery() != "" || (searchForm.getStartDate() != null && searchForm.getEndDate() != null)){
            productLogList = dynamoQuery.exeQuery(ProductLog.class,  ProductLog.searchQueryExpression(company, searchForm));

        }else { // 전제데이터 다운로드
            productLogList = dynamoQuery.exeQuery(ProductLog.class, ProductLog.queryExpression(company, false));
        }

        csvPrinter.printRecord(columns);
        for(Object o : productLogList){
            ProductLog productLog = (ProductLog) o;
            List<String> record = new ArrayList<>();
            record.add(productLog.getProductName());
            record.add(productLog.getProductCode());
            record.add(productLog.getUserName());
            record.add(productLog.getUserEmail());
            record.add(productLog.getAction());
            record.add(productLog.getCreatedAt().toString());
            csvPrinter.printRecord(record);
        }

    }

}
