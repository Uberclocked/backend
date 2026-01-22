package com.uberclocked.api.company.service;

import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.company.model.entity.CompanyUser;
import com.uberclocked.api.company.repository.CompanyUserRepository;
import com.uberclocked.api.users.model.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyUserService {

    private final CompanyUserRepository companyUserRepository;

    public CompanyUserService(CompanyUserRepository companyUserRepository) {
        this.companyUserRepository = companyUserRepository;
    }

    public CompanyUser addUserToCompany(User user, Company company) {
        if (companyUserRepository.existsByUserAndCompany(user, company)) {
            throw new IllegalStateException("User already belongs to this company");
        }
        CompanyUser relation = new CompanyUser(user, company);
        return companyUserRepository.save(relation);
    }

    public void removeUserFromCompany(User user, Company company) {
        companyUserRepository.findByUserAndCompany(user, company)
                .ifPresent(companyUserRepository::delete);
    }

    public boolean isUserInCompany(User user, Company company) {
        return companyUserRepository.existsByUserAndCompany(user, company);
    }

    public List<CompanyUser> getUsersOfCompany(Company company) {
        return companyUserRepository.findByCompany(company);
    }

    public List<CompanyUser> getCompaniesOfUser(User user) {
        return companyUserRepository.findByUser(user);
    }
}