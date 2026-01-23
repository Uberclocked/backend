package com.uberclocked.api.product.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.service.ComponentService;
import com.uberclocked.api.product.mapper.ProductMapper;
import com.uberclocked.api.product.model.dto.ProductDataDto;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.productSpecification.ProductSpecification;
import com.uberclocked.api.product.repository.ProductRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    public ProductRepository productRepository;
    public ComponentService componentService;
    public ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ComponentService componentService, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.componentService = componentService;
        this.productMapper = productMapper;
    }
    public Product create(ProductDataDto dto) {
        if (productRepository.existsById(dto.sku())) {
            throw new IllegalArgumentException("Product with this SKU already exists");
        }
        Component component =
                componentService.getEntityById(dto.componentSkuPrefix());
        Product product =
                new Product(
                        dto.sku(),
                        dto.name(),
                        component,
                        dto.price(),
                        dto.stock());
        product.setAttributes(dto.attributes());
        return productRepository.save(product);
    }

    public List<Product> getAllActive() {
        return productRepository.findByActiveTrue();
    }

    public Product getById(String sku) {
        return productRepository
                .findById(sku)
                .orElseThrow(() ->
                        new ResourceDoesNotExistsException("Product with SKU '" + sku + "' not found"));
    }

    public Product update(String sku, ProductDataDto dto) {
        Product product = getById(sku);
        productMapper.update(dto, product);
        if (dto.componentSkuPrefix() != null) {
            Component component =
                    componentService.getEntityById(dto.componentSkuPrefix());
            product.setComponent(component);
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
            Map<String, String> attributes
    ) {

        Specification<Product> spec =
                ProductSpecification.filter(
                        componentSkuPrefix,
                        minPrice,
                        maxPrice,
                        attributes
                );

        return productRepository.findAll(spec);
    }
}
