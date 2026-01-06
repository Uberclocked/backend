package com.uberclocked.api.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    private String auth0Id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    private LocalDateTime lastLogin;

    private String country;

    private String cellPhone;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    public User(){}

    public User(String subject, String userName, String email){
        this.auth0Id = subject;
        this.userName = userName;
        this.email = email;
    }
}
