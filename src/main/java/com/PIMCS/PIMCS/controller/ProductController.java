package com.PIMCS.PIMCS.controller;

import com.PIMCS.PIMCS.domain.Product;
import com.PIMCS.PIMCS.domain.ProductCategory;
import com.PIMCS.PIMCS.form.*;
import com.PIMCS.PIMCS.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    /**
     * 상품등록
     */
    @GetMapping("/create")
    public String createForm(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<ProductCategory> productCategories = productService.createProductFormService(secUserCustomForm.getCompany());
        model.addAttribute("productCategories", productCategories);
        return "product/createProduct/createProduct";
    }

    @PostMapping("/create")
    public String create(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductForm productForm, Model model){

        Product product = productService.createProduct(productForm, secUserCustomForm.getCompany());
        model.addAttribute("product",product);
        return "product/createProduct/fragment/registeredCardView";
    }

    /**
     * 제품 목록
     */
    @GetMapping("/read")
    public String read(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, Model model){
        List<Product> products = productService.readProductService(secUserCustomForm.getCompany());
        model.addAttribute("products",products);
        return "product/readProduct";
    }

    /**
     * 제품 이미지 로드
     * @param fileName
     * @return 이미지를 byte[]로 return
     * @throws IOException
     */
    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<byte[]> loadProductImage(@PathVariable String fileName)  {

        try {
            InputStream imageStream = new FileInputStream("/Users/gamdodo/Documents/java_workspace/media/" + fileName);
            byte[] imageByteArray = new byte[0];
            imageByteArray = imageStream.readAllBytes();
            imageStream.close();
            return  new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * 상품수정
     */
    @PostMapping("/update")
    @ResponseBody
    public HashMap<String, Object> update(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, UpdateProductFormList updateProductFormList){
        return productService.updateProduct(secUserCustomForm.getCompany(), updateProductFormList);
    }

    /**
     * 상품삭제
     */
    @PostMapping("/delete")
    @ResponseBody
    public HashMap<String, Object> delete(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductFormList productFormList){
        return productService.deleteProduct(secUserCustomForm.getCompany(), productFormList);
    }


    /**
     * 체크된 제품 csv download
     */
    @PostMapping(value = "/download/csv",  produces = "text/csv")
    public void downloadProductCsv(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, ProductCsvForm productCsvForm , HttpServletResponse response) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/csv; charset=UTF-8");
        String exportFileName = "product-" + LocalDate.now().toString() + ".csv";
        response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
        productService.downloadProductCsvService(secUserCustomForm.getCompany(), productCsvForm, response.getWriter());
    }

    /**
     * 제품명 체크
     */
    @GetMapping("/check/name")
    @ResponseBody
    public HashMap<String, Object> checkProductName(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, @RequestParam("productName") String productName){
        return productService.checkProductNameService(secUserCustomForm.getCompany(), productName);
    }

    /**
     * 제품명 코드
     */
    @GetMapping("/check/code")
    @ResponseBody
    public HashMap<String, Object> checkProductCode(@AuthenticationPrincipal SecUserCustomForm secUserCustomForm, @RequestParam("productCode") String productCode){
        return productService.checkProductCodeSerivice(secUserCustomForm.getCompany(), productCode);
    }
}
