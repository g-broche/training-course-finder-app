package com.example.finder.utils;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.input.RequestMessage;
import com.example.finder.dto.input.RequestRegister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SanitizerUtilTest {

    private SanitizerUtil sanitizerUtil;

    @BeforeEach
    void setUp() {
        sanitizerUtil = new SanitizerUtil();
    }

    @Test
    void sanitizeForHtml_ShouldEscapeHtmlTags() {
        String input = "<script>alert('XSS')</script>";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("&lt;script&gt;alert('XSS')&lt;/script&gt;", result);
    }

    @Test
    void sanitizeForHtml_ShouldEscapeAmpersand() {
        String input = "Tom & Jerry";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("Tom &amp; Jerry", result);
    }

    @Test
    void sanitizeForHtml_ShouldEscapeQuotes() {
        String input = "Say \"Hello\"";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("Say &quot;Hello&quot;", result);
    }

    @Test
    void sanitizeForHtml_ShouldEscapeLessThanAndGreaterThan() {
        String input = "5 < 10 > 3";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("5 &lt; 10 &gt; 3", result);
    }

    @Test
    void sanitizeForHtml_ShouldTrimWhitespace() {
        String input = "  Hello World  ";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("Hello World", result);
    }

    @Test
    void sanitizeForHtml_ShouldHandleEmptyString() {
        String input = "";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("", result);
    }

    @Test
    void sanitizeForHtml_ShouldHandleStringWithOnlyWhitespace() {
        String input = "   ";
        String result = sanitizerUtil.sanitizeForHtml(input);
        assertEquals("", result);
    }

    @Test
    void sanitizeRegisterInputs_ShouldSanitizeAllFields() {
        RequestRegister input = new RequestRegister(
                "<b>John</b>  ",
                "  <script>Doe</script>",
                "  JD123  ",
                "  john@example.com  ",
                "  password123  ",
                true);

        RequestRegister result = sanitizerUtil.sanitizeRegisterInputs(input);

        assertEquals("&lt;b&gt;John&lt;/b&gt;", result.getFirstName());
        assertEquals("&lt;script&gt;Doe&lt;/script&gt;", result.getLastName());
        assertEquals("JD123", result.getDisplayName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        assertTrue(result.getHasAcceptedGdpr());
    }

    @Test
    void sanitizeRegisterInputs_ShouldTrimAllStringFields() {
        RequestRegister input = new RequestRegister(
                "  John  ",
                "  Doe  ",
                "  JohnD  ",
                "  john@example.com  ",
                "  password123  ",
                false);

        RequestRegister result = sanitizerUtil.sanitizeRegisterInputs(input);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("JohnD", result.getDisplayName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("password123", result.getPassword());
        assertFalse(result.getHasAcceptedGdpr());
    }

    @Test
    void sanitizeAnnounceInputs_ShouldSanitizeAllFields() {
        RequestAnnounce input = new RequestAnnounce(
                "<h1>Lost Dog</h1>  ",
                "  <script>alert('test')</script>Description  ",
                "  Paris  ",
                "  France  ",
                "  48.8566  ",
                "  2.3522  ",
                LocalDate.of(2024, 1, 1),
                1L);

        RequestAnnounce result = sanitizerUtil.sanitizeAnnounceInputs(input);

        assertEquals("&lt;h1&gt;Lost Dog&lt;/h1&gt;", result.getTitle());
        assertTrue(result.getDescription().contains("&lt;script&gt;"));
        assertEquals("Paris", result.getCity());
        assertEquals("France", result.getCountry());
        assertEquals("48.8566", result.getLatitude());
        assertEquals("2.3522", result.getLongitude());
        assertEquals(LocalDate.of(2024, 1, 1), result.getRelevantDate());
        assertEquals(1L, result.getCategoryId());
    }

    @Test
    void sanitizeDiscussionInputs_ShouldSanitizeMessage() {
        RequestDiscussion input = new RequestDiscussion(
                "  <b>Hello World</b>  ");

        RequestDiscussion result = sanitizerUtil.sanitizeDiscussionInputs(input);

        assertEquals("&lt;b&gt;Hello World&lt;/b&gt;", result.getMessage());
    }

    @Test
    void sanitizeDiscussionInputs_ShouldTrimMessage() {
        RequestDiscussion input = new RequestDiscussion(
                "  Hello World  ");

        RequestDiscussion result = sanitizerUtil.sanitizeDiscussionInputs(input);

        assertEquals("Hello World", result.getMessage());
    }

    @Test
    void sanitizeMessageInputs_ShouldSanitizeMessage() {
        RequestMessage input = new RequestMessage(
                "  <script>alert('XSS')</script>  ");

        RequestDiscussion result = sanitizerUtil.sanitizeMessageInputs(input);

        assertEquals("&lt;script&gt;alert('XSS')&lt;/script&gt;", result.getMessage());
    }

    @Test
    void sanitizeMessageInputs_ShouldTrimMessage() {
        RequestMessage input = new RequestMessage(
                "  Hello  ");

        RequestDiscussion result = sanitizerUtil.sanitizeMessageInputs(input);

        assertEquals("Hello", result.getMessage());
    }
}
