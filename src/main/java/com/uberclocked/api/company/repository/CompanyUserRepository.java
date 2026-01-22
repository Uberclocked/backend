package com.uberclocked.api.company.repository;

import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.company.model.entity.CompanyUser;
import com.uberclocked.api.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {

    boolean existsByUserAndCompany(User user, Company company);

    List<CompanyUser> findByCompany(Company company);

    List<CompanyUser> findByUser(User user);

    Optional<CompanyUser> findByUserAndCompany(User user, Company company);
}
