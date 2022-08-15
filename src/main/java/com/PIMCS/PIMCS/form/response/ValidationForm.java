package com.PIMCS.PIMCS.form.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ValidationForm {
    private boolean valid;
    private String message;

    public ValidationForm(){}

    public ValidationForm(boolean valid, String message){
        this.valid = valid;
        this.message = message;
    }

}
