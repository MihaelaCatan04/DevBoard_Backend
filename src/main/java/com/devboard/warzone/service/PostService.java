package com.devboard.warzone.service;

import com.devboard.warzone.dto.PageResponse;
import com.devboard.warzone.dto.PostRequest;
import com.devboard.warzone.mapper.PostMapper;
import com.devboard.warzone.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    public PageResponse<Post> getAllPosts(int limit, int skip, String tag, String search, String sort) {
        List<Post> posts = postMapper.findAll(limit, skip, tag, search, sort);
        long total = postMapper.countAll(tag, search);
        return new PageResponse<>(posts, limit, skip, total);
    }

    public Post getPostById(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    public Post createPost(PostRequest request, String author) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setBody(request.getBody());
        post.setTag(request.getTag());
        post.setAuthor(author);
        postMapper.insert(post);
        return post;
    }

    public Post updatePost(Long id, PostRequest request, String username, String role) {
        Post post = getPostById(id);

        if (!post.getAuthor().equals(username) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own posts");
        }

        post.setTitle(request.getTitle());
        post.setBody(request.getBody());
        post.setTag(request.getTag());
        postMapper.update(post);
        return post;
    }

    public void votePost(Long id, int direction) {
        getPostById(id);
        if (direction != 1 && direction != -1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Direction must be 1 or -1");
        }
        postMapper.updateVotes(id, direction);
    }

    public void deletePost(Long id, String username, String role) {
        Post post = getPostById(id);

        if (!post.getAuthor().equals(username) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own posts");
        }

        postMapper.softDelete(id);
    }

    public void hardDeletePost(Long id) {
        getPostById(id);
        postMapper.hardDelete(id);
    }

    public void restorePost(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        if (post.getDeletedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post is not deleted");
        }
        postMapper.restore(id);
    }

    public PageResponse<Post> getAllPostsIncludingDeleted(int limit, int skip) {
        return new PageResponse<>(postMapper.findAllIncludingDeleted(limit, skip), limit, skip, postMapper.countAllIncludingDeleted());
    }
}