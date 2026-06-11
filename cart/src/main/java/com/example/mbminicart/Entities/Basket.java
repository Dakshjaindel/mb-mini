package com.example.mbminicart.Entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Basket {
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


    protected Basket(){}

    public Basket(Long userId,Date date,Integer flag,Integer quantity){
        this.userId= userId;
        this.date= date;
        this.flag=flag;
        this.quantity=quantity;
    }


}

