package com.example.finder.repository.specification;

import com.example.finder.model.Message;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class MessageSpecifications {
    public static Specification<Message> belongToDiscussion(UUID discussionId) {
        return (root, query, cb) -> cb.equal(
                root.get("discussion").get("id"),
                discussionId);
    }

    public static Specification<Message> hasId(UUID messageId) {
        return (root, query, cb) -> cb.equal(
                root.get("id"),
                messageId);
    }

    public static Specification<Message> isReported() {
        return (root, query, cb) -> cb.isTrue(
                root.get("isReported"));
    }
}
