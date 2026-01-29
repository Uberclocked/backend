package com.uberclocked.api.user.repository;

import com.uberclocked.api.user.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {

  Optional<User> findByAuth0Id(String auth0Id);

  Optional<User> findByEmail(String email);
}
