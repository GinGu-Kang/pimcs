package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.domain.Mat;
import lombok.Data;

import java.util.List;

@Data
public class MatForm {
    private Mat mat;
    private int productId;
    private List<String> mailRecipients;
}
