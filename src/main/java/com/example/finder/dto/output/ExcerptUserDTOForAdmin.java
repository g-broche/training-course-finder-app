package com.example.finder.dto.output;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.finder.model.AppUser;
import com.example.finder.model.Role;

public class ExcerptUserDTOForAdmin {
    private String id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private boolean isVerified;
    private String status;
    private boolean hasAcceptedGdpr;
    private Timestamp createdAt;
    private Set<String> roles;
    private boolean hasHadReportedMessages;

    public ExcerptUserDTOForAdmin(AppUser user) {
        this.id = user.getId().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.isVerified = user.getIsVerified();
        this.hasAcceptedGdpr = user.getHasAcceptGdpr();
        this.status = user.getUserStatus().getName();
        this.roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        this.createdAt = user.getCreatedAt();
        this.hasHadReportedMessages = user
                .getMessages()
                .stream()
                .anyMatch(message -> message.isReported());
    }

    public String getId() {
        return id;
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

    public boolean getIsVerified() {
        return isVerified;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean getHasAcceptedGdpr() {
        return hasAcceptedGdpr;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getStatus() {
        return status;
    }

    public boolean getHasHadReportedMessages() {
        return hasHadReportedMessages;
    }
}
