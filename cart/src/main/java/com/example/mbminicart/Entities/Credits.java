package com.example.mbminicart.Entities;


import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Credits extends Auditable {
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


    protected Credits(){}

    public Credits(Long customerId,BigDecimal creditAmount, String type, Integer flag){
        this.customerId=customerId;
        this.creditAmount=creditAmount;
        this.type=type;
        this.flag=flag;

    }


}
