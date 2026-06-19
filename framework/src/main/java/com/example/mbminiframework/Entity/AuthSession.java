package com.example.mbminiframework.Entity;


import com.example.mbminiframework.Auditing.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AuthSession extends Auditable {



    @Id
    @GeneratedValue
    private Long Id;


    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false,unique = true)
    private String authKey;

    @Column(nullable = false,unique = true)
    private String refreshToken;



    @Column(nullable = false)
    private LocalDateTime authKeyExpiresAt;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;



    protected AuthSession() {}

    public AuthSession(Long UserId,String AuthKey,String refreshToken){
        this.userId =UserId;
        this.authKey=AuthKey;
        this.refreshToken=refreshToken;
        this.authKeyExpiresAt=LocalDateTime.now().plusMinutes(30);
        this.refreshTokenExpiresAt=LocalDateTime.now().plusDays(5);
    }



}
