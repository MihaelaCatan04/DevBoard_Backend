package com.devboard.warzone.mapper;

import com.devboard.warzone.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> findAll(@Param("limit") int limit, @Param("skip") int skip, @Param("tag") String tag, @Param("search") String search, @Param("sort") String sort);

    long countAll(@Param("tag") String tag, @Param("search") String search);

    Post findById(@Param("id") Long id);

    List<Post> findAllIncludingDeleted(@Param("limit") int limit, @Param("skip") int skip);

    long countAllIncludingDeleted();

    void insert(Post post);

    void update(Post post);

    void softDelete(@Param("id") Long id);

    void hardDelete(@Param("id") Long id);

    void restore(@Param("id") Long id);

    void updateVotes(@Param("id") Long id, @Param("direction") int direction);

    List<Post> findByAuthor(@Param("username") String username, @Param("limit") int limit, @Param("skip") int skip);

    long countByAuthor(@Param("username") String username);
}