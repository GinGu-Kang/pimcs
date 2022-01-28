package com.PIMCS.PIMCS.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jdk.jfr.DataAmount;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Entity
public class Mat {
    @Id
    @Column(name = "serial_number")
    private String serialNumber; //매트고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_code")
    private Product product; //상품코드

    @Column(name = "company_code")
    private String companyCode; //회사코드

    @Column(name = "calc_method")
    private int calcMethod; //계산방식(0이면 무게, 1이면 갯수)

    @Column(name = "threshold")
    private int threshold; //무게가 임계값 보다 낮아지면 통지이메일 발송

    @Column(name = "inventory_weight")
    private int inventoryWeight; //현재재고무게

    @Column(name = "recently_notice_date")
    private Timestamp recentlyNoticeDate; //최근통지 일시

    @Column(name = "is_send_email")
    private int isSendEmail; //이메일 발송했는 여부(1이면 발송, 0이면 미발송)

    @Column(name = "mat_location")
    private String matLocation; // 매트위치

    @Column(name = "product_order_cnt")
    private int productOrderCnt; //상품주문 수량(임계값 밑으로 떨어졌을 주무할 상품수량)

    @Column(name = "box_weight")
    private int boxWeight; //박스 무게

    @Column(name = "battery")
    private int battery;

    @CreationTimestamp
    @Column(name = "createat")
    private Timestamp createat;

}
