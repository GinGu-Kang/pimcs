package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.domain.Product;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductForm {
    private Product product;
    private int productCategoryId;
    private MultipartFile productImage;
}
