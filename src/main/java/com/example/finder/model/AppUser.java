package com.example.finder.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;
    @Column(name = "display_name", unique = true, nullable = false, length = 30)
    private String displayName;
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    @Column(name = "activation_token", nullable = true, length = 255)
    private String activationToken;

    @Column(name = "has_accepted_gdpr", nullable = false)
    private boolean hasAcceptGdpr;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "edited_at")
    private Timestamp editedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private UserStatus userStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "record_status_id", nullable = false)
    private RecordStatus recordStatus;

    public AppUser() {
    }

    public AppUser(
            String firstName,
            String lastName,
            String displayName,
            String email,
            String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public boolean getHasAcceptGdpr() {
        return hasAcceptGdpr;
    }

    public void setHasAcceptGdpr(boolean hasAcceptGdpr) {
        this.hasAcceptGdpr = hasAcceptGdpr;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Timestamp editedAt) {
        this.editedAt = editedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }
}