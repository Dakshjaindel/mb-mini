package com.example.mbminicustomer.Entities;


import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)

public class Customer extends Auditable {

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

    @Column
    private Double Latitude;

    @Column
    private Double Longitude;


    protected Customer(){}

    public Customer(String Name, String PhoneNo, String password, String Email, Long HouseNo, String Locality, String City, Long Pincode,Double latitude,Double longitude){
        this.name=Name;
        this.phoneNo=PhoneNo;
        this.password=password;
        this.email=Email;
        this.houseNo=HouseNo;
        this.city=City;
        this.pincode=Pincode;
        this.locality=Locality;
        this.Latitude=latitude;
        this.Longitude=longitude;
    }


    @PrePersist
    @PreUpdate
    public void encryptPass(){
        if (this.password != null && !this.password.startsWith("$2a$") && !this.password.startsWith("$2b$") && !this.password.startsWith("$2y$")){
            BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
            this.password=encoder.encode(this.password);
        }
    }

}
