package com.devboard.warzone.mapper;

import com.devboard.warzone.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    List<Post> findAll(@Param("limit") int limit, @Param("skip") int skip, @Param("tag") String tag);

    long countAll(@Param("tag") String tag);

    Post findById(@Param("id") Long id);

    void insert(Post post);

    void update(Post post);

    void delete(@Param("id") Long id);

    void updateVotes(@Param("id") Long id, @Param("direction") int direction);
}