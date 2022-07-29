package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.noSqlDomain.InOutHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DynamoResultPage {
    private int page;
    private int totalPage;
    private int size;
    private List data;
}
