package com.PIMCS.PIMCS.Utils;

import com.PIMCS.PIMCS.domain.Mat;
import com.PIMCS.PIMCS.domain.Product;

import java.util.List;

public class MatServiceUtils {

    public Product findProduct(List<Product> products, int id){

        for(Product product : products){
            if(product.getId() == id) return product;
        }
        return null;
    }

    public Mat findMat(List<Mat> mats, int id){
        for(Mat mat : mats){
            if(mat.getId() == id)return mat;
        }
        return null;
    }
}
