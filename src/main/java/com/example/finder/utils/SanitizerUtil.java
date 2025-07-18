package com.example.finder.utils;

import com.example.finder.dto.input.RequestRegister;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

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
                inputs.getFirstName().trim(),
                inputs.getLastName().trim(),
                inputs.getDisplayName().trim(),
                inputs.getEmail().trim(),
                inputs.getPassword().trim()
        );
    }
}