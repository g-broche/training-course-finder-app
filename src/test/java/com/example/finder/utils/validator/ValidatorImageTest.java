package com.example.finder.utils.validator;

import com.example.finder.exception.file.InvalidImageException;
import com.example.finder.exception.file.NullFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorImageTest {

    private ValidatorImage validatorImage;

    @BeforeEach
    void setUp() {
        validatorImage = new ValidatorImage();
    }

    @Test
    void validateImage_ShouldReturnTrueForValidJpegImage() {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        assertTrue(validatorImage.validateImage(validImage));
    }

    @Test
    void validateImage_ShouldReturnTrueForValidPngImage() {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                "test image content".getBytes());

        assertTrue(validatorImage.validateImage(validImage));
    }

    @Test
    void validateImage_ShouldReturnTrueForValidWebpImage() {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                "test image content".getBytes());

        assertTrue(validatorImage.validateImage(validImage));
    }

    @Test
    void validateImage_ShouldThrowNullFileExceptionForNullFile() {
        assertThrows(NullFileException.class, () -> {
            validatorImage.validateImage(null);
        });
    }

    @Test
    void validateImage_ShouldThrowNullFileExceptionForEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]);

        assertThrows(NullFileException.class, () -> {
            validatorImage.validateImage(emptyFile);
        });
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionForInvalidMimeType() {
        MultipartFile invalidImage = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "test image content".getBytes());

        InvalidImageException exception = assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(invalidImage);
        });

        assertTrue(exception.getMessage().contains("Invalid image type"));
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionForTextFile() {
        MultipartFile textFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "test content".getBytes());

        assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(textFile);
        });
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionForPdfFile() {
        MultipartFile pdfFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes());

        assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(pdfFile);
        });
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionForSvgFile() {
        MultipartFile svgFile = new MockMultipartFile(
                "file",
                "test.svg",
                "image/svg+xml",
                "test content".getBytes());

        assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(svgFile);
        });
    }

    @Test
    void validateImage_ShouldIncludeAllowedTypesInExceptionMessage() {
        MultipartFile invalidImage = new MockMultipartFile(
                "file",
                "test.gif",
                "image/gif",
                "test content".getBytes());

        InvalidImageException exception = assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(invalidImage);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("JPEG"));
        assertTrue(message.contains("PNG"));
        assertTrue(message.contains("WEBP"));
    }

    @Test
    void validateImage_ShouldAcceptJpegWithAlternativeMimeType() {
        // Some systems may use image/jpeg
        MultipartFile jpegImage = new MockMultipartFile(
                "file",
                "test.jpeg",
                "image/jpeg",
                "test content".getBytes());

        assertTrue(validatorImage.validateImage(jpegImage));
    }

    @Test
    void validateImage_ShouldHandleLargeValidImage() {
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        MultipartFile largeImage = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent);

        assertTrue(validatorImage.validateImage(largeImage));
    }

    @Test
    void validateImage_ShouldRejectVideoFile() {
        MultipartFile videoFile = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                "test content".getBytes());

        assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(videoFile);
        });
    }
}
