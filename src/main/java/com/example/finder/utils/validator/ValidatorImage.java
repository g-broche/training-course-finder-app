package com.example.finder.utils.validator;

import com.example.finder.exception.file.InvalidImageException;
import com.example.finder.exception.file.NullFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class ValidatorImage {
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");

    public boolean validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new NullFileException();
        }
        if (!ALLOWED_MIME_TYPES.contains(image.getContentType())) {
            throw new InvalidImageException("Invalid image type. Allowed types: JPEG, PNG, WEBP.");
        }
        return true;
    }
}
