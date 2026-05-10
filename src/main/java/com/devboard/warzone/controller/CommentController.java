package com.devboard.warzone.controller;

import com.devboard.warzone.dto.CommentRequest;
import com.devboard.warzone.dto.PageResponse;
import com.devboard.warzone.model.Comment;
import com.devboard.warzone.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comments on posts")
public class CommentController {

    private final CommentService commentService;
    private final com.devboard.warzone.security.JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "Get comments for a post — paginated")
    public ResponseEntity<PageResponse<Comment>> getComments(@PathVariable Long postId, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int skip) {

        return ResponseEntity.ok(commentService.getComments(postId, limit, skip));
    }

    @PostMapping
    @Operation(summary = "Add a comment — requires WRITER or ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Comment> addComment(@PathVariable Long postId, @RequestBody CommentRequest request, Principal principal) {

        Comment comment = commentService.addComment(postId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment — only author or ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Principal principal, @RequestHeader("Authorization") String authHeader) {

        String role = jwtUtil.extractRole(authHeader.substring(7));
        commentService.deleteComment(postId, commentId, principal.getName(), role);
        return ResponseEntity.noContent().build();
    }
}