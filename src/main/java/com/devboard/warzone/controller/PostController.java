package com.devboard.warzone.controller;

import com.devboard.warzone.dto.PageResponse;
import com.devboard.warzone.dto.PostRequest;
import com.devboard.warzone.model.Post;
import com.devboard.warzone.security.JwtUtil;
import com.devboard.warzone.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "War Stories posts")
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get posts — paginated, filterable, searchable, sortable")
    public ResponseEntity<PageResponse<Post>> getAllPosts(
            @RequestParam(defaultValue = "10")   int limit,
            @RequestParam(defaultValue = "0")    int skip,
            @RequestParam(required = false)      String tag,
            @RequestParam(required = false)      String search,
            @RequestParam(defaultValue = "date") String sort) {

        return ResponseEntity.ok(
                postService.getAllPosts(limit, skip, tag, search, sort)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single post by id")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    @Operation(summary = "Create a post — requires WRITER or ADMIN role", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest request, Principal principal) {  // Principal gives us the logged-in username

        Post post = postService.createPost(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(post);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a post — only author or ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Post> updatePost(@Valid @PathVariable Long id, @RequestBody PostRequest request, Principal principal, @RequestHeader("Authorization") String authHeader) {

        String role = extractRole(authHeader);

        Post post = postService.updatePost(id, request, principal.getName(), role);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post — only author or ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Principal principal, @RequestHeader("Authorization") String authHeader) {

        String role = extractRole(authHeader);
        postService.deletePost(id, principal.getName(), role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/vote")
    @Operation(summary = "Vote on a post — pass direction: 1 or -1", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> vote(@PathVariable Long id, @RequestParam int direction) {

        postService.votePost(id, direction);
        return ResponseEntity.ok().build();
    }

    private String extractRole(String authHeader) {
        return jwtUtil.extractRole(authHeader.substring(7));
    }
}