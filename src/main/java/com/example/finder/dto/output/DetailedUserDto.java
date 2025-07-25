package com.example.finder.dto.output;

import com.example.finder.model.AppUser;
import com.example.finder.model.Role;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public class DetailedUserDto {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private boolean isVerified;
    private boolean hasAcceptedGdpr;
    private Timestamp createdAt;
    private Set<RoleDto> roles;

    public DetailedUserDto(AppUser user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.isVerified = user.getIsVerified();
        this.hasAcceptedGdpr = user.getHasAcceptGdpr();
        this.roles = user.getRoles()
                .stream()
                .map(Role::toDto)
                .collect(Collectors.toSet());
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return isVerified;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean isHasAcceptedGdpr() {
        return hasAcceptedGdpr;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }
}
