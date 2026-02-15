package com.example.finder.dto.output;

import com.example.finder.model.UserStatus;

public class UserStatusDto {
    private String name;

    public UserStatusDto(UserStatus userStatus) {
        this.name = userStatus.getName();
    }

    public String getName() {
        return name;
    }
}
