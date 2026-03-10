package com.example.finder.utils.validator;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.output.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorAnnounceTest {

    private ValidatorAnnounce validator;

    @BeforeEach
    void setUp() {
        validator = new ValidatorAnnounce();
    }

    // Tests for isValidTitle

    @Test
    void isValidTitle_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidTitle(null));
    }

    @Test
    void isValidTitle_ShouldReturnTrueForValidTitle() {
        assertTrue(validator.isValidTitle("Lost Dog"));
    }

    @Test
    void isValidTitle_ShouldReturnTrueForMinimumLengthTitle() {
        assertTrue(validator.isValidTitle("12345"));
    }

    @Test
    void isValidTitle_ShouldReturnTrueForMaximumLengthTitle() {
        String maxTitle = "a".repeat(50);
        assertTrue(validator.isValidTitle(maxTitle));
    }

    @Test
    void isValidTitle_ShouldReturnFalseForTooShortTitle() {
        assertFalse(validator.isValidTitle("1234"));
    }

    @Test
    void isValidTitle_ShouldReturnFalseForTooLongTitle() {
        String tooLongTitle = "a".repeat(51);
        assertFalse(validator.isValidTitle(tooLongTitle));
    }

    @Test
    void isValidTitle_ShouldTrimWhitespace() {
        assertTrue(validator.isValidTitle("  Lost Dog  "));
    }

    // Tests for isValidDescription
    @Test
    void isValidDescription_ShouldReturnTrueForValidDescription() {
        String validDescription = "This is a detailed description of the lost item.";
        assertTrue(validator.isValidDescription(validDescription));
    }

    @Test
    void isValidDescription_ShouldReturnTrueForMinimumLengthDescription() {
        String minDescription = "a".repeat(30);
        assertTrue(validator.isValidDescription(minDescription));
    }

    @Test
    void isValidDescription_ShouldReturnTrueForMaximumLengthDescription() {
        String maxDescription = "a".repeat(1000);
        assertTrue(validator.isValidDescription(maxDescription));
    }

    @Test
    void isValidDescription_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidDescription(null));
    }

    @Test
    void isValidDescription_ShouldReturnFalseForTooShortDescription() {
        String tooShortDescription = "a".repeat(29);
        assertFalse(validator.isValidDescription(tooShortDescription));
    }

    @Test
    void isValidDescription_ShouldReturnFalseForTooLongDescription() {
        String tooLongDescription = "a".repeat(1001);
        assertFalse(validator.isValidDescription(tooLongDescription));
    }

    // Tests for isValidCity
    @Test
    void isValidCity_ShouldReturnTrueForValidCity() {
        assertTrue(validator.isValidCity("Paris"));
    }

    @Test
    void isValidCity_ShouldReturnTrueForMinimumLengthCity() {
        assertTrue(validator.isValidCity("A"));
    }

    @Test
    void isValidCity_ShouldReturnTrueForMaximumLengthCity() {
        String maxCity = "a".repeat(100);
        assertTrue(validator.isValidCity(maxCity));
    }

    @Test
    void isValidCity_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidCity(null));
    }

    @Test
    void isValidCity_ShouldReturnFalseForEmptyString() {
        assertFalse(validator.isValidCity(""));
    }

    @Test
    void isValidCity_ShouldReturnFalseForTooLongCity() {
        String tooLongCity = "a".repeat(101);
        assertFalse(validator.isValidCity(tooLongCity));
    }

    // Tests for isValidCountry
    @Test
    void isValidCountry_ShouldReturnTrueForValidCountry() {
        assertTrue(validator.isValidCountry("France"));
    }

    @Test
    void isValidCountry_ShouldReturnTrueForMinimumLengthCountry() {
        assertTrue(validator.isValidCountry("A"));
    }

    @Test
    void isValidCountry_ShouldReturnTrueForMaximumLengthCountry() {
        String maxCountry = "a".repeat(50);
        assertTrue(validator.isValidCountry(maxCountry));
    }

    @Test
    void isValidCountry_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidCountry(null));
    }

    @Test
    void isValidCountry_ShouldReturnFalseForTooLongCountry() {
        String tooLongCountry = "a".repeat(51);
        assertFalse(validator.isValidCountry(tooLongCountry));
    }

    // Tests for isValidLatitude
    @Test
    void isValidLatitude_ShouldReturnTrueForValidLatitude() {
        assertTrue(validator.isValidLatitude("48.8566"));
    }

    @Test
    void isValidLatitude_ShouldReturnTrueForNegativeLatitude() {
        assertTrue(validator.isValidLatitude("-33.8688"));
    }

    @Test
    void isValidLatitude_ShouldReturnTrueForZero() {
        assertTrue(validator.isValidLatitude("0.0"));
    }

    @Test
    void isValidLatitude_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidLatitude(null));
    }

    @Test
    void isValidLatitude_ShouldReturnFalseForNonNumeric() {
        assertFalse(validator.isValidLatitude("abc"));
    }

    @Test
    void isValidLatitude_ShouldReturnFalseForTooLong() {
        String tooLong = "1".repeat(31);
        assertFalse(validator.isValidLatitude(tooLong));
    }

    @Test
    void isValidLatitude_ShouldAcceptMaxLength() {
        String maxLength = "1".repeat(30);
        assertTrue(validator.isValidLatitude(maxLength));
    }

    // Tests for isValidLongitude
    @Test
    void isValidLongitude_ShouldReturnTrueForValidLongitude() {
        assertTrue(validator.isValidLongitude("2.3522"));
    }

    @Test
    void isValidLongitude_ShouldReturnTrueForNegativeLongitude() {
        assertTrue(validator.isValidLongitude("-151.2093"));
    }

    @Test
    void isValidLongitude_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidLongitude(null));
    }

    @Test
    void isValidLongitude_ShouldReturnFalseForNonNumeric() {
        assertFalse(validator.isValidLongitude("xyz"));
    }

    @Test
    void isValidLongitude_ShouldReturnFalseForTooLong() {
        String tooLong = "1".repeat(31);
        assertFalse(validator.isValidLongitude(tooLong));
    }

    // Tests for isValidDate
    @Test
    void isValidDate_ShouldReturnTrueForPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(10);
        assertTrue(validator.isValidDate(pastDate));
    }

    @Test
    void isValidDate_ShouldReturnTrueForToday() {
        LocalDate today = LocalDate.now();
        assertTrue(validator.isValidDate(today));
    }

    @Test
    void isValidDate_ShouldReturnFalseForFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertFalse(validator.isValidDate(futureDate));
    }

    @Test
    void isValidDate_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidDate(null));
    }

    // Tests for validateAnnounceInputs
    @Test
    void validateAnnounceInputs_ShouldReturnEmptyListForValidInputs() {
        RequestAnnounce validRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidTitle() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost", // Too short
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("title", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidDescription() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "Too short", // Less than 30 characters
                "New York",
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("description", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidCity() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "", // Empty city
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("city", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidCountry() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "", // Empty country
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("country", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidLatitude() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "not-a-number", // Invalid latitude
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("latitude", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForInvalidLongitude() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "40.7829",
                "not-a-number", // Invalid longitude
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("longitude", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnErrorForFutureDate() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost Dog in Central Park",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().plusDays(1), // Future date
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("relevant date", errors.get(0).getErrorName());
    }

    @Test
    void validateAnnounceInputs_ShouldReturnMultipleErrorsForMultipleInvalidFields() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost", // Too short
                "Short", // Too short
                "", // Empty
                "", // Empty
                "abc", // Not a number
                "xyz", // Not a number
                LocalDate.now().plusDays(1), // Future
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertEquals(7, errors.size());
    }

    @Test
    void validateAnnounceInputs_ErrorsShouldHaveMessages() {
        RequestAnnounce invalidRequest = new RequestAnnounce(
                "Lost",
                "A golden retriever was lost in Central Park on Monday evening.",
                "New York",
                "USA",
                "40.7829",
                "-73.9654",
                LocalDate.now().minusDays(1),
                1L);

        List<ErrorDto> errors = validator.validateAnnounceInputs(invalidRequest);

        assertFalse(errors.get(0).getErrorMessage().isEmpty());
        assertTrue(errors.get(0).getErrorMessage().contains("between"));
    }
}
