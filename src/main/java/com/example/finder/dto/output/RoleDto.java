package com.example.finder.dto.output;

import com.example.finder.model.Role;

public class RoleDto {
    private String name;

    public RoleDto(Role role) {
        this.name = role.getName();
    }

    public String getName() {
        return name;
    }
}
