package com.PIMCS.PIMCS.Utils;

import java.util.UUID;

public class CompanyServiceUtils {
    public String UUIDgeneration(){
        String uuid = UUID.randomUUID().toString();
        uuid=uuid.replace("-","");
        return uuid;
    }
}
