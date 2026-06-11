package com.example.mbminicustomer.Entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuthSession {


    @Getter
    @Setter
    @Id
    @GeneratedValue
    private Long Id;


    @Column(nullable = false)
    private Long UserId;

    @Column(nullable = false,unique = true)
    private String AuthKey;

    protected AuthSession() {}

    public AuthSession(Long UserId,String AuthKey){
        this.UserId=UserId;
        this.AuthKey=AuthKey;
    }


}
