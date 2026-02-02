package com.example.finder.utils;

import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.model.enums.AvailableRecordStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnumUtil {

    @Autowired
    public EnumUtil() {
    }

    public AvailableAnnounceTypes announceTypeMatcher(String type) {
        AvailableAnnounceTypes result = null;
        if (type != null) {
            try {
                result = AvailableAnnounceTypes.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid announce type filter: " + type + "; defaulting to null.");
                return result;
            }
        }
        return result;
    }

    public AvailableRecordStatus recordStatusMatcher(String recordStatus) {
        AvailableRecordStatus result = null;
        if (recordStatus != null) {
            for (AvailableRecordStatus status : AvailableRecordStatus.values()) {
                if (status.getDisplayName().equalsIgnoreCase(recordStatus)) {
                    result = status;
                    break;
                }
            }
            if (result == null) {
                System.out.println("Invalid record status: " + recordStatus + "; defaulting to null.");
            }
        }
        return result;
    }
}
