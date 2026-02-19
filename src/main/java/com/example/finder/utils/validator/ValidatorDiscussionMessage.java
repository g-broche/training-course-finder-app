package com.example.finder.utils.validator;

import com.example.finder.dto.input.RequestDiscussion;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.utils.StringUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ValidatorDiscussionMessage {
    private final Map<String, Integer> messageSpecs = Map.of(
            "min", 1,
            "max", 500);

    public boolean isValidMessage(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() >= messageSpecs.get("min")
                && trimmedInput.length() <= messageSpecs.get("max");
    }

    public List<ErrorDto> validateDiscussionMessage(RequestDiscussion inputs) {
        List<ErrorDto> errors = new ArrayList<>();
        if (!isValidMessage(inputs.getMessage())) {
            errors.add(buildMessageError());
        }
        return errors;
    }

    private ErrorDto buildMessageError() {
        String message = StringUtil.concat(
                "message must have between",
                messageSpecs.get("min").toString(),
                "and",
                messageSpecs.get("max").toString(),
                "characters");
        return new ErrorDto("title", message);
    }
}
