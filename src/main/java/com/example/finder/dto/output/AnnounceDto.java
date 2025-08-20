package com.example.finder.dto.output;

import com.example.finder.model.*;
import com.example.finder.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDate;

public class AnnounceDto {

    private String id;
    private String title;
    private String description;
    private String photo;
    private String city;
    private String country;
    private String latitude;
    private String longitude;
    private LocalDate relevantDate;
    private String type;
    private OtherUserDto author;
    private String interactivityState;
    private String status;
    private String category;
    private Timestamp createdAt;
    private Timestamp editedAt;

    public AnnounceDto(Announce announce, String basePhotoPath){
        this.id = announce.getId().toString();
        this.title = announce.getTitle();
        this.description = announce.getDescription();
        this.photo = getPhotoPathIfExists(announce, basePhotoPath);
        this.city = announce.getCity();
        this.country = announce.getCountry();
        this.latitude = announce.getLatitude();
        this.longitude = announce.getLongitude();
        this.relevantDate = announce.getRelevantDate();
        this.type = announce.getType().getName();
        this.author = announce.getAuthor().toOtherUserDto();
        this.interactivityState = announce.getInteractivityState().getName();
        this.status = announce.getStatus().getName();
        this.category = announce.getCategory().getName();
        this.createdAt = announce.getCreatedAt();
        this.editedAt = announce.getEditedAt();
    }

    private String getPhotoPathIfExists(Announce announce, String basePhotoPath){
        return announce.getPhoto() != null && !announce.getPhoto().isEmpty()
                ? basePhotoPath+announce.getPhoto()
                : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OtherUserDto getAuthor() {
        return author;
    }

    public void setAuthor(OtherUserDto author) {
        this.author = author;
    }

    public String getInteractivityState() {
        return interactivityState;
    }

    public void setInteractivityState(String interactivityState) {
        this.interactivityState = interactivityState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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
