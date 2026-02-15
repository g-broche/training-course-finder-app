package com.example.finder.utils.validator;

import com.example.finder.dto.input.RequestAnnounce;
import com.example.finder.dto.input.RequestRegister;
import com.example.finder.dto.output.ErrorDto;
import com.example.finder.utils.StringUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class ValidatorAnnounce {
    private final Map<String, Integer> titleSpecs = Map.of(
            "min", 5,
            "max", 50);
    private final Map<String, Integer> descriptionSpecs = Map.of(
            "min", 30,
            "max", 1000);
    private final Map<String, Integer> citySpecs = Map.of(
            "min", 1,
            "max", 100);
    private final Map<String, Integer> countrySpecs = Map.of(
            "min", 1,
            "max", 50);
    private final Map<String, Integer> coordinateSpecs = Map.of(
            "max", 30);

    public boolean isValidTitle(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() >= titleSpecs.get("min")
                && trimmedInput.length() <= titleSpecs.get("max");
    }

    public boolean isValidDescription(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() >= descriptionSpecs.get("min")
                && trimmedInput.length() <= descriptionSpecs.get("max");
    }

    public boolean isValidCity(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() >= citySpecs.get("min")
                && trimmedInput.length() <= citySpecs.get("max");
    }

    public boolean isValidCountry(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() >= countrySpecs.get("min")
                && trimmedInput.length() <= countrySpecs.get("max");
    }

    public boolean isValidLatitude(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() <= coordinateSpecs.get("max")
                && StringUtil.isDouble(input);
    }

    public boolean isValidLongitude(String input) {
        if (input == null) {
            return false;
        }
        String trimmedInput = input.trim();
        return trimmedInput.length() <= coordinateSpecs.get("max")
                && StringUtil.isDouble(input);
    }

    public boolean isValidDate(LocalDate input) {
        if (input == null) {
            return false;
        }
        return !input.isAfter(LocalDate.now());
    }

    public List<ErrorDto> validateAnnounceInputs(RequestAnnounce inputs) {
        List<ErrorDto> errors = new ArrayList<>();
        if (!isValidTitle(inputs.getTitle())) {
            errors.add(buildTitleError());
        }
        if (!isValidDescription(inputs.getDescription())) {
            errors.add(buildDescriptionError());
        }
        if (!isValidCity(inputs.getCity())) {
            errors.add(buildCityError());
        }
        if (!isValidCountry(inputs.getCountry())) {
            errors.add(buildCountryError());
        }
        if (!isValidLatitude(inputs.getLatitude())) {
            errors.add(buildLatitudeError());
        }
        if (!isValidLongitude(inputs.getLongitude())) {
            errors.add(buildLongitudeError());
        }
        if (!isValidDate(inputs.getRelevantDate())) {
            errors.add(buildDateError());
        }
        return errors;
    }

    private ErrorDto buildTitleError() {
        String message = StringUtil.concat(
                "title must have between",
                titleSpecs.get("min").toString(),
                "and",
                titleSpecs.get("max").toString(),
                "characters");
        return new ErrorDto("title", message);
    }

    private ErrorDto buildDescriptionError() {
        String message = StringUtil.concat(
                "description must have between",
                descriptionSpecs.get("min").toString(),
                "and",
                descriptionSpecs.get("max").toString(),
                "characters");
        return new ErrorDto("description", message);
    }

    private ErrorDto buildCityError() {
        String message = StringUtil.concat(
                "city must have between",
                citySpecs.get("min").toString(),
                "and",
                citySpecs.get("max").toString(),
                "characters");
        return new ErrorDto("city", message);
    }

    private ErrorDto buildCountryError() {
        String message = StringUtil.concat(
                "country must have between",
                countrySpecs.get("min").toString(),
                "and",
                countrySpecs.get("max").toString(),
                "characters");
        return new ErrorDto("country", message);
    }

    private ErrorDto buildLatitudeError() {
        String message = StringUtil.concat(
                "latitude must be a decimal number with less than",
                coordinateSpecs.get("max").toString(),
                "characters");
        return new ErrorDto("latitude", message);
    }

    private ErrorDto buildLongitudeError() {
        String message = StringUtil.concat(
                "longitude must be a decimal number with less than",
                coordinateSpecs.get("max").toString(),
                "characters");
        return new ErrorDto("longitude", message);
    }

    private ErrorDto buildDateError() {
        String message = "Relevant date cannot be after the current date";
        return new ErrorDto("relevant date", message);
    }

}
