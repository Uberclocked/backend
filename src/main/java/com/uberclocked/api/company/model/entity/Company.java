package com.uberclocked.api.company.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company {

  @Id private UUID id;

  private String name;
  @Column(unique = true)
  private String cuit;
  private String email;
  private String phone;

  //    @OneToMany(mappedBy = "company")
  //    private List<Product> products;
}
