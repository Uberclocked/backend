package com.uberclocked.api.market.controller;

import com.uberclocked.api.market.model.dto.PostDataDto;
import com.uberclocked.api.market.model.dto.PostInterestDto;
import com.uberclocked.api.market.model.dto.UserPublicDto;
import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.service.PostInterestService;
import com.uberclocked.api.market.service.PostService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final PostInterestService interestService;

  public PostController(PostService postService, PostInterestService interestService) {
    this.postService = postService;
    this.interestService = interestService;
  }

  @PostMapping
  public Post create(@RequestBody PostDataDto dto, @AuthenticationPrincipal Jwt jwt) {
    return postService.create(dto, jwt);
  }

  @GetMapping
  public List<Post> getAll() {
    return postService.getAllActive();
  }

  @GetMapping("/{id}")
  public Post getById(@PathVariable UUID id) {
    return postService.getById(id);
  }

  @GetMapping("/me")
  public List<Post> myPosts(@AuthenticationPrincipal Jwt jwt) {
    return postService.getMyPosts(jwt);
  }

  @PatchMapping("/{id}")
  public Post update(
      @PathVariable UUID id, @RequestBody PostDataDto dto, @AuthenticationPrincipal Jwt jwt) {
    return postService.update(id, dto, jwt);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    postService.delete(id, jwt);
  }

  @PostMapping("/{id}/sold")
  public void markAsSold(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    postService.markAsSold(id, jwt);
  }

  @PostMapping("/{id}")
  public void markInterest(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    interestService.markInterest(id, jwt);
  }

  @PostMapping("/{id}/users/me")
  public UserPublicDto buySellerInfo(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return UserPublicDto.fromEntity(interestService.buySellerInfo(id, jwt));
  }

  @GetMapping("/{id}/interested")
  public List<PostInterestDto> getInterested(
      @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return interestService.getInterestedUsers(id, jwt);
  }
}
