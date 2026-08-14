package com.uberclocked.api.market.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.market.model.dto.PostDataDto;
import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.model.entity.PostStatus;
import com.uberclocked.api.market.repository.PostRepository;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UsersService usersService;

  public PostService(PostRepository postRepository, UsersService usersService) {
    this.postRepository = postRepository;
    this.usersService = usersService;
  }

  public Post create(PostDataDto dto, MultipartFile image, Jwt jwt) throws IOException {
    User seller = usersService.getUserOrCreate(jwt);
    byte[] img = null;
    if (image != null && !image.isEmpty()) {
      img = image.getBytes();
    }
    Post post =
            new Post(
                    dto.title(),
                    img,
                    dto.description(),
                    dto.price(),
                    dto.category(),
                    seller,
                    LocalDateTime.now());

    post.setStatus(PostStatus.ACTIVE);
    return postRepository.save(post);
  }

  public List<Post> getAll() {
    return postRepository.findAll();
  }

  public List<Post> getAllActive() {
    return postRepository.findByStatus(PostStatus.ACTIVE);
  }

  public Post getById(UUID id) {
    return postRepository
            .findById(id)
            .orElseThrow(() -> new ResourceDoesNotExistsException("Post not found"));
  }

  public List<Post> getMyPosts(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return postRepository.findBySeller(user);
  }

  public Post update(UUID id, PostDataDto dto, Jwt jwt) {
    Post post = getById(id);
    User user = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(user.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    if (post.getStatus() != PostStatus.ACTIVE) {
      throw new IllegalStateException("Only active posts can be updated");
    }

    if (dto.title() != null) post.setTitle(dto.title());
    if (dto.description() != null) post.setDescription(dto.description());
    if (dto.price() != null) post.setPrice(dto.price());
    if (dto.category() != null) post.setCategory(dto.category());

    return postRepository.save(post);
  }

  public void delete(UUID id, Jwt jwt) {
    Post post = getById(id);
    User user = usersService.getUserOrCreate(jwt);

    List<String> roles = jwt.getClaimAsStringList("https://uberclocked.com/roles");
    boolean isAdmin = roles != null && roles.contains("Admin");
    boolean isOwner = post.getSeller().getId().equals(user.getId());

    if (!isAdmin && !isOwner) {
      throw new IllegalStateException("You are not allowed to delete this post");
    }

    if (post.getStatus() == PostStatus.DELETED) {
      return;
    }

    post.setStatus(PostStatus.DELETED);
    postRepository.save(post);
  }

  public void markAsSold(UUID id, Jwt jwt) {
    Post post = getById(id);
    User user = usersService.getUserOrCreate(jwt);

    if (!post.getSeller().getId().equals(user.getId())) {
      throw new IllegalStateException("You are not the owner of this post");
    }

    if (post.getStatus() == PostStatus.DELETED) {
      throw new IllegalStateException("Deleted posts cannot be sold");
    }

    if (post.getStatus() == PostStatus.SOLD) {
      return;
    }

    post.setStatus(PostStatus.SOLD);
    postRepository.save(post);
  }
}
