package com.PIMCS.PIMCS.form;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;


public class ResultGraph {
    private String title;
    private List<String> labels = new ArrayList<>();
    private List<Integer> data = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
