package com.uberclocked.api.company.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company {

  @Id private UUID id;

  private String name;

  @Column(unique = true)
  private String cuit;

  @Column(unique = true, nullable = false)
  private String emailDomain;

  private String phone;

  //    @OneToMany(mappedBy = "company")
  //    private List<Product> products;
}
