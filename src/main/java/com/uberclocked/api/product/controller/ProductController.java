package com.uberclocked.api.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberclocked.api.product.model.dto.ProductDataDto;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping(consumes = { "multipart/form-data" })
  @PreAuthorize("hasRole('Admin')")
  public Product create(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam String sku,
      @RequestParam String name,
      @RequestParam String componentSkuPrefix,
      @RequestParam Double price,
      @RequestParam int stock,
      @RequestParam String attributes,
      @RequestPart(required = false) MultipartFile image) throws IOException {
    Map<String, String> attributesMap = new ObjectMapper().readValue(attributes, Map.class);

    ProductDataDto dto = new ProductDataDto(
        sku,
        name,
        componentSkuPrefix,
        price,
        stock,
        attributesMap);

    return productService.create(dto, image);
  }

  @GetMapping
  public List<Product> getAll() {
    return productService.getAllActive();
  }

  @GetMapping("/{sku}")
  public Product getById(@PathVariable String sku) {
    return productService.getById(sku);
  }

  @PatchMapping(value = "/{sku}", consumes = { "multipart/form-data" })
  @PreAuthorize("hasRole('Admin')")
  public Product update(
      @AuthenticationPrincipal Jwt jwt,
      @PathVariable String sku,
      @RequestParam String name,
      @RequestParam Double price,
      @RequestParam int stock,
      @RequestParam String componentSkuPrefix,
      @RequestParam String attributes,
      @RequestPart(required = false) MultipartFile image) throws IOException {

    Map<String, String> attributesMap = new ObjectMapper().readValue(attributes, Map.class);

    ProductDataDto dto = new ProductDataDto(
        sku,
        name,
        componentSkuPrefix,
        price,
        stock,
        attributesMap);

    return productService.update(sku, dto, image);
  }

  @DeleteMapping("/{sku}")
  @PreAuthorize("hasRole('Admin')")
  public void delete(@PathVariable String sku, @AuthenticationPrincipal Jwt jwt) {
    productService.delete(sku);
  }

  @GetMapping("/filter")
  public List<Product> filter(
      @RequestParam(required = false) String componentSkuPrefix,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam Map<String, String> attributes) {

    if ("ALL".equalsIgnoreCase(componentSkuPrefix)) {
      componentSkuPrefix = null;
    }

    Set<String> reservedKeys = Set.of(
        "componentSkuPrefix",
        "minPrice",
        "maxPrice",
        "page",
        "size",
        "sort");

    attributes.keySet().removeIf(reservedKeys::contains);

    return productService.filter(componentSkuPrefix, minPrice, maxPrice, attributes);
  }

}
