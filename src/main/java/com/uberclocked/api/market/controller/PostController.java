package com.uberclocked.api.market.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberclocked.api.market.model.dto.PostDataDto;
import com.uberclocked.api.market.model.dto.PostInterestDto;
import com.uberclocked.api.market.model.dto.PostResponseDto;
import com.uberclocked.api.market.model.dto.UserPublicDto;
import com.uberclocked.api.market.service.PostInterestService;
import com.uberclocked.api.market.service.PostService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
public class PostController {

  private final PostService postService;
  private final PostInterestService interestService;

  public PostController(PostService postService, PostInterestService interestService) {
    this.postService = postService;
    this.interestService = interestService;
  }

  @GetMapping("/admin/all")
  public List<PostResponseDto> getAllForAdmin(@AuthenticationPrincipal Jwt jwt) {
    List<String> roles = jwt.getClaimAsStringList("https://uberclocked.com/roles");
    boolean isAdmin = roles != null && roles.contains("Admin");
    if (!isAdmin) throw new IllegalStateException("Forbidden");
    return postService.getAll().stream().map(PostResponseDto::fromEntity).toList();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public PostResponseDto create(
          @RequestPart("data") PostDataDto dto,
          @RequestPart(value = "image", required = false) MultipartFile image,
          @AuthenticationPrincipal Jwt jwt) throws IOException {
    return PostResponseDto.fromEntity(postService.create(dto, image, jwt));
  }

  @GetMapping
  public List<PostResponseDto> getAll() {
    return postService.getAllActive().stream().map(PostResponseDto::fromEntity).toList();
  }

  @GetMapping("/{id}")
  public PostResponseDto getById(@PathVariable UUID id) {
    return PostResponseDto.fromEntity(postService.getById(id));
  }

  @GetMapping("/me")
  public List<PostResponseDto> myPosts(@AuthenticationPrincipal Jwt jwt) {
    return postService.getMyPosts(jwt).stream().map(PostResponseDto::fromEntity).toList();
  }

  @PatchMapping("/{id}")
  public PostResponseDto update(
          @PathVariable UUID id, @RequestBody PostDataDto dto, @AuthenticationPrincipal Jwt jwt) {
    return PostResponseDto.fromEntity(postService.update(id, dto, jwt));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    postService.delete(id, jwt);
  }

  @PostMapping("/{id}/sold")
  public void markAsSold(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    postService.markAsSold(id, jwt);
  }

  @PostMapping("/{id}/interest")
  public void markInterest(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    interestService.markInterest(id, jwt);
  }

  @GetMapping("/{id}/interested")
  public List<PostInterestDto> getInterested(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return interestService.getInterestedUsers(id, jwt);
  }

  @PostMapping("/{postId}/interested/{interestedUserId}/purchase")
  public UserPublicDto purchaseInterestedInfo(
          @PathVariable UUID postId,
          @PathVariable UUID interestedUserId,
          @AuthenticationPrincipal Jwt jwt) {
    return UserPublicDto.fromEntity(interestService.buyInterestedInfo(postId, interestedUserId, jwt));
  }

  @GetMapping("/{postId}/interested/{interestedUserId}")
  public UserPublicDto getInterestedInfo(
          @PathVariable UUID postId,
          @PathVariable UUID interestedUserId,
          @AuthenticationPrincipal Jwt jwt) {
    return UserPublicDto.fromEntity(interestService.getInterestedInfoIfPurchased(postId, interestedUserId, jwt));
  }
  @GetMapping("/{id}/interest/me")
  public boolean hasMyInterest(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    return interestService.hasInterest(id, jwt);
  }
}