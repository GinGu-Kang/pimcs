package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.Interface.FileStorage;
import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.domain.User;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.form.response.ValidationForm;
import com.PIMCS.PIMCS.repository.UserRepository;
import com.PIMCS.PIMCS.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
//@RequestMapping(value = "/product")
public class ProductController {


    private final ProductService productService;
    @Qualifier("fileStorage")
    private final FileStorage fileStorage;

    private final UserRepository userRepository;
    @Autowired
    public ProductController(ProductService productService, FileStorage fileStorage, UserRepository userRepository) {
        this.productService = productService;
        this.fileStorage = fileStorage;
        this.userRepository = userRepository;
    }


    /**
     * 상품등록
     */
    @GetMapping("/products/create")
    public String createProductForm(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<ProductCategory> productCategories = productService.createProductFormService(secUserCustomForm.getCompany());
        model.addAttribute("productCategories", productCategories);
        return "product/createProduct/createProduct";
    }

    @PostMapping("/products")
    public String createProduct(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductForm productForm, Model model){

        System.out.println("======");
        System.out.println(productForm);
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        Product product = productService.createProductService(productForm, secUserCustomForm.getCompany(), user);
        model.addAttribute("product",product);
        return "product/createProduct/fragment/registeredCardView";
    }

    /**
     * 제품 목록
     */
    @GetMapping("/products")
    public String findProductList(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<Product> products = productService.findProductListService(secUserCustomForm.getCompany());
        model.addAttribute("products",products);
        return "product/readProduct";
    }

    /**
     * 제품 이미지 로드
     * @param fileName
     * @return 이미지를 byte[]로 return
     * @throws IOException
     */
    @GetMapping("/product/images/{fileName:.+}")
    public ResponseEntity<byte[]> loadProductImage(@PathVariable String fileName)  {

        try {
            byte[] imageByteArray = fileStorage.read(fileName);
            return  new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * 상품수정
     */
    @PutMapping("/products")
    @ResponseBody
    public HashMap<String, Object> updateProducts(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, UpdateProductFormList updateProductFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return productService.updateProductsService(secUserCustomForm.getCompany(), updateProductFormList, user);
    }

    /**
     * 상품삭제
     */
    @DeleteMapping("/products")
    @ResponseBody
    public HashMap<String, Object> delete(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductFormList productFormList){
        User user = userRepository.getOne(secUserCustomForm.getUsername());
        return productService.deleteProduct(secUserCustomForm.getCompany(), productFormList, user);
    }


    /**
     * 체크된 제품 csv download
     */
    @PostMapping(value = "/products/csv",  produces = "text/csv")
    public void downloadProductCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductCsvForm productCsvForm , HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "product-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        productService.downloadProductCsvService(secUserCustomForm.getCompany(), productCsvForm, response.getWriter());
    }


    @GetMapping("/products/validation")
    @ResponseBody
    public ValidationForm productValidation(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @RequestParam("type") String type,
            @RequestParam("value") String value){

        type = type.toLowerCase();
        if(type.equals("productname")){

            return productService.checkProductsByProductNameService(secUserCustomForm.getCompany(), value);

        }else if(type.equals("productcode")){

            return productService.checkProductsByProductCodeService(secUserCustomForm.getCompany(), value);

        }
        throw new IllegalStateException("Does not exist type prams");
    }


    @GetMapping("/products/log")
    public String productLog(
            @AuthenticationPrincipal SecUserCustomForm secUserCustomForm,
            @PageableDefault(size=10) Pageable pageable,
            InOutHistorySearchForm searchForm,
            Model model){

        DynamoResultPage dynamoResultPage;

        if(searchForm.isExist()){
            System.out.println("======");
            System.out.println("hiudhiuhoijio");
            dynamoResultPage = productService.searchProductLogService(secUserCustomForm.getCompany(), searchForm, pageable);
            System.out.println(dynamoResultPage);
        }else{
             dynamoResultPage = productService.productLogService(secUserCustomForm.getCompany(), pageable);
        }


        model.addAttribute("dynamoResultPage", dynamoResultPage);
        model.addAttribute("searchForm",searchForm);
        return "product/productLog.html";
    }

    /**
     * 매트관리 로그 csv
     */
    @GetMapping(value = "/products/log/csv",  produces = "text/csv")
    public void downloadProductsLogCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm ,InOutHistorySearchForm searchForm, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "product-log-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);

        productService.downloadProductsLogCsvService(secUserCustomForm.getCompany(),searchForm ,response.getWriter());
    }


}
