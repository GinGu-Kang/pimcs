package com.PIMCS.PIMCS.form;

import com.PIMCS.PIMCS.noSqlDomain.OrderMailRecipients;
import lombok.Data;

import java.util.List;

@Data
public class MailRecipientsForm {
    private List<OrderMailRecipients> mailRecipients;
}
