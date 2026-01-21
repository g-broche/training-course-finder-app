package com.example.finder.repository.specification;

import com.example.finder.model.Announce;
import com.example.finder.model.AnnounceType;
import com.example.finder.model.Discussion;
import com.example.finder.model.enums.AvailableRecordStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class DiscussionSpecifications {
        public static Specification<Discussion> hasAnnounce(UUID announceId) {
                return (root, query, cb) -> cb.equal(
                                root.get("announce").get("id"),
                                announceId);
        }

        public static Specification<Discussion> hasId(UUID discussionId) {
                return (root, query, cb) -> cb.equal(
                                root.get("id"),
                                discussionId);
        }

        public static Specification<Discussion> hasAnnounceAuthor(UUID announceAuthorId) {
                return (root, query, cb) -> cb.equal(
                                root.get("announce").get("author").get("id"),
                                announceAuthorId);
        }

        public static Specification<Discussion> hasResponder(UUID responderId) {
                return (root, query, cb) -> cb.equal(
                                root.get("interlocutor").get("id"),
                                responderId);
        }

        public static Specification<Discussion> hasParticipant(UUID userId) {
                return (root, query, cb) -> cb.or(
                                cb.equal(root.get("announce").get("author").get("id"), userId),
                                cb.equal(root.get("interlocutor").get("id"), userId));
        }

        public static Specification<Discussion> mustHaveVisibleAnnounce() {
                return (root, query, cb) -> cb.equal(
                                root.get("announce").get("recordStatus").get("name"),
                                AvailableRecordStatus.SHOWN.toString());
        }
}