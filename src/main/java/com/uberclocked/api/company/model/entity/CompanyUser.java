package com.uberclocked.api.company.model.entity;

import com.uberclocked.api.users.model.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompanyUser {

    @Id
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Company company;

    public CompanyUser(User user, Company company) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.company = company;
    }
}

