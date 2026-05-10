package com.devboard.warzone.controller;

import com.devboard.warzone.dto.PageResponse;
import com.devboard.warzone.mapper.PostMapper;
import com.devboard.warzone.mapper.UserMapper;
import com.devboard.warzone.model.Post;
import com.devboard.warzone.model.User;
import com.devboard.warzone.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin only endpoints")
public class AdminController {

    private final PostService postService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;

    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get a user by username — ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        user.setPassword("[hidden]");
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users/{username}/posts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all posts by a user — ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<Post>> getUserPosts(@PathVariable String username, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int skip) {

        return ResponseEntity.ok(new PageResponse<>(postMapper.findByAuthor(username, limit, skip), limit, skip, postMapper.countByAuthor(username)));
    }

    @DeleteMapping("/users/{username}/posts")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete all posts by a user — ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteAllUserPosts(@PathVariable String username) {
        postMapper.findByAuthor(username, 1000, 0).forEach(post -> postMapper.softDelete(post.getId()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts")
    @Operation(summary = "Get ALL posts including deleted — ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPostsIncludingDeleted(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int skip) {
        return ResponseEntity.ok(postService.getAllPostsIncludingDeleted(limit, skip));
    }

    @PostMapping("/posts/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore a soft deleted post — ADMIN only", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> restorePost(@PathVariable Long id) {
        postService.restorePost(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Permanently delete a post — ADMIN only, irreversible", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> hardDeletePost(@PathVariable Long id) {
        postService.hardDeletePost(id);
        return ResponseEntity.noContent().build();
    }
}