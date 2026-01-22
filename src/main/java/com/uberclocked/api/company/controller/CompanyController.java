package com.uberclocked.api.company.controller;

import com.uberclocked.api.company.model.dto.CompanyDataDto;
import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.company.mapper.CompanyMapper;
import com.uberclocked.api.company.service.CompanyService;
import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyMapper mapper;

    public CompanyController(CompanyService companyService, CompanyMapper mapper) {
        this.companyService = companyService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@Valid @RequestBody CompanyDataDto dto) {
        Company company = new Company();
        mapper.update(dto, company);
        return companyService.createCompany(company);
    }

    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping("/{id}")
    public Company getCompany(@PathVariable UUID id) {
        return companyService.getCompany(id);
    }

    @PatchMapping("/{id}")
    public Company updateCompany(@PathVariable UUID id, @Valid @RequestBody CompanyDataDto dto) {
        return companyService.updateCompany(id, dto, mapper);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable UUID id) {
        companyService.deleteCompany(id);
    }
}
