package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.domain.Product;

import java.util.List;

public class ProductServiceUtils {


    public boolean isDuplicateProductCode(Product replaceProduct, List<Product> products){

        for(Product product : products){
            if(product.getId() != replaceProduct.getId() && replaceProduct.getProductCode().equals(product.getProductCode())) return true;
        }
        return false;
    }

    public boolean isDuplicateProductName(Product replaceProduct, List<Product> products){

        for(Product product : products){
            if(product.getId() != replaceProduct.getId() &&replaceProduct.getProductName().equals(product.getProductName())) return true;
        }
        return false;
    }
    public Product findProduct(Product findProduct, List<Product> products){
        for(Product product : products){
            if(product.getId() == findProduct.getId()) return product;
        }
        return null;
    }
}
