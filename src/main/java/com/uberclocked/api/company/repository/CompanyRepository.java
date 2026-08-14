package com.uberclocked.api.company.repository;

import com.uberclocked.api.company.model.entity.Company;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

  boolean existsByEmailDomain(String emailDomain);

  Optional<Company> findByEmailDomain(String emailDomain);
}
