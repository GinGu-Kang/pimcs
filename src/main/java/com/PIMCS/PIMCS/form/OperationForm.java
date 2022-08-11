package com.PIMCS.PIMCS.form;

import com.mysql.cj.protocol.x.XProtocolRowInputStream;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationForm {
    private  String lte;
    private  String gte;
    private  String lt;
    private  String gt;



}
