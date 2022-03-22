package com.PIMCS.PIMCS.form;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;


public class ResultGraph {


    private String productName;
    private List<String> labels = new ArrayList<>();
    private List<Integer> data = new ArrayList<>();

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setLabels(String label){
        labels.add(label);
    }
    public void setData(int data){
        this.data.add(data);
    }

    public List<String> getLabels(){
        return labels;
    }

    public List<Integer> getData(){
        return data;
    }

}
