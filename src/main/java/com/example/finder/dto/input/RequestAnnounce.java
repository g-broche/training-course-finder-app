package com.example.finder.dto.input;

import com.example.finder.model.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public class RequestAnnounce {
    private String title;
    private String description;
    private File photo;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
    private LocalDate relevantDate;
    private Long categoryId;

    public RequestAnnounce() {
    }

    public RequestAnnounce(
            String title,
            String description,
            File photo,
            String city,
            String country,
            String latitude,
            String longitude,
            LocalDate relevantDate,
            Long categoryId) {
        this.title = title;
        this.description = description;
        this.photo = photo;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.relevantDate = relevantDate;
        this.categoryId = categoryId;
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

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}