package com.uberclocked.api.company.repository;

import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.company.model.entity.CompanyUser;
import com.uberclocked.api.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {

  boolean existsByUserAndCompany(User user, Company company);

  List<CompanyUser> findByCompany(Company company);

  List<CompanyUser> findByUser(User user);

  Optional<CompanyUser> findByUserAndCompany(User user, Company company);
}
