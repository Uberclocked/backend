package com.uberclocked.api.product.productSpecification;

import com.uberclocked.api.product.model.entity.Product;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Predicate;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

  public static Specification<Product> filter(
      String componentSkuPrefix, Double minPrice, Double maxPrice, Map<String, String> attributes) {
    return (root, query, cb) -> {
      Predicate predicate = cb.equal(root.get("active"), true);

      if (componentSkuPrefix != null) {
        predicate =
            cb.and(predicate, cb.equal(root.get("component").get("skuPrefix"), componentSkuPrefix));
      }

      if (minPrice != null) {
        predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
      }

      if (maxPrice != null) {
        predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
      }

      if (attributes != null && !attributes.isEmpty()) {
        for (Map.Entry<String, String> entry : attributes.entrySet()) {

          MapJoin<Product, String, String> attributesJoin =
              root.joinMap("attributes", JoinType.INNER);

          Predicate keyMatch = cb.equal(attributesJoin.key(), entry.getKey());

          Predicate valueMatch = cb.equal(attributesJoin.value(), entry.getValue());

          predicate = cb.and(predicate, keyMatch, valueMatch);
        }
      }

      return predicate;
    };
  }
}
