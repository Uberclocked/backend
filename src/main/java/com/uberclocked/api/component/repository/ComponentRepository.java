package com.uberclocked.api.component.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uberclocked.api.component.model.entity.Component;

@Repository
public interface ComponentRepository extends JpaRepository<Component, String> {
  boolean existsByCode(String code);
}
