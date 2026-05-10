package com.devboard.warzone.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VoteMapper {

    Integer findVote(@Param("username") String username, @Param("postId") Long postId);

    void insertVote(@Param("username") String username, @Param("postId") Long postId, @Param("direction") int direction);

    void updateVote(@Param("username") String username, @Param("postId") Long postId, @Param("direction") int direction);

    void deleteVote(@Param("username") String username, @Param("postId") Long postId);
}