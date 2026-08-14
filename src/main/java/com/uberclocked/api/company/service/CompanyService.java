package com.uberclocked.api.company.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.company.mapper.CompanyMapper;
import com.uberclocked.api.company.model.dto.CompanyDataDto;
import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.company.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public Company createCompany(Company company) {
    if (companyRepository.existsByEmailDomain(company.getEmailDomain())) {
      throw new IllegalStateException("A company with this domain already exists");
    }
    company.setId(UUID.randomUUID());
    return companyRepository.save(company);
  }

  public List<Company> getAllCompanies() {
    return companyRepository.findAll();
  }

  public Company getCompany(UUID id) {
    return companyRepository
        .findById(id)
        .orElseThrow(() -> new ResourceDoesNotExistsException("Company not found"));
  }

  public Company updateCompany(UUID id, CompanyDataDto dto, CompanyMapper mapper) {
    Company company = getCompany(id);
    mapper.update(dto, company);
    return companyRepository.save(company);
  }

  @Transactional
  public void deleteCompany(UUID id) {
    Company company = getCompany(id);
    companyRepository.delete(company);
  }

  public Optional<Company> findByDomain(String domain) {
    return companyRepository.findByEmailDomain(domain);
  }
}
