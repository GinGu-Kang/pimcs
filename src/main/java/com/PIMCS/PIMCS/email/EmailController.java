package com.PIMCS.PIMCS.email;




import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 이메일 컨트롤러
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class EmailController {

    private final EmailUtilImpl emailUtilImpl;

    /**
     * 이메일 발송
     * @param params
     * @return
     */


    

    @RequestMapping(value = "/email/sendEmail",method = RequestMethod.POST)
    public Map<String, Object> sendEmail(@RequestBody Map<String, Object> params){
        log.info("email params={}", params);

        return emailUtilImpl.sendEmail( (String) params.get("userId")
                , (String) params.get("subject")
                , (String) params.get("body")
        );
    }
}