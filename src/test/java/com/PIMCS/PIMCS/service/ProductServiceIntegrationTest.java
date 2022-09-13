package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.TestUtils;
import com.PIMCS.PIMCS.Utils.DynamoQuery;
import com.PIMCS.PIMCS.domain.*;
import com.PIMCS.PIMCS.form.ProductForm;
import com.PIMCS.PIMCS.form.ProductFormList;
import com.PIMCS.PIMCS.form.UpdateProductFormList;
import com.PIMCS.PIMCS.noSqlDomain.*;
import com.PIMCS.PIMCS.repository.CompanyRepository;
import com.PIMCS.PIMCS.repository.ProductCategoryRepository;
import com.PIMCS.PIMCS.repository.ProductRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.utils.GenerateEntity;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private  GenerateEntity generateEntity;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    DynamoQuery dynamoQuery;

    @Autowired
    private ProductRepository productRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    private  Company company;

    private User user;

    private  List<ProductForm> productForms;

    private ProductCategory productCategory;

    private String imageTestPath ="/Users/gamdodo/ipad_.png";

    private int notExsitcCategoryId = 10000000;

    private int maxSize = 200;

    @BeforeAll
    public void start(){

        user = generateEntity.createUser(null,true);
        company = user.getCompany();
        productCategory = generateEntity.createProductCategory(company, true);
    }

    @BeforeEach
    public void setup(){

        productForms = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<maxSize; i++){
            ProductForm productForm = new ProductForm();
            Product product = new Product();
            product.setProductCode(UUID.randomUUID().toString().substring(0,30));
            product.setProductWeight(random.nextInt(30));
            product.setProductName(UUID.randomUUID().toString().substring(0,30));

            productForm.setProduct(product);
            productForm.setProductCategoryId(productCategory.getId());
            productForms.add(productForm);
        }
    }

    @Test
    public void  notExistCategory(){

        for(ProductForm productForm : productForms){
            productForm.setProductCategoryId(notExsitcCategoryId);

            IllegalStateException e = Assertions.assertThrows(IllegalStateException.class, () -> productService.createProductService(productForm, company, user));
            Assertions.assertEquals("Category does not exist.", e.getMessage());
        }
    }


    @Test
    public void create(){

        for(ProductForm productForm : productForms){
            productService.createProductService(productForm, company, user);
        }

        for(ProductForm productForm : productForms){
            Optional<Product> productOpt = productRepository.findById(productForm.getProduct().getId());
            Assertions.assertEquals(true, productOpt.isPresent());

            DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class, company.getId(), productForm.getProduct().getId());
            Assertions.assertNotNull(dynamoProduct);
        }

    }

    @Test
    public void updateProductCode(){
        create();

        List<ProductForm> array = new ArrayList<>();
        for(ProductForm productForm : productForms){
            Product product = productForm.getProduct();
            product.setProductCode(UUID.randomUUID().toString().substring(0,30));
            productForm.setProduct(product);
            array.add(productForm);
        }
        UpdateProductFormList productFormList = new UpdateProductFormList();
        productFormList.setProductForms(array);
        productFormList.setUpdateTargetColumn("productCode");


        productService.updateProductsService(company, productFormList, user);

        for(ProductForm productForm : array){

            Product find = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNotNull(find);
            Assertions.assertEquals(productForm.getProduct().getProductCode(), find.getProductCode());
        }

    }

    @Test
    public void updateProductName(){
        create();

        List<ProductForm> array = new ArrayList<>();
        for(ProductForm productForm : productForms){
            Product product = productForm.getProduct();
            product.setProductName(UUID.randomUUID().toString().substring(0,30));
            productForm.setProduct(product);
            array.add(productForm);
        }
        UpdateProductFormList productFormList = new UpdateProductFormList();
        productFormList.setProductForms(array);
        productFormList.setUpdateTargetColumn("productName");


        productService.updateProductsService(company, productFormList, user);

        for(ProductForm productForm : array){

            Product find = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNotNull(find);
            Assertions.assertEquals(productForm.getProduct().getProductName(), find.getProductName());
        }
    }


    @Test
    public void updateProductCategory(){
        create();
        List<ProductForm> array = new ArrayList<>();
        ProductCategory productCategory =  generateEntity.createProductCategory(company, true);
        for(ProductForm productForm : productForms){
            productForm.setProductCategoryId(productCategory.getId());
            array.add(productForm);
        }
        UpdateProductFormList productFormList = new UpdateProductFormList();
        productFormList.setProductForms(array);
        productFormList.setUpdateTargetColumn("productCategory");

        productService.updateProductsService(company, productFormList, user);

        for(ProductForm productForm : array){
            Product find = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNotNull(find);
            Assertions.assertEquals(productForm.getProductCategoryId(), find.getProductCategory().getId());
        }

    }

    @Test
    public void updateProductImage() throws IOException {
        create();
        List<ProductForm> array = new ArrayList<>();
        File firstFile = new File(imageTestPath);
        MultipartFile multipartFile = new MockMultipartFile("test.png", new FileInputStream(firstFile));
        for(ProductForm productForm : productForms){
            productForm.setProductImage(multipartFile);
            array.add(productForm);
        }
        UpdateProductFormList productFormList = new UpdateProductFormList();
        productFormList.setProductForms(array);
        productFormList.setUpdateTargetColumn("productImage");

        productService.updateProductsService(company, productFormList, user);


        for(ProductForm productForm : array){
            Product product = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNotNull(product);
            Path f = Paths.get(product.getProductImage());
            File secFile = new File(uploadPath, f.getFileName().toString());
            Assertions.assertEquals(true, isEqual(firstFile.toPath(), secFile.toPath()));
            secFile.delete();
        }

    }

    @Test
    public void updateProductWeight(){
        create();

        List<ProductForm> array = new ArrayList<>();
        Random random = new Random();
        for(ProductForm productForm : productForms){
            Product product = productForm.getProduct();
            product.setProductWeight(random.nextInt(40));
            productForm.setProduct(product);
            array.add(productForm);
        }
        UpdateProductFormList productFormList = new UpdateProductFormList();
        productFormList.setProductForms(array);
        productFormList.setUpdateTargetColumn("productWeight");


        productService.updateProductsService(company, productFormList,user);

        for(ProductForm productForm : array){

            Product find = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNotNull(find);
            Assertions.assertEquals(productForm.getProduct().getProductWeight(), find.getProductWeight());
        }
    }

    @Test
    public void delete(){
        create();
        List<ProductForm> array = new ArrayList<>();

        for(ProductForm productForm : productForms){
            array.add(productForm);
        }
        ProductFormList productFormList = new ProductFormList();
        productFormList.setProductForms(array);

        productService.deleteProduct(company, productFormList, user);

        for(ProductForm productForm : productForms){
            Product product = productRepository.findById(productForm.getProduct().getId()).orElse(null);
            Assertions.assertNull(product);

            DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class, company.getId(), productForm.getProduct().getId());
            Assertions.assertNull(dynamoProduct);
        }

    }

    private  boolean isEqual(Path firstFile, Path secondFile)
    {
        try {
            if (Files.size(firstFile) != Files.size(secondFile)) {
                return false;
            }

            byte[] first = Files.readAllBytes(firstFile);
            byte[] second = Files.readAllBytes(secondFile);
            return Arrays.equals(first, second);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    @AfterAll
    public void completeAllTest(){
        System.out.println("completeAllTest");

        userRepository.delete(user);
        companyRepository.delete(company);
        productCategoryRepository.delete(productCategory);


        for(ProductForm productForm : productForms){
            DynamoProduct dynamoProduct = dynamoDBMapper.load(DynamoProduct.class, company.getId(), productForm.getProduct().getId());
            if(dynamoProduct != null) dynamoDBMapper.delete(dynamoProduct);
        }

        DynamoDBQueryExpression<ProductLog> queryExpression = ProductLog.queryExpression(company, false);
        List<ProductLog> productLogs = dynamoQuery.exeQuery(ProductLog.class, queryExpression);

        if(productLogs.size() > 0){
            dynamoDBMapper.batchDelete(productLogs);
        }
    }

}
