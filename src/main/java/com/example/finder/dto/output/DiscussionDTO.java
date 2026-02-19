package com.example.finder.dto.output;

import com.example.finder.model.*;

import java.sql.Timestamp;
import java.util.UUID;

public class DiscussionDTO {
    private UUID discussionId;
    private UUID announceId;
    private OtherUserDto announceAuthor;
    private OtherUserDto announceResponder;
    private String excerpt;
    private String interactivityStateName;
    private int messageCount;
    private Timestamp createdAt;
    private Timestamp editedAt;
    private Timestamp lastMessageDate;

    public DiscussionDTO(Discussion discussion) {
        this.discussionId = discussion.getId();
        this.announceId = discussion.getAnnounce().getId();
        this.announceAuthor = new OtherUserDto(discussion.getAnnounce().getAuthor());
        this.announceResponder = new OtherUserDto(discussion.getInterlocutor());
        this.interactivityStateName = discussion.getInteractivityState().getName();
        this.messageCount = discussion.getMessages().size();
        this.excerpt = discussion.getExcerpt();
        this.createdAt = discussion.getCreatedAt();
        this.editedAt = discussion.getEditedAt();
        this.lastMessageDate = discussion.getLastMessageTimestamp();
    }

    public UUID getDiscussionId() {
        return discussionId;
    }

    public UUID getAnnounceId() {
        return announceId;
    }

    public OtherUserDto getAnnounceAuthor() {
        return announceAuthor;
    }

    public OtherUserDto getAnnounceResponder() {
        return announceResponder;
    }

    public String getInteractivityStateName() {
        return interactivityStateName;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getEditedAt() {
        return editedAt;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public Timestamp getLastMessageDate() {
        return lastMessageDate;
    }
}
