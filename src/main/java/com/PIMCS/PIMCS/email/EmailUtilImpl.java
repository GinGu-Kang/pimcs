package com.PIMCS.PIMCS.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Component
@EnableAsync
public class EmailUtilImpl {//implements EmailUtil

    @Autowired
    private final JavaMailSender sender;

    public EmailUtilImpl(JavaMailSender sender) {
        this.sender = sender;
    }

//    @Override
//    @Transactional
    @Async
    public Map<String, Object> sendEmail(String[] toAddress, String subject, String body, boolean ishtml) {
        Map<String, Object> result = new HashMap<String, Object>();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(body,ishtml);
            result.put("resultCode", 200);
        } catch (MessagingException e) {
            e.printStackTrace();
            result.put("resultCode", 500);
        }

        sender.send(message);
        return result;
    }
}
