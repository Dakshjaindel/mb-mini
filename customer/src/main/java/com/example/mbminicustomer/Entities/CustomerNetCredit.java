package com.example.mbminicustomer.Entities;

import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CustomerNetCredit extends Auditable {
    @Id
    @GeneratedValue
    private Long Id;

    @Column
    private Long customerId;

    @Column
    private BigDecimal walletCredit;

    @Column
    private Integer flag;


    protected CustomerNetCredit(){
    }

    public  CustomerNetCredit(Long customerId, BigDecimal walletCredit, Integer flag ){
        this.customerId=customerId;
        this.walletCredit=walletCredit;
        this.flag=flag;
    }
}
