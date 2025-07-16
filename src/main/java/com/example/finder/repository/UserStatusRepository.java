package com.example.finder.repository;

import com.example.finder.exception.RoleNotFoundException;
import com.example.finder.exception.UserStatusNotFoundException;
import com.example.finder.model.Role;
import com.example.finder.model.UserStatus;
import com.example.finder.model.enums.AvailableRoles;
import com.example.finder.model.enums.AvailableUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

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
