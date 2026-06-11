package com.example.mbminicart.Entities;

import ch.qos.logback.core.model.INamedModel;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BasketItem {
    @Id
    @GeneratedValue
    private Long Id;

    @Column
    private Long basketId;


    @Column
    private Long productId;

    @Column
    private Integer quantity;

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

    protected BasketItem(){}

    public BasketItem(Long basketId, Long productId, Integer quantity,Integer flag){
        this.basketId=basketId;
        this.productId=productId;
        this.quantity=quantity;
        this.flag=flag;
    }


}
