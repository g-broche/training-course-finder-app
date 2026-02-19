package com.example.finder.dto.output;

import com.example.finder.model.AppUser;

public class OtherUserDto {
    private String displayName;

    public OtherUserDto(AppUser user) {
        this.displayName = user.getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }
}
