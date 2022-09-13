package com.PIMCS.PIMCS.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@ToString(exclude = {"company","product"})
@Entity
public class Mat  implements Comparable<Mat>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product; //상품 object

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company; //회사 object
    private String serialNumber; //매트고유번호
    private int calcMethod; //계산방식(0이면 무게, 1이면 갯수)
    private int threshold; //무게가 임계값 보다 낮아지면 통지이메일 발송
    private int inventoryWeight; //현재재고무게
    private String matLocation; // 매트위치
    private int productOrderCnt; //상품주문 수량(임계값 밑으로 떨어졌을 주무할 상품수량)
    private int boxWeight; //박스 무게
    private Timestamp recentlyNoticeDate; //최근통지 일시
    @Transient
    private int currentInventory; //현재재고
    private int battery;

    @Column(updatable =false)
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedate;


    public int getCurrentInventory(){
        if(product == null) return 0;

        if(calcMethod == 1){ // 계산방식 갯수일때
            return inventoryWeight  / product.getProductWeight();
        }
        return inventoryWeight;
    }

    public static Mat findBySerialNumber(List<Mat> mats, String serialNumber){
        for(Mat mat : mats){
            if(mat.getSerialNumber().equals(serialNumber)){
                return mat;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Mat o) {
        return this.serialNumber.compareTo(o.getSerialNumber());
    }
}
