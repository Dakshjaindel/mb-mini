package com.example.mbminicustomer.Entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private String authKey;

    @Column(nullable = false,unique = true)
    private String RefreshToken;

    @Column
    private String createdBy;

    @Column
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime authKeyExpiresAt;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;



    protected AuthSession() {}

    public AuthSession(Long UserId,String AuthKey,String refreshToken){
        this.UserId=UserId;
        this.authKey=AuthKey;
        this.RefreshToken=refreshToken;
        this.authKeyExpiresAt=LocalDateTime.now().plusMinutes(30);
        this.refreshTokenExpiresAt=LocalDateTime.now().plusDays(5);
    }


}
