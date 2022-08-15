package com.PIMCS.PIMCS.form.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseForm {

    private boolean success;
    private String message;
    private List data;
}
