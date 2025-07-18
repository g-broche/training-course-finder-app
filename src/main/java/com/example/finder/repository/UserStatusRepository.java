package com.example.finder.repository;

import com.example.finder.exception.entity.UserStatusNotFoundException;
import com.example.finder.model.UserStatus;
import com.example.finder.model.enums.AvailableUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    Optional<UserStatus> findByName(String userStatusName);

    default UserStatus getAllowedUserStatusOrThrow() {
        return findByName(AvailableUserStatus.ALLOWED.toString())
                .orElseThrow(UserStatusNotFoundException::new);
    }

    default UserStatus getBannedUserStatusOrThrow() {
        return findByName(AvailableUserStatus.BANNED.toString())
                .orElseThrow(UserStatusNotFoundException::new);
    }
}
