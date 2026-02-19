package com.example.finder.utils;

import com.example.finder.model.enums.AvailableAnnounceStatus;
import com.example.finder.model.enums.AvailableAnnounceTypes;
import com.example.finder.model.enums.AvailableInteractivityState;
import com.example.finder.model.enums.AvailableRecordStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilTest {

    private EnumUtil enumUtil;

    @BeforeEach
    void setUp() {
        enumUtil = new EnumUtil();
    }

    // Tests for announceTypeMatcher
    @Test
    void announceTypeMatcher_ShouldReturnNullForNullInput() {
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher(null);
        assertNull(result);
    }

    @Test
    void announceTypeMatcher_ShouldMatchUppercaseInput() {
        // Assuming LOST and FOUND are valid enum values
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher("LOST");
        assertNotNull(result);
        assertEquals(AvailableAnnounceTypes.LOST, result);
    }

    @Test
    void announceTypeMatcher_ShouldMatchLowercaseInput() {
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher("lost");
        assertNotNull(result);
        assertEquals(AvailableAnnounceTypes.LOST, result);
    }

    @Test
    void announceTypeMatcher_ShouldMatchMixedCaseInput() {
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher("LosT");
        assertNotNull(result);
        assertEquals(AvailableAnnounceTypes.LOST, result);
    }

    @Test
    void announceTypeMatcher_ShouldReturnNullForInvalidInput() {
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher("INVALID_TYPE");
        assertNull(result);
    }

    @Test
    void announceTypeMatcher_ShouldMatchFoundType() {
        AvailableAnnounceTypes result = enumUtil.announceTypeMatcher("FOUND");
        assertNotNull(result);
        assertEquals(AvailableAnnounceTypes.FOUND, result);
    }

    // Tests for interactivityStateMatcher
    @Test
    void interactivityStateMatcher_ShouldReturnNullForNullInput() {
        AvailableInteractivityState result = enumUtil.interactivityStateMatcher(null);
        assertNull(result);
    }

    @Test
    void interactivityStateMatcher_ShouldMatchValidDisplayName() {
        // Test with actual display names from the enum
        for (AvailableInteractivityState state : AvailableInteractivityState.values()) {
            AvailableInteractivityState result = enumUtil.interactivityStateMatcher(state.getDisplayName());
            assertNotNull(result, "Should match display name: " + state.getDisplayName());
            assertEquals(state, result);
        }
    }

    @Test
    void interactivityStateMatcher_ShouldBeCaseInsensitive() {
        AvailableInteractivityState firstState = AvailableInteractivityState.values()[0];
        String displayName = firstState.getDisplayName();

        AvailableInteractivityState result1 = enumUtil.interactivityStateMatcher(displayName.toLowerCase());
        AvailableInteractivityState result2 = enumUtil.interactivityStateMatcher(displayName.toUpperCase());

        assertEquals(firstState, result1);
        assertEquals(firstState, result2);
    }

    @Test
    void interactivityStateMatcher_ShouldReturnNullForInvalidInput() {
        AvailableInteractivityState result = enumUtil.interactivityStateMatcher("INVALID_STATE");
        assertNull(result);
    }

    // Tests for announceStatusMatcher
    @Test
    void announceStatusMatcher_ShouldReturnNullForNullInput() {
        AvailableAnnounceStatus result = enumUtil.announceStatusMatcher(null);
        assertNull(result);
    }

    @Test
    void announceStatusMatcher_ShouldMatchValidDisplayName() {
        // Test with actual display names from the enum
        for (AvailableAnnounceStatus status : AvailableAnnounceStatus.values()) {
            AvailableAnnounceStatus result = enumUtil.announceStatusMatcher(status.getDisplayName());
            assertNotNull(result, "Should match display name: " + status.getDisplayName());
            assertEquals(status, result);
        }
    }

    @Test
    void announceStatusMatcher_ShouldBeCaseInsensitive() {
        AvailableAnnounceStatus firstStatus = AvailableAnnounceStatus.values()[0];
        String displayName = firstStatus.getDisplayName();

        AvailableAnnounceStatus result1 = enumUtil.announceStatusMatcher(displayName.toLowerCase());
        AvailableAnnounceStatus result2 = enumUtil.announceStatusMatcher(displayName.toUpperCase());

        assertEquals(firstStatus, result1);
        assertEquals(firstStatus, result2);
    }

    @Test
    void announceStatusMatcher_ShouldReturnNullForInvalidInput() {
        AvailableAnnounceStatus result = enumUtil.announceStatusMatcher("INVALID_STATUS");
        assertNull(result);
    }

    // Tests for recordStatusMatcher
    @Test
    void recordStatusMatcher_ShouldReturnNullForNullInput() {
        AvailableRecordStatus result = enumUtil.recordStatusMatcher(null);
        assertNull(result);
    }

    @Test
    void recordStatusMatcher_ShouldMatchValidDisplayName() {
        // Test with actual display names from the enum
        for (AvailableRecordStatus status : AvailableRecordStatus.values()) {
            AvailableRecordStatus result = enumUtil.recordStatusMatcher(status.getDisplayName());
            assertNotNull(result, "Should match display name: " + status.getDisplayName());
            assertEquals(status, result);
        }
    }

    @Test
    void recordStatusMatcher_ShouldBeCaseInsensitive() {
        AvailableRecordStatus firstStatus = AvailableRecordStatus.values()[0];
        String displayName = firstStatus.getDisplayName();

        AvailableRecordStatus result1 = enumUtil.recordStatusMatcher(displayName.toLowerCase());
        AvailableRecordStatus result2 = enumUtil.recordStatusMatcher(displayName.toUpperCase());

        assertEquals(firstStatus, result1);
        assertEquals(firstStatus, result2);
    }

    @Test
    void recordStatusMatcher_ShouldReturnNullForInvalidInput() {
        AvailableRecordStatus result = enumUtil.recordStatusMatcher("INVALID_RECORD_STATUS");
        assertNull(result);
    }

    @Test
    void recordStatusMatcher_ShouldHandleEmptyString() {
        AvailableRecordStatus result = enumUtil.recordStatusMatcher("");
        assertNull(result);
    }
}
