package com.example.finder.utils;

import com.example.finder.model.AnnounceStatus;
import com.example.finder.model.AnnounceType;
import com.example.finder.model.Category;

import java.text.Normalizer;

public class StringUtil {
    public static String concat(String... substrings){
        return String.join(" ", substrings);
    }

    public static String concatJoined(String... substrings){
        return String.join("", substrings);
    }

    public static String concatWithDelimiter(String delimiter, String... substrings){
        return String.join(delimiter, substrings);
    }

    public static String toSlug(String input){
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = normalized.replaceAll("[^\\w\\s-]", "")
                .trim()
                .replaceAll("[\\s]+", "-")
                .toLowerCase();
        return slug;
    }
}
