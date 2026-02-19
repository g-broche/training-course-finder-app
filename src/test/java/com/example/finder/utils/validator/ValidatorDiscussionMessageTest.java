package com.example.finder.utils.validator;

import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.output.ErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorDiscussionMessageTest {

    private ValidatorDiscussionMessage validator;

    @BeforeEach
    void setUp() {
        validator = new ValidatorDiscussionMessage();
    }

    // Tests for isValidMessage
    @Test
    void isValidMessage_ShouldReturnTrueForValidMessage() {
        assertTrue(validator.isValidMessage("This is a valid message"));
    }

    @Test
    void isValidMessage_ShouldReturnTrueForMinimumLengthMessage() {
        assertTrue(validator.isValidMessage("A"));
    }

    @Test
    void isValidMessage_ShouldReturnTrueForMaximumLengthMessage() {
        String maxMessage = "a".repeat(500);
        assertTrue(validator.isValidMessage(maxMessage));
    }

    @Test
    void isValidMessage_ShouldReturnFalseForNull() {
        assertFalse(validator.isValidMessage(null));
    }

    @Test
    void isValidMessage_ShouldReturnFalseForEmptyString() {
        assertFalse(validator.isValidMessage(""));
    }

    @Test
    void isValidMessage_ShouldReturnFalseForTooLongMessage() {
        String tooLongMessage = "a".repeat(501);
        assertFalse(validator.isValidMessage(tooLongMessage));
    }

    @Test
    void isValidMessage_ShouldTrimWhitespace() {
        assertTrue(validator.isValidMessage("  Valid message  "));
    }

    @Test
    void isValidMessage_ShouldReturnFalseForOnlyWhitespace() {
        assertFalse(validator.isValidMessage("   "));
    }

    @Test
    void isValidMessage_ShouldAcceptMessageWithSpecialCharacters() {
        assertTrue(validator.isValidMessage("Hello! How are you? @user #topic"));
    }

    @Test
    void isValidMessage_ShouldAcceptMessageWithNumbers() {
        assertTrue(validator.isValidMessage("Meeting at 3pm on floor 5"));
    }

    @Test
    void isValidMessage_ShouldAcceptMessageWithNewlines() {
        assertTrue(validator.isValidMessage("Line 1\nLine 2\nLine 3"));
    }

    @Test
    void isValidMessage_ShouldAcceptMessageWithEmojis() {
        assertTrue(validator.isValidMessage("Great! 👍"));
    }

    @Test
    void isValidMessage_ShouldAcceptMultilingualMessage() {
        assertTrue(validator.isValidMessage("Hello, Bonjour, Hola, 你好"));
    }

    // Tests for validateDiscussionMessage
    @Test
    void validateDiscussionMessage_ShouldReturnEmptyListForValidMessage() {
        RequestDiscussion validRequest = new RequestDiscussion("This is a valid message");

        List<ErrorDto> errors = validator.validateDiscussionMessage(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateDiscussionMessage_ShouldReturnErrorForInvalidMessage() {
        RequestDiscussion invalidRequest = new RequestDiscussion("");

        List<ErrorDto> errors = validator.validateDiscussionMessage(invalidRequest);

        assertEquals(1, errors.size());
    }

    @Test
    void validateDiscussionMessage_ShouldReturnErrorForTooLongMessage() {
        String tooLongMessage = "a".repeat(501);
        RequestDiscussion invalidRequest = new RequestDiscussion(tooLongMessage);

        List<ErrorDto> errors = validator.validateDiscussionMessage(invalidRequest);

        assertEquals(1, errors.size());
    }

    @Test
    void validateDiscussionMessage_ErrorShouldHaveCorrectField() {
        RequestDiscussion invalidRequest = new RequestDiscussion("");

        List<ErrorDto> errors = validator.validateDiscussionMessage(invalidRequest);

        assertEquals("title", errors.get(0).getErrorName());
    }

    @Test
    void validateDiscussionMessage_ErrorMessageShouldContainConstraints() {
        RequestDiscussion invalidRequest = new RequestDiscussion("");

        List<ErrorDto> errors = validator.validateDiscussionMessage(invalidRequest);

        String errorMessage = errors.get(0).getErrorMessage();
        assertTrue(errorMessage.contains("between"));
        assertTrue(errorMessage.contains("1"));
        assertTrue(errorMessage.contains("500"));
    }

    @Test
    void validateDiscussionMessage_ShouldAcceptMaxLengthMessage() {
        String maxMessage = "a".repeat(500);
        RequestDiscussion validRequest = new RequestDiscussion(maxMessage);

        List<ErrorDto> errors = validator.validateDiscussionMessage(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateDiscussionMessage_ShouldAcceptMinLengthMessage() {
        RequestDiscussion validRequest = new RequestDiscussion("A");

        List<ErrorDto> errors = validator.validateDiscussionMessage(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateDiscussionMessage_ShouldAcceptMessageWithWhitespace() {
        RequestDiscussion validRequest = new RequestDiscussion("Hello World");

        List<ErrorDto> errors = validator.validateDiscussionMessage(validRequest);

        assertTrue(errors.isEmpty());
    }

    @Test
    void isValidMessage_ShouldReturnTrueForMessageWith499Characters() {
        String message = "a".repeat(499);
        assertTrue(validator.isValidMessage(message));
    }

    @Test
    void isValidMessage_ShouldReturnTrueForMessageWith250Characters() {
        String message = "a".repeat(250);
        assertTrue(validator.isValidMessage(message));
    }

    @Test
    void isValidMessage_ShouldReturnTrueForMessageWithMixedContent() {
        String message = "This is a message with numbers 123, special chars !@#, and UPPERCASE.";
        assertTrue(validator.isValidMessage(message));
    }

    @Test
    void validateDiscussionMessage_ShouldReturnErrorForNullMessage() {
        RequestDiscussion invalidRequest = new RequestDiscussion(null);

        List<ErrorDto> errors = validator.validateDiscussionMessage(invalidRequest);

        assertEquals(1, errors.size());
    }
}
