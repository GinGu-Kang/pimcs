package com.PIMCS.PIMCS.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchForm {

    private String searchType; //조회항목 ex) 시리얼번호, 상품명 등
    private String searchQuery; //조회내용

    public SearchForm(String searchType,String searchQuery){
        this.searchType = searchType;
        this.searchQuery = searchQuery;
    }
    public SearchForm(){}
}
