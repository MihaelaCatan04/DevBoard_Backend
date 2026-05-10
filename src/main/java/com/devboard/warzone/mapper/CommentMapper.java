package com.devboard.warzone.mapper;

import com.devboard.warzone.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> findByPostId(@Param("postId") Long postId,
                               @Param("limit") int limit,
                               @Param("skip") int skip);

    long countByPostId(@Param("postId") Long postId);

    Comment findById(@Param("id") Long id);

    void insert(Comment comment);

    void delete(@Param("id") Long id);
}