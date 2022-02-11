package com.PIMCS.PIMCS.form;


import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatCategoryAndOrderForm {
    private List<MatCategoryOrder> matCategoryOrderList;
    private List<Integer> matCategoryIdList;

}
