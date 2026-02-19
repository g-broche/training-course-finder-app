package com.example.finder.utils.validator;

import com.example.finder.dto.input.RequestRegister;
import com.example.finder.dto.output.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorUserTest {

    private ValidatorUser validatorUser;

    @BeforeEach
    void setUp() {
        validatorUser = new ValidatorUser();
    }

    // Tests for isValidFirstName
    @Test
    void isValidFirstName_ShouldReturnTrueForValidName() {
        assertTrue(validatorUser.isValidFirstName("John"));
    }

    @Test
    void isValidFirstName_ShouldReturnTrueForNameWithAccents() {
        assertTrue(validatorUser.isValidFirstName("José"));
    }

    @Test
    void isValidFirstName_ShouldReturnTrueForNameWithHyphen() {
        assertTrue(validatorUser.isValidFirstName("Marie-Claire"));
    }

    @Test
    void isValidFirstName_ShouldReturnTrueForNameWithApostrophe() {
        assertTrue(validatorUser.isValidFirstName("O'Brien"));
    }

    @Test
    void isValidFirstName_ShouldReturnTrueForNameWithSpace() {
        assertTrue(validatorUser.isValidFirstName("Jean Paul"));
    }

    @Test
    void isValidFirstName_ShouldReturnFalseForNull() {
        assertFalse(validatorUser.isValidFirstName(null));
    }

    @Test
    void isValidFirstName_ShouldReturnFalseForEmptyString() {
        assertFalse(validatorUser.isValidFirstName(""));
    }

    @Test
    void isValidFirstName_ShouldReturnFalseForTooLongName() {
        String longName = "a".repeat(31);
        assertFalse(validatorUser.isValidFirstName(longName));
    }

    @Test
    void isValidFirstName_ShouldAcceptMaxLengthName() {
        String maxName = "a".repeat(30);
        assertTrue(validatorUser.isValidFirstName(maxName));
    }

    @Test
    void isValidFirstName_ShouldTrimWhitespace() {
        assertTrue(validatorUser.isValidFirstName("  John  "));
    }

    @Test
    void isValidFirstName_ShouldReturnFalseForNameWithNumbers() {
        assertFalse(validatorUser.isValidFirstName("John123"));
    }

    // Tests for isValidLastName
    @Test
    void isValidLastName_ShouldReturnTrueForValidName() {
        assertTrue(validatorUser.isValidLastName("Smith"));
    }

    @Test
    void isValidLastName_ShouldReturnFalseForNull() {
        assertFalse(validatorUser.isValidLastName(null));
    }

    @Test
    void isValidLastName_ShouldReturnFalseForTooLongName() {
        String longName = "a".repeat(31);
        assertFalse(validatorUser.isValidLastName(longName));
    }

    // Tests for isValidDisplayName
    @Test
    void isValidDisplayName_ShouldReturnTrueForValidName() {
        assertTrue(validatorUser.isValidDisplayName("Johnny"));
    }

    @Test
    void isValidDisplayName_ShouldReturnTrueForAlphanumeric() {
        assertTrue(validatorUser.isValidDisplayName("User"));
    }

    @Test
    void isValidDisplayName_ShouldReturnFalseForNull() {
        assertFalse(validatorUser.isValidDisplayName(null));
    }

    // Tests for isValidEmail
    @Test
    void isValidEmail_ShouldReturnTrueForValidEmail() {
        assertTrue(validatorUser.isValidEmail("user@example.com"));
    }

    @Test
    void isValidEmail_ShouldReturnTrueForEmailWithDots() {
        assertTrue(validatorUser.isValidEmail("user.name@example.com"));
    }

    @Test
    void isValidEmail_ShouldReturnTrueForEmailWithHyphens() {
        assertTrue(validatorUser.isValidEmail("user-name@example.com"));
    }

    @Test
    void isValidEmail_ShouldReturnTrueForEmailWithUnderscore() {
        assertTrue(validatorUser.isValidEmail("user_name@example.com"));
    }

    @Test
    void isValidEmail_ShouldReturnTrueForSubdomain() {
        assertTrue(validatorUser.isValidEmail("user@mail.example.com"));
    }

    @Test
    void isValidEmail_ShouldReturnFalseForNull() {
        assertFalse(validatorUser.isValidEmail(null));
    }

    @Test
    void isValidEmail_ShouldReturnFalseForEmptyString() {
        assertFalse(validatorUser.isValidEmail(""));
    }

    @Test
    void isValidEmail_ShouldReturnFalseForMissingAtSign() {
        assertFalse(validatorUser.isValidEmail("userexample.com"));
    }

    @Test
    void isValidEmail_ShouldReturnFalseForMissingDomain() {
        assertFalse(validatorUser.isValidEmail("user@"));
    }

    @Test
    void isValidEmail_ShouldReturnFalseForMissingTopLevelDomain() {
        assertFalse(validatorUser.isValidEmail("user@example"));
    }

    @Test
    void isValidEmail_ShouldTrimWhitespace() {
        assertTrue(validatorUser.isValidEmail("  user@example.com  "));
    }

    // Tests for isValidPassword
    @Test
    void isValidPassword_ShouldReturnTrueForValidPassword() {
        assertTrue(validatorUser.isValidPassword("password123"));
    }

    @Test
    void isValidPassword_ShouldReturnTrueForMinimumLengthPassword() {
        assertTrue(validatorUser.isValidPassword("12345678"));
    }

    @Test
    void isValidPassword_ShouldReturnFalseForNull() {
        assertFalse(validatorUser.isValidPassword(null));
    }

    @Test
    void isValidPassword_ShouldReturnFalseForTooShortPassword() {
        assertFalse(validatorUser.isValidPassword("1234567"));
    }

    @Test
    void isValidPassword_ShouldReturnFalseForEmptyString() {
        assertFalse(validatorUser.isValidPassword(""));
    }

    @Test
    void isValidPassword_ShouldTrimWhitespace() {
        assertTrue(validatorUser.isValidPassword("  password123  "));
    }

    @Test
    void isValidPassword_ShouldReturnTrueForLongPassword() {
        String longPassword = "a".repeat(100);
        assertTrue(validatorUser.isValidPassword(longPassword));
    }

    // Tests for validateRegisterInputs
    @Test
    void validateRegisterInputs_ShouldReturnEmptyListForValidInputs() {
        RequestRegister validRequest = new RequestRegister(
                "John",
                "Doe",
                "JohnDoe",
                "john@example.com",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateRegisterInputs_ShouldReturnErrorForInvalidFirstName() {
        RequestRegister invalidRequest = new RequestRegister(
                "",
                "Doe",
                "JohnDoe",
                "john@example.com",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("firstName", errors.get(0).getErrorName());
    }

    @Test
    void validateRegisterInputs_ShouldReturnErrorForInvalidLastName() {
        RequestRegister invalidRequest = new RequestRegister(
                "John",
                "",
                "JohnDoe",
                "john@example.com",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("lastName", errors.get(0).getErrorName());
    }

    @Test
    void validateRegisterInputs_ShouldReturnErrorForInvalidDisplayName() {
        RequestRegister invalidRequest = new RequestRegister(
                "John",
                "Doe",
                "",
                "john@example.com",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("displayName", errors.get(0).getErrorName());
    }

    @Test
    void validateRegisterInputs_ShouldReturnErrorForInvalidEmail() {
        RequestRegister invalidRequest = new RequestRegister(
                "John",
                "Doe",
                "JohnDoe",
                "invalid-email",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("email", errors.get(0).getErrorName());
    }

    @Test
    void validateRegisterInputs_ShouldReturnErrorForInvalidPassword() {
        RequestRegister invalidRequest = new RequestRegister(
                "John",
                "Doe",
                "JohnDoe",
                "john@example.com",
                "short",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(1, errors.size());
        assertEquals("password", errors.get(0).getErrorName());
    }

    @Test
    void validateRegisterInputs_ShouldReturnMultipleErrorsForMultipleInvalidFields() {
        RequestRegister invalidRequest = new RequestRegister(
                "",
                "",
                "",
                "invalid-email",
                "short",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertEquals(5, errors.size());
    }

    @Test
    void validateRegisterInputs_ShouldIncludeErrorMessages() {
        RequestRegister invalidRequest = new RequestRegister(
                "",
                "Doe",
                "JohnDoe",
                "john@example.com",
                "password123",
                true);

        List<ErrorDto> errors = validatorUser.validateRegisterInputs(invalidRequest);

        assertFalse(errors.get(0).getErrorMessage().isEmpty());
        assertTrue(errors.get(0).getErrorMessage().contains("between"));
    }
}
