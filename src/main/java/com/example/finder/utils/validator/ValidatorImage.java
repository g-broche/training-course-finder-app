package com.example.finder.utils.validator;

import com.example.finder.exception.file.InvalidImageException;
import com.example.finder.exception.file.NullFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class ValidatorImage {
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp");
    private static final long MAX_IMAGE_SIZE_BYTES = 15L * 1024 * 1024;
    private static final int MAX_IMAGE_DIMENSION_PX = 4096;

    public boolean validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new NullFileException();
        }
        if (!ALLOWED_MIME_TYPES.contains(image.getContentType())) {
            throw new InvalidImageException("Invalid image type. Allowed types: JPEG, PNG, WEBP.");
        }

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new InvalidImageException("Image exceeds maximum file size of " + MAX_IMAGE_SIZE_BYTES + " bytes.");
        }

        BufferedImage bufferedImage = readImage(image);
        if (bufferedImage.getWidth() > MAX_IMAGE_DIMENSION_PX || bufferedImage.getHeight() > MAX_IMAGE_DIMENSION_PX) {
            throw new InvalidImageException(
                    "Image dimensions exceed maximum of " + MAX_IMAGE_DIMENSION_PX + "x" + MAX_IMAGE_DIMENSION_PX
                            + " pixels.");
        }

        return true;
    }

    private BufferedImage readImage(MultipartFile image) {
        try (InputStream inputStream = image.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                throw new InvalidImageException("The uploaded file is not a readable image.");
            }
            return bufferedImage;
        } catch (IOException e) {
            throw new InvalidImageException("Could not read uploaded image.", e);
        }
    }
}
