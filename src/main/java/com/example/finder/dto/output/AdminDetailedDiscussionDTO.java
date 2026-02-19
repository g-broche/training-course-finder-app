package com.example.finder.dto.output;

import com.example.finder.model.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class AdminDetailedDiscussionDTO {
    private UUID discussionId;
    private UUID announceId;
    private String announceTitle;
    private OtherUserDto announceAuthor;
    private OtherUserDto announceResponder;
    private String interactivityStateName;
    private List<MessageDTO> messages;
    private boolean hasReportedMessage;
    private Timestamp createdAt;
    private Timestamp editedAt;
    private Timestamp lastMessageDate;

    public AdminDetailedDiscussionDTO(Discussion discussion) {
        this.discussionId = discussion.getId();
        this.announceId = discussion.getAnnounce().getId();
        this.announceAuthor = new OtherUserDto(discussion.getAnnounce().getAuthor());
        this.announceResponder = new OtherUserDto(discussion.getInterlocutor());
        this.interactivityStateName = discussion.getInteractivityState().getName();
        this.messages = discussion.getMessages().stream()
                .sorted((m1, m2) -> Integer.compare(m1.getIndex(), m2.getIndex()))
                .map(Message::toDto)
                .toList();
        this.createdAt = discussion.getCreatedAt();
        this.editedAt = discussion.getEditedAt();
        this.announceTitle = discussion.getAnnounce().getTitle();
        this.lastMessageDate = discussion.getLastMessageTimestamp();
        this.hasReportedMessage = discussion.hasReportedMessage();
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

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getEditedAt() {
        return editedAt;
    }

    public String getAnnounceTitle() {
        return announceTitle;
    }

    public void setAnnounceTitle(String announceTitle) {
        this.announceTitle = announceTitle;
    }

    public Timestamp getLastMessageDate() {
        return lastMessageDate;
    }

    public boolean getHasReportedMessage() {
        return hasReportedMessage;
    }
}
