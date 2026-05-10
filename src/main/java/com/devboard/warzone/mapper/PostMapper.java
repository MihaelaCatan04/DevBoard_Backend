package com.devboard.warzone.mapper;

import com.devboard.warzone.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    Post findById(@Param("id") Long id);

    void insert(Post post);

    void update(Post post);

    void delete(@Param("id") Long id);

    void updateVotes(@Param("id") Long id, @Param("direction") int direction);

    List<Post> findAll(@Param("limit") int limit, @Param("skip") int skip, @Param("tag") String tag, @Param("search") String search, @Param("sort") String sort);

    long countAll(@Param("tag") String tag, @Param("search") String search);

    List<Post> findByAuthor(@Param("username") String username,
                            @Param("limit") int limit,
                            @Param("skip") int skip);

    long countByAuthor(@Param("username") String username);
}