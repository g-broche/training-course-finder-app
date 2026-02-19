package com.example.finder.repository.specification;

import com.example.finder.model.AppUser;
import com.example.finder.model.enums.AvailableUserStatus;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class UserSpecifications {
        public static Specification<AppUser> hasDisplayNameContaining(String search) {
                return (root, query, cb) -> search == null || search.isEmpty()
                                ? cb.conjunction()
                                : cb.like(cb.lower(root.get("displayName")), "%" + search.toLowerCase() + "%");
        }

        public static Specification<AppUser> hasId(UUID userId) {
                return (root, query, cb) -> cb.equal(
                                root.get("id"),
                                userId);
        }

        public static Specification<AppUser> isBanned() {
                return (root, query, cb) -> cb.equal(
                                root.get("userStatus").get("name"),
                                AvailableUserStatus.BANNED.toString());
        }

        public static Specification<AppUser> isAllowed() {
                return (root, query, cb) -> cb.equal(
                                root.get("userStatus").get("name"),
                                AvailableUserStatus.ALLOWED.toString());
        }
}