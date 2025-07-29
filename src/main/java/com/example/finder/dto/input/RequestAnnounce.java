package com.example.finder.dto.input;

import java.time.LocalDate;

public class RequestAnnounce {
    private String title;
    private String description;
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
            String city,
            String country,
            String latitude,
            String longitude,
            LocalDate relevantDate,
            Long categoryId) {
        this.title = title;
        this.description = description;
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