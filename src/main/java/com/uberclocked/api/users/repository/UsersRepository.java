package com.uberclocked.api.users.repository;

import com.uberclocked.api.users.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<User, UUID> {

  Optional<User> findByAuth0Id(String auth0Id);

  void deleteByAuth0Id(String auth0Id);
}
