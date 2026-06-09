package com.example.mbminicustomer;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)

public class Customer {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column
    private String name;


    @Pattern(regexp= "^[0-9]{10}$",message = "Phone number must be exactly 10 digits")
    @Column
    private String phoneNo;

    @Column
    private String password;

    @NotBlank
    @Email(message = "invalid Email Format")
    @Column(unique = true,nullable = false)
    private String email;


    @Column
    private Long houseNo;

    @Column
    private String locality;

    @Column
    private String city;

    @Column
    private Long pincode;

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

    protected Customer(){}

    protected Customer(String Name, String PhoneNo, String password,String Email, Long HouseNo,String Locality, String Ctiy,Long Pincode){
        this.name=Name;
        this.phoneNo=PhoneNo;
        this.password=password;
        this.email=Email;
        this.houseNo=HouseNo;
        this.city=Ctiy;
        this.pincode=Pincode;
        this.locality=Locality;
    }


    @PrePersist
    @PreUpdate
    public void encryptPass(){
        if (this.password!=null){
            BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
            this.password=encoder.encode(this.password);
        }
    }









}
