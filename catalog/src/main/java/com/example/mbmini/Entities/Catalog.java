package com.example.mbmini.Entities;


import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Catalog extends Auditable {

    @Id
    @GeneratedValue
    private Long Id;


    @Column
    private String productName;

    @Column
    private Integer quantity;

    @Column
    private BigDecimal price;

    @Column
    private Boolean isActive;



    protected Catalog(){}
    public  Catalog(String productName,Integer quantity,BigDecimal price, Boolean isActive){
        this.productName=productName;
        this.quantity=quantity;
        this.price=price;
        this.isActive=isActive;
    }

    public Long getId() {
        return Id;
    }
    public String getProductName(){return productName;}

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Boolean getIsActive(){
        return isActive;
    }

    @Override
    public String toString() {
        return "Catalog{id=" + Id + ", productName='" + productName + "', quantity=" + quantity + ", price=" + price + ", isActive=" + isActive + "}";
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public void setQuantity(Integer quantity){
        this.quantity=quantity;
    }


}
