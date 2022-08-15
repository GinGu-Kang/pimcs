package com.PIMCS.PIMCS.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class InOutHistorySearchForm {
    private String query;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public boolean isExist(){
        if(query != null || startDate != null || endDate != null){
            return true;
        }
        return false;
    }

    public boolean isExistAll(){
        if(query != null && startDate != null && endDate != null){
            return true;
        }
        return false;
    }
}
