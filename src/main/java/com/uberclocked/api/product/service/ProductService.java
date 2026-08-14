package com.uberclocked.api.product.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.service.ComponentService;
import com.uberclocked.api.product.mapper.ProductMapper;
import com.uberclocked.api.product.model.dto.ProductDataDto;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.repository.ProductRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductService {

  public ProductRepository productRepository;
  public ComponentService componentService;
  public ProductMapper productMapper;

  public ProductService(
      ProductRepository productRepository,
      ComponentService componentService,
      ProductMapper productMapper) {
    this.productRepository = productRepository;
    this.componentService = componentService;
    this.productMapper = productMapper;
  }

  public Product create(ProductDataDto dto, MultipartFile image) throws IOException {
    if (productRepository.existsById(dto.sku())) {
      throw new IllegalArgumentException("Product with this SKU already exists");
    }
    Component component = componentService.getEntityById(dto.componentSkuPrefix());
    Product product = productMapper.toEntity(dto);
    if (image != null && !image.isEmpty()) {
      product.setImage(image.getBytes());
    }
    product.setSkuPrefix(dto.sku());
    product.setComponent(component);
    product.initializeAttributesFromComponent(dto.attributes());

    return productRepository.save(product);
  }

  public List<Product> getAllActive() {
    return productRepository.findByActiveTrue();
  }

  public Product getById(String sku) {
    return productRepository
        .findById(sku)
        .orElseThrow(
            () -> new ResourceDoesNotExistsException("Product with SKU '" + sku + "' not found"));
  }

  public Product update(String sku, ProductDataDto dto,MultipartFile image) throws IOException {
    Product product = getById(sku);
    productMapper.update(dto, product);
    if (dto.componentSkuPrefix() != null) {
      Component component = componentService.getEntityById(dto.componentSkuPrefix());
      product.setComponent(component);
      product.clearAttributes();
      product.initializeAttributesFromComponent(dto.attributes());
      if (image != null && !image.isEmpty()) {
        product.setImage(image.getBytes());
      }
    }

    return productRepository.save(product);
  }

  public void delete(String sku) {
    Product product = getById(sku);
    product.setActive(false);
    productRepository.save(product);
  }

  public List<Product> filter(
          String componentSkuPrefix,
          Double minPrice,
          Double maxPrice,
          Map<String, String> attributes) {

    Specification<Product> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (componentSkuPrefix != null && !componentSkuPrefix.isEmpty()) {
        predicates.add(cb.like(root.get("skuPrefix"), componentSkuPrefix + "%"));
      }

      if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
      }

      if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
      }

      attributes.forEach((key, value) -> {
        if (value != null && !value.isEmpty()) {
          MapJoin<Product, String, String> join = root.joinMap("attributes", JoinType.LEFT);
          predicates.add(cb.and(
                  cb.equal(cb.lower(join.key()), key.toLowerCase()),
                  cb.equal(cb.lower(join.value()), value.toLowerCase())
          ));
        }
      });

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    return productRepository.findAll(spec);
  }

  @Transactional
  public void decreaseStock(String sku, int quantity) {

    Product product = getById(sku);

    if (product.getStock() < quantity) {
      throw new IllegalArgumentException("Not enough stock for product " + product.getName());
    }

    product.setStock(product.getStock() - quantity);
    productRepository.save(product);
  }
}
