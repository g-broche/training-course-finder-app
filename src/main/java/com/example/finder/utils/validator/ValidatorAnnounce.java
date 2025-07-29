package com.example.finder.utils.validator;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ValidatorAnnounce {
    private final Map<String, Integer> title = Map.of(
            "min", 5,
            "max", 50
    );
    private final Map<String, Integer> description = Map.of(
            "min", 30,
            "max", 1000
    );
}
