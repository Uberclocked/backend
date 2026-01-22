package com.uberclocked.api.promotion.model.entity;

import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.users.model.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Promotion {
    @Id
    private UUID id;

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    @Setter
    private User user;
    @ManyToOne
    @Setter
    private Company company;
}