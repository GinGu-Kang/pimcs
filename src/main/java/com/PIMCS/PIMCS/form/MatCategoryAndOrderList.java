package com.PIMCS.PIMCS.form;


import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatCategoryAndOrderList {
    private List<MatCategoryOrder> matCategoryOrderList;
    private List<Integer> matCategoryIdList;

}
