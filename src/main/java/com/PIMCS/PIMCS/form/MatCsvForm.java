package com.PIMCS.PIMCS.form;

import lombok.Data;

import java.util.List;

@Data
public class MatCsvForm {
    List<Integer> checkedMatId;
    List<String> checkedColumnNames;
}
