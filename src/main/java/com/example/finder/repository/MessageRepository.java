package com.example.finder.repository;

import com.example.finder.model.Discussion;
import com.example.finder.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>{
    
    @Query("SELECT MAX(m.index) FROM Message m WHERE m.discussion.id = :discussionId")
    Optional<Integer> findMaxIndexByDiscussionId(@Param("discussionId") UUID discussionId);
}
