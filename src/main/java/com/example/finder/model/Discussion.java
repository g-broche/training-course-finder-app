package com.example.finder.model;

import com.example.finder.dto.output.AdminDiscussionDTO;
import com.example.finder.dto.output.DetailedDiscussionDTO;
import com.example.finder.dto.output.DiscussionDTO;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

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

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setDiscussion(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setDiscussion(null);
    }

    public Timestamp getLastMessageTimestamp() {
        if (messages.isEmpty()) {
            return null;
        }
        Message lastMessage = messages.stream()
                .max((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .orElse(null);
        return lastMessage != null ? lastMessage.getCreatedAt() : null;
    }

    public boolean hasReportedMessage() {
        return messages.stream().anyMatch(Message::isReported);
    }

    public DetailedDiscussionDTO toDetailedDiscussionDTO() {
        return new DetailedDiscussionDTO(this);
    }

    public DiscussionDTO toDiscussionDTO() {
        return new DiscussionDTO(this);
    }

    public AdminDiscussionDTO toAdminDiscussionDTO() {
        return new AdminDiscussionDTO(this);
    }

    public String getExcerpt() {
        if (messages.isEmpty()) {
            return "";
        }
        Message[] sortedMessages = this.getMessages().stream()
                .sorted((m1, m2) -> Integer.compare(m1.getIndex(), m2.getIndex()))
                .toArray(Message[]::new);
        String excerpt = sortedMessages[0].getContent();
        return excerpt;
    }
}
