package com.uberclocked.api.company.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Company {

    @Id
    private UUID id;

    private String name;
    private String cuit;
    private String email;
    private String phone;

//    @OneToMany(mappedBy = "company")
//    private List<Product> products;
}
