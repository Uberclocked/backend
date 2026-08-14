package com.uberclocked.api.product.repository;

import com.uberclocked.api.product.model.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
  List<Product> findByActiveTrue();
}
