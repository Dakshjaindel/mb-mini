package com.example.mbminicustomer;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


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

    public AuthSession() {}

    protected AuthSession(Long UserId,String AuthKey){
        this.UserId=UserId;
        this.AuthKey=AuthKey;
    }


}
