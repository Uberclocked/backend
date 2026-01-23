package com.uberclocked.api.market.repository;

import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.model.entity.PostInterest;
import com.uberclocked.api.user.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostInterestRepository extends JpaRepository<PostInterest, UUID> {

  boolean existsByPostAndInterested(Post post, User interested);

  Optional<PostInterest> findByPostAndInterested(Post post, User interested);

  List<PostInterest> findByPost(Post post);

  List<PostInterest> findByInterested(User user);
}
