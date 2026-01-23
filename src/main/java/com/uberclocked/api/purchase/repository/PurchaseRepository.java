package com.uberclocked.api.purchase.repository;

import com.uberclocked.api.purchase.model.entity.Purchase;
import com.uberclocked.api.user.model.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

  List<Purchase> findByUser(User user);
}
