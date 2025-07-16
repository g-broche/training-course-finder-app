package com.example.finder.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Role {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    public Role() {}

    public Role(String name) {
        this.setName(name);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}