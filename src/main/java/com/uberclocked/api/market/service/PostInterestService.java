package com.uberclocked.api.market.service;

import com.uberclocked.api.market.mapper.PostInterestMapper;
import com.uberclocked.api.market.model.dto.PostInterestDto;
import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.model.entity.PostInterest;
import com.uberclocked.api.market.repository.PostInterestRepository;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class PostInterestService {

  private final PostService postService;
  private final PostInterestRepository interestRepository;
  private final UsersService usersService;

  public PostInterestService(
      PostService postService,
      PostInterestRepository interestRepository,
      UsersService usersService) {
    this.postService = postService;
    this.interestRepository = interestRepository;
    this.usersService = usersService;
  }

  public void markInterest(UUID postId, Jwt jwt) {
    Post post = postService.getById(postId);
    User user = usersService.getUserOrCreate(jwt);

    if (post.getSeller().getId().equals(user.getId())) {
      throw new IllegalStateException("You cannot be interested in your own post");
    }

    boolean exists = interestRepository.existsByPostAndInterested(post, user);
    if (exists) return;

    PostInterest interest = new PostInterest(post, user);
    interestRepository.save(interest);
  }

  public User buySellerInfo(UUID postId, Jwt jwt) {
    Post post = postService.getById(postId);

    User buyer = usersService.getUserOrCreate(jwt);

    PostInterest interest =
        interestRepository
            .findByPostAndInterested(post, buyer)
            .orElseThrow(() -> new IllegalStateException("You must mark interest first"));

    if (!interest.isInfoPurchased()) {
      interest.setInfoPurchased(true);
      interestRepository.save(interest);
    }

    return post.getSeller();
  }

  public List<PostInterestDto> getInterestedUsers(UUID postId, Jwt jwt) {
    Post post = postService.getById(postId);
    User seller = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(seller.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    return interestRepository.findByPost(post).stream().map(PostInterestMapper::toDto).toList();
  }
}
