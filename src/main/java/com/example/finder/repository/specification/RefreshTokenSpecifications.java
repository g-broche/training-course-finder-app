package com.example.finder.repository.specification;

import com.example.finder.model.AppUser;
import com.example.finder.model.RefreshToken;
import org.springframework.data.jpa.domain.Specification;

public class RefreshTokenSpecifications {
    public static Specification<RefreshToken> belongsToUser(AppUser user) {
        return (root, query, cb) -> cb.equal(
                root.get("user"),
                user);
    }

    public static Specification<RefreshToken> isRevoked() {
        return (root, query, cb) -> cb.isTrue(
                root.get("isRevoked"));
    }
}
