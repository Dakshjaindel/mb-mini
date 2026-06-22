package com.example.mbminicart.Entities;

import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BasketItem extends Auditable {
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


    protected BasketItem(){}

    public BasketItem(Long basketId, Long productId, Integer quantity,Integer flag){
        this.basketId=basketId;
        this.productId=productId;
        this.quantity=quantity;
        this.flag=flag;
    }


}
