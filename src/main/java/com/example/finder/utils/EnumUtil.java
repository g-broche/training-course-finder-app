package com.example.finder.utils;

import com.example.finder.model.enums.AvailableAnnounceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnumUtil {

    @Autowired
    public EnumUtil() {
    }

    public AvailableAnnounceTypes announceTypeMatcher(String type) {
        AvailableAnnounceTypes typeFilter = null;
        if (type != null) {
            try {
                typeFilter = AvailableAnnounceTypes.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid announce type filter: " + type + "; defaulting to null.");
                return typeFilter;
            }
        }
        return typeFilter;
    }
}
