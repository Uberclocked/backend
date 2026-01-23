package com.uberclocked.api.product.controller;

import com.uberclocked.api.product.model.dto.ProductDataDto;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Product create(@RequestBody ProductDataDto dto) {
    return productService.create(dto);
  }

  @GetMapping
  public List<Product> getAll() {
    return productService.getAllActive();
  }

  @GetMapping("/{sku}")
  public Product getById(@PathVariable String sku) {
    return productService.getById(sku);
  }

  @PatchMapping("/{sku}")
  @PreAuthorize("hasRole('ADMIN')")
  public Product update(@PathVariable String sku, @RequestBody ProductDataDto dto) {
    return productService.update(sku, dto);
  }

  @DeleteMapping("/{sku}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable String sku) {
    productService.delete(sku);
  }

  @GetMapping("/filter")
  public List<Product> filter(
      @RequestParam(required = false) String componentSkuPrefix,
      @RequestParam(required = false) Double minPrice,
      @RequestParam(required = false) Double maxPrice,
      @RequestParam Map<String, String> attributes) {
    attributes.remove("componentSkuPrefix");
    attributes.remove("minPrice");
    attributes.remove("maxPrice");

    return productService.filter(componentSkuPrefix, minPrice, maxPrice, attributes);
  }
}
