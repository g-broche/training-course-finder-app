package com.example.finder.repository;

import com.example.finder.model.Announce;
import com.example.finder.model.AppUser;
import com.example.finder.model.Discussion;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DiscussionRepository extends JpaRepository<Discussion, UUID>, JpaSpecificationExecutor<Discussion> {

    @Query(value = "SELECT d.id FROM discussion d " +
            "LEFT JOIN (SELECT discussion_id, MAX(created_at) as max_created_at " +
            "           FROM message " +
            "           GROUP BY discussion_id) m " +
            "ON d.id = m.discussion_id " +
            "ORDER BY COALESCE(m.max_created_at, d.created_at) DESC", nativeQuery = true)
    List<String> findDiscussionIdsOrderByLastMessageDate();
}
