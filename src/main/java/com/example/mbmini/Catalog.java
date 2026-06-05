package com.example.mbmini;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Catalog {

    @Id
    @GeneratedValue
    private Long Id;

    @Setter
    @NotNull
    private String productName;

    @Min(0)
    private Integer quantity;

    @Min(0)
    @Digits(fraction = 2,message = "Numeric value out of bounds (.<2 digits> expected)", integer = 12)
    private BigDecimal price;


    @NotNull
    private Boolean isActive;

    @Column(name = "created_on")
    @CreatedDate
    private LocalDateTime created_on;

    @Column(name = "created_by")
    @CreatedBy
    private String created_by;

    @Column(name = "modified_on")
    @LastModifiedDate
    private LocalDateTime modified_on;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modified_by;

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
