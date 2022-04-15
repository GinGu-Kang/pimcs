package com.PIMCS.PIMCS.form;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProductFormList {
    List<ProductForm> productForms;
    String updateTargetColumn;
}
