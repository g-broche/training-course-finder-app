package com.example.finder.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void concat_ShouldJoinStringsWithSpace() {
        String result = StringUtil.concat("Hello", "World");
        assertEquals("Hello World", result);
    }

    @Test
    void concat_ShouldJoinMultipleStringsWithSpaces() {
        String result = StringUtil.concat("One", "Two", "Three", "Four");
        assertEquals("One Two Three Four", result);
    }

    @Test
    void concat_ShouldHandleSingleString() {
        String result = StringUtil.concat("Single");
        assertEquals("Single", result);
    }

    @Test
    void concat_ShouldHandleEmptyStrings() {
        String result = StringUtil.concat("", "", "");
        assertEquals("  ", result); // Two spaces between three empty strings
    }

    @Test
    void concatJoined_ShouldJoinStringsWithoutDelimiter() {
        String result = StringUtil.concatJoined("Hello", "World");
        assertEquals("HelloWorld", result);
    }

    @Test
    void concatJoined_ShouldJoinMultipleStrings() {
        String result = StringUtil.concatJoined("One", "Two", "Three");
        assertEquals("OneTwoThree", result);
    }

    @Test
    void concatJoined_ShouldBuildUrl() {
        String result = StringUtil.concatJoined("http://example.com", "/", "api", "/", "users");
        assertEquals("http://example.com/api/users", result);
    }

    @Test
    void concatWithDelimiter_ShouldJoinStringsWithCustomDelimiter() {
        String result = StringUtil.concatWithDelimiter("-", "Hello", "World");
        assertEquals("Hello-World", result);
    }

    @Test
    void concatWithDelimiter_ShouldJoinMultipleStringsWithCustomDelimiter() {
        String result = StringUtil.concatWithDelimiter(", ", "One", "Two", "Three");
        assertEquals("One, Two, Three", result);
    }

    @Test
    void concatWithDelimiter_ShouldJoinWithPipeDelimiter() {
        String result = StringUtil.concatWithDelimiter("|", "A", "B", "C");
        assertEquals("A|B|C", result);
    }

    @Test
    void toSlug_ShouldConvertToLowercase() {
        String result = StringUtil.toSlug("HELLO WORLD");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_ShouldReplaceSpacesWithHyphens() {
        String result = StringUtil.toSlug("Hello World");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_ShouldRemoveAccents() {
        String result = StringUtil.toSlug("Café Münchën");
        assertEquals("cafe-munchen", result);
    }

    @Test
    void toSlug_ShouldRemoveSpecialCharacters() {
        String result = StringUtil.toSlug("Hello @ World!");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_ShouldReplaceMultipleSpacesWithSingleHyphen() {
        String result = StringUtil.toSlug("Hello    World");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_ShouldTrimWhitespace() {
        String result = StringUtil.toSlug("  Hello World  ");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_ShouldHandleComplexString() {
        String result = StringUtil.toSlug("Éléphant & Café - 2024!");
        assertEquals("elephant-cafe-2024", result);
    }

    @Test
    void toSlug_ShouldHandleEmptyString() {
        String result = StringUtil.toSlug("");
        assertEquals("", result);
    }

    @Test
    void isDouble_ShouldReturnTrueForValidInteger() {
        assertTrue(StringUtil.isDouble("123"));
    }

    @Test
    void isDouble_ShouldReturnTrueForValidDecimal() {
        assertTrue(StringUtil.isDouble("123.456"));
    }

    @Test
    void isDouble_ShouldReturnTrueForNegativeNumber() {
        assertTrue(StringUtil.isDouble("-123.456"));
    }

    @Test
    void isDouble_ShouldReturnTrueForZero() {
        assertTrue(StringUtil.isDouble("0"));
    }

    @Test
    void isDouble_ShouldReturnTrueForDecimalZero() {
        assertTrue(StringUtil.isDouble("0.0"));
    }

    @Test
    void isDouble_ShouldReturnTrueForScientificNotation() {
        assertTrue(StringUtil.isDouble("1.23e-4"));
    }

    @Test
    void isDouble_ShouldReturnFalseForInvalidString() {
        assertFalse(StringUtil.isDouble("abc"));
    }

    @Test
    void isDouble_ShouldReturnFalseForNull() {
        assertFalse(StringUtil.isDouble(null));
    }

    @Test
    void isDouble_ShouldReturnFalseForEmptyString() {
        assertFalse(StringUtil.isDouble(""));
    }

    @Test
    void isDouble_ShouldReturnFalseForBlankString() {
        assertFalse(StringUtil.isDouble("   "));
    }

    @Test
    void isDouble_ShouldReturnFalseForMixedString() {
        assertFalse(StringUtil.isDouble("123abc"));
    }

    @Test
    void isDouble_ShouldReturnFalseForCommaDecimal() {
        assertFalse(StringUtil.isDouble("123,456"));
    }
}
