package com.uberclocked.api.company.mapper;

import com.uberclocked.api.company.model.dto.CompanyDataDto;
import com.uberclocked.api.company.model.entity.Company;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

  CompanyDataDto toDto(Company entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void update(CompanyDataDto dto, @MappingTarget Company entity);

  @AfterMapping
  default void extractDomain(CompanyDataDto dto, @MappingTarget Company entity) {

    if (dto.email() == null || dto.email().isBlank()) return;

    String email = dto.email().toLowerCase().trim();

    entity.setEmailDomain(email);
  }
}
