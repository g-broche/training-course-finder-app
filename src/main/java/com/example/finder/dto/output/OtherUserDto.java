package com.example.finder.dto.output;

import com.example.finder.model.AppUser;
import com.example.finder.model.Role;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public class OtherUserDto {
    private String displayName;

    public OtherUserDto(AppUser user){
        this.displayName = user.getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }
}
