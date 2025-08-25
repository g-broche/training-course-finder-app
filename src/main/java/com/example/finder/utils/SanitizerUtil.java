package com.example.finder.utils;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.input.RequestRegister;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SanitizerUtil {

    /**
     * escapes html sensible characters from a string to prevent code injection
     * @param input
     * @return escaped string
     */
    public String sanitizeForHtml(String input){
        return StringEscapeUtils.escapeHtml4(input.trim());
    }

    /**
     * Given a RequestRegister instance, will return a newly made RequestRegister
     * formed from the sanitization of the given instance's properties
     * @param inputs
     * @return sanitized RequestRegister
     */
    public RequestRegister sanitizeRegisterInputs(RequestRegister inputs){
        return new RequestRegister(
                sanitizeForHtml(inputs.getFirstName().trim()),
                sanitizeForHtml(inputs.getLastName().trim()),
                sanitizeForHtml(inputs.getDisplayName().trim()),
                inputs.getEmail().trim(),
                inputs.getPassword().trim(),
                inputs.getHasAcceptedGdpr()
        );
    }

    public RequestAnnounce sanitizeAnnounceInputs(RequestAnnounce inputs){
        return new RequestAnnounce(
                sanitizeForHtml(inputs.getTitle().trim()),
                sanitizeForHtml(inputs.getDescription().trim()),
                sanitizeForHtml(inputs.getCity().trim()),
                sanitizeForHtml(inputs.getCountry().trim()),
                sanitizeForHtml(inputs.getLatitude().trim()),
                sanitizeForHtml(inputs.getLongitude().trim()),
                inputs.getRelevantDate(),
                inputs.getCategoryId()
        );
    }

    public RequestDiscussion sanitizeDiscussionInputs(RequestDiscussion inputs){
        return new RequestDiscussion(
                sanitizeForHtml(inputs.getMessage().trim())
        );
    }
}