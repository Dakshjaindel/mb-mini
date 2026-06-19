package com.example.mbminicart.Entities;

import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Basket extends Auditable {
    @Id
    @GeneratedValue
    private Long Id;


    @Column
    private Long userId;

    @Column
    private Date date;

    @Column
    private Integer flag;

    @Column
    private Integer quantity;


    protected Basket(){}

    public Basket(Long userId,Date date,Integer flag,Integer quantity){
        this.userId= userId;
        this.date= date;
        this.flag=flag;
        this.quantity=quantity;
    }


}

