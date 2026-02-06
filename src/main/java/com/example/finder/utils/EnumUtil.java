package com.example.finder.utils;

import com.example.finder.model.enums.AvailableAnnounceStatus;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.model.enums.AvailableInteractivityState;
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

    public AvailableInteractivityState interactivityStateMatcher(String interactivityState) {
        AvailableInteractivityState result = null;
        if (interactivityState != null) {
            for (AvailableInteractivityState status : AvailableInteractivityState.values()) {
                if (status.getDisplayName().equalsIgnoreCase(interactivityState)) {
                    result = status;
                    break;
                }
            }
            if (result == null) {
                System.out.println("Invalid interactivity state: " + interactivityState + "; defaulting to null.");
            }
        }
        return result;
    }

    public AvailableAnnounceStatus announceStatusMatcher(String announceStatus) {
        AvailableAnnounceStatus result = null;
        if (announceStatus != null) {
            for (AvailableAnnounceStatus status : AvailableAnnounceStatus.values()) {
                if (status.getDisplayName().equalsIgnoreCase(announceStatus)) {
                    result = status;
                    break;
                }
            }
            if (result == null) {
                System.out.println("Invalid announce status: " + announceStatus + "; defaulting to null.");
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
