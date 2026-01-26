package com.example.finder.dto.output;

import com.example.finder.model.Message;

import java.sql.Timestamp;
import java.util.UUID;

public class MessageDTO {
    private UUID discussionId;
    private UUID announceId;
    private int index;
    private OtherUserDto author;
    private String content;
    private Timestamp createdAt;
    private Timestamp editedAt;

    public MessageDTO(Message message) {
        this.discussionId = message.getDiscussion().getId();
        this.announceId = message.getDiscussion().getAnnounce().getId();
        this.index = message.getIndex();
        this.author = new OtherUserDto(message.getAuthor());
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
        this.editedAt = message.getEditedAt();
    }

    public UUID getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(UUID discussionId) {
        this.discussionId = discussionId;
    }

    public UUID getAnnounceId() {
        return announceId;
    }

    public void setAnnounceId(UUID announceId) {
        this.announceId = announceId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public OtherUserDto getAuthor() {
        return author;
    }

    public void setAuthor(OtherUserDto author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
