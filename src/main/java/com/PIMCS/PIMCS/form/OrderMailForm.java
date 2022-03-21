package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.domain.OrderMailFrame;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class OrderMailForm {
    private OrderMailFrame orderMailFrame;
    private MatOrder matOrder;
    private List<MatCategoryOrder> matCategoryOrderList;
}
