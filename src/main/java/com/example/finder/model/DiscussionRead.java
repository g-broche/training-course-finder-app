package com.example.finder.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Intended as a way to keep track to how far into a discussion a user as read.
 * The class has been made in case such feature is implemented later on but it
 * is not considered as a priority
 */
@Entity
public class DiscussionRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Reader_id", nullable = false)
    private AppUser reader;

    @Column(name = "last_read_message_index", nullable = true)
    private int lastReadMessageIndex;

    @Column(name = "read_at", nullable = true)
    private Timestamp readAt;
}
