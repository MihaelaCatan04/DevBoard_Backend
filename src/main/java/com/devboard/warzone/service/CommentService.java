package com.devboard.warzone.service;

import com.devboard.warzone.dto.CommentRequest;
import com.devboard.warzone.dto.PageResponse;
import com.devboard.warzone.mapper.CommentMapper;
import com.devboard.warzone.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostService postService;

    public PageResponse<Comment> getComments(Long postId, int limit, int skip) {
        postService.getPostById(postId);
        List<Comment> comments = commentMapper.findByPostId(postId, limit, skip);
        long total = commentMapper.countByPostId(postId);
        return new PageResponse<>(comments, limit, skip, total);
    }

    public Comment addComment(Long postId, CommentRequest request, String author) {
        postService.getPostById(postId);
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthor(author);
        comment.setBody(request.getBody());
        commentMapper.insert(comment);
        return comment;
    }

    public void deleteComment(Long postId, Long commentId,
                              String username, String role) {
        Comment comment = commentMapper.findById(commentId);

        if (comment == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Comment not found"
            );
        }

        if (!comment.getPostId().equals(postId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Comment does not belong to this post"
            );
        }

        if (!comment.getAuthor().equals(username) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only delete your own comments"
            );
        }

        commentMapper.delete(commentId);
    }
}