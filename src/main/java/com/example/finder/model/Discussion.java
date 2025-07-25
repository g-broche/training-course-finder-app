package com.example.finder.model;

import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Discussion {
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "announce_id", nullable = false)
    private Announce announce;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interlocutor_id", nullable = false)
    private AppUser interlocutor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interactivity_state_id", nullable = false)
    private InteractivityState interactivityState;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "edited_at")
    private Timestamp editedAt;

    public Discussion() {
    }

    public Discussion(Announce announce, AppUser interlocutor) {
        this.announce = announce;
        this.interlocutor = interlocutor;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Announce getAnnounce() {
        return announce;
    }

    public void setAnnounce(Announce announce) {
        this.announce = announce;
    }

    public AppUser getInterlocutor() {
        return interlocutor;
    }

    public void setInterlocutor(AppUser interlocutor) {
        this.interlocutor = interlocutor;
    }

    public InteractivityState getInteractivityState() {
        return interactivityState;
    }

    public void setInteractivityState(InteractivityState interactivityState) {
        this.interactivityState = interactivityState;
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
}
