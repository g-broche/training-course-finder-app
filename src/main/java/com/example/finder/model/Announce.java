package com.example.finder.model;

import jakarta.persistence.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Announce {
    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private UUID id;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "photo", nullable = true, length = 255)
    private String photo;
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    @Column(name = "country", nullable = false, length = 50)
    private String country;
    @Column(name = "latitude", nullable = true, length = 15)
    private String latitude;
    @Column(name = "longitude", nullable = true, length = 15)
    private String longitude;
    @Column(name = "relevant_date", nullable = true)
    private LocalDate relevantDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false)
    private AnnounceType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interactivity_state_id", nullable = false)
    private InteractivityState interactivityState;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "record_status_id", nullable = false)
    private RecordStatus recordStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "edited_at")
    private Timestamp editedAt;

    public Announce() {

    }
    public Announce(String title, String description, LocalDate relevantDate, String city) {
        this.title = title;
        this.description = description;
        this.relevantDate = relevantDate;
        this.city = city;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public LocalDate getRelevantDate() {
        return relevantDate;
    }

    public void setRelevantDate(LocalDate relevantDate) {
        this.relevantDate = relevantDate;
    }

    public AnnounceType getType() {
        return type;
    }

    public void setType(AnnounceType type) {
        this.type = type;
    }

    public AppUser getAuthor() {
        return author;
    }

    public void setAuthor(AppUser author) {
        this.author = author;
    }

    public InteractivityState getInteractivityState() {
        return interactivityState;
    }

    public void setInteractivityState(InteractivityState interactivityState) {
        this.interactivityState = interactivityState;
    }

    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(RecordStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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