package com.example.mbminicart.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CustomerNetCredit {
    @Id
    @GeneratedValue
    private Long Id;

    @Column
    private Long customerId;

    @Column
    private BigDecimal walletCredit;

    @Column
    private Integer flag;

    @Column(name = "Created by",updatable = true)
    @CreatedBy
    private String createdBy;

    @Column(name="Created On",updatable = false)
    @CreatedDate
    private LocalDateTime createdOn;

    @Column(name = "Modified By",updatable = true)
    @LastModifiedBy
    private String modifiedBy;

    @Column(name="Modified On",updatable = true)
    @LastModifiedDate
    private LocalDateTime modifiedOn;

    protected CustomerNetCredit(){
    }

    public  CustomerNetCredit(Long customerId, BigDecimal walletCredit, Integer flag ){
        this.customerId=customerId;
        this.walletCredit=walletCredit;
        this.flag=flag;
    }
}
