package com.uberclocked.api.market.repository;

import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.model.entity.PostStatus;
import com.uberclocked.api.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

  Optional<Post> findById(UUID postId);

  List<Post> findByStatus(PostStatus status);

  List<Post> findBySeller(User seller);
}
