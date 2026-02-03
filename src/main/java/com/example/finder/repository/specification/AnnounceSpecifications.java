package com.example.finder.repository.specification;

import com.example.finder.model.Announce;
import com.example.finder.model.AnnounceType;
import com.example.finder.model.enums.AvailableRecordStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class AnnounceSpecifications {

        public static Specification<Announce> hasType(AnnounceType type) {
                return (root, query, cb) -> type == null
                                ? cb.conjunction()
                                : cb.equal(root.get("type"), type);
        }

        public static Specification<Announce> hasCategory(Long categoryId) {
                return (root, query, cb) -> categoryId == null
                                ? cb.conjunction()
                                : cb.equal(root.get("category").get("id"), categoryId);
        }

        public static Specification<Announce> hasSearch(String search) {
                return (root, query, cb) -> search == null || search.isEmpty()
                                ? cb.conjunction()
                                : cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%");
        }

        public static Specification<Announce> hasShownStatus() {
                return (root, query, cb) -> cb.equal(
                                root.get("recordStatus").get("name"),
                                AvailableRecordStatus.SHOWN.toString());
        }

        public static Specification<Announce> hasAuthor(UUID authorId) {
                return (root, query, cb) -> cb.equal(
                                root.get("author").get("id"),
                                authorId);
        }

        public static Specification<Announce> hasId(UUID announceId) {
                return (root, query, cb) -> cb.equal(
                                root.get("id"),
                                announceId);
        }

        public static Specification<Announce> hasDiscussionWithInterlocutor(UUID announceId, UUID interlocutorId) {
                return (root, query, cb) -> {
                        if (announceId == null || interlocutorId == null) {
                                return cb.conjunction();
                        }
                        var discussionJoin = root.join("discussions");
                        return cb.and(
                                        cb.equal(root.get("id"), announceId),
                                        cb.equal(discussionJoin.get("interlocutor").get("id"), interlocutorId));
                };
        }

        public static Specification<Announce> hasDiscussion(UUID discussionId) {
                return (root, query, cb) -> {
                        if (discussionId == null) {
                                return cb.conjunction();
                        }
                        var discussionJoin = root.join("discussions");
                        return cb.equal(discussionJoin.get("id"), discussionId);
                };
        }
}