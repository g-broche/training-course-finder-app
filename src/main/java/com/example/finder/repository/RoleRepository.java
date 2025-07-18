package com.example.finder.repository;

import com.example.finder.exception.entity.RoleNotFoundException;
import com.example.finder.model.Role;
import com.example.finder.model.enums.AvailableRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String roleName);

    default Role getAdminRoleOrThrow() {
        return findByName(AvailableRoles.ADMIN.toString())
                .orElseThrow(RoleNotFoundException::new);
    }

    default Role getUserRoleOrThrow() {
        return findByName(AvailableRoles.USER.toString())
                .orElseThrow(RoleNotFoundException::new);
    }
}
