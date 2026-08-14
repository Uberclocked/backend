package com.uberclocked.api.market.service;

import com.uberclocked.api.emailData.EmailService;
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
  private final EmailService emailService;

  public PostInterestService(
      PostService postService,
      PostInterestRepository interestRepository,
      UsersService usersService,
      EmailService emailService) {
    this.postService = postService;
    this.interestRepository = interestRepository;
    this.usersService = usersService;
    this.emailService = emailService;
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

  public List<PostInterestDto> getInterestedUsers(UUID postId, Jwt jwt) {
    Post post = postService.getById(postId);
    User seller = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(seller.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    return interestRepository.findByPost(post).stream().map(PostInterestMapper::toDto).toList();
  }

  public User buyInterestedInfo(UUID postId, UUID interestedUserId, Jwt jwt) {
    Post post = postService.getById(postId);
    User seller = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(seller.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    User interested = usersService.getUSerById(interestedUserId);

    PostInterest interest =
            interestRepository
                    .findByPostAndInterested(post, interested)
                    .orElseThrow(() -> new IllegalStateException("This user is not interested in this post"));

    if (!interest.isInfoPurchased()) {
      interest.setInfoPurchased(true);
      interestRepository.save(interest);
      emailService.sendMail(
              seller.getEmail(),
              "Information of the interested - UberClocked",
              buildInterestedBody(post, interested)
      );
    }
    return interested;
  }

  public User getInterestedInfoIfPurchased(UUID postId, UUID interestedUserId, Jwt jwt) {
    Post post = postService.getById(postId);
    User seller = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(seller.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    User interested = usersService.getUSerById(interestedUserId);

    PostInterest interest =
            interestRepository
                    .findByPostAndInterested(post, interested)
                    .orElseThrow(() -> new IllegalStateException("This user is not interested in this post"));

    if (!interest.isInfoPurchased()) {
      throw new IllegalStateException("Info not purchased yet");
    }

    return interested;
  }

  public boolean hasInterest(UUID postId, Jwt jwt) {
    Post post = postService.getById(postId);
    User user = usersService.getUserOrCreate(jwt);

    if (post.getSeller().getId().equals(user.getId())) {
      return false;
    }

    return interestRepository.existsByPostAndInterested(post, user);
  }


  private String buildInterestedBody(Post post, User interested) {
    return """
    You have successfully purchased the contact information of an interested user for your post:

    Post: %s

    Interested User Details:
    Username: %s
    Email: %s
   Phone: %s
    Country: %s

    You can now contact this user directly.

    Thank you for using UberClocked.
   """.formatted(
            post.getTitle(),
            interested.getUserName(),
            interested.getEmail(),
            interested.getCellPhone(),
            interested.getCountry()
    );
  }
}
