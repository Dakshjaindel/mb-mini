package com.example.mbminicart.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
public class Credits {
    @Id
    @GeneratedValue
    private Long Id;

    @Column
    private Integer flag;

    @Column
    private Long customerId;

    @Column
    @Min(0)
    private BigDecimal creditAmount;

    @Column
    private String type;

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

    protected Credits(){}

    public Credits(Long customerId,BigDecimal creditAmount, String type, Integer flag){
        this.customerId=customerId;
        this.creditAmount=creditAmount;
        this.type=type;
        this.flag=flag;

    }


}
