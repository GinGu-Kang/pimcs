package com.PIMCS.PIMCS.form;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductForm {
    private String productCode;
    private String productName;
    private int productCategoryId;
    private int productWeight;
    private MultipartFile productImage;
}
