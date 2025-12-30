package com.uberclocked.api.component.repository;

import com.uberclocked.api.component.model.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentRepository extends JpaRepository<Component, String> {
  boolean existsBySkuPrefix(String skuPrefix);
}
