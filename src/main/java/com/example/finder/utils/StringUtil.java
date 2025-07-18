package com.example.finder.utils;

public class StringUtil {
    public static String concat(String... substrings){
        return String.join(" ", substrings);
    }
    public static String concatWithDelimiter(String delimiter, String... substrings){
        return String.join(delimiter, substrings);
    }
}
