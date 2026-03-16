package com.example.finder.utils.validator;

import com.example.finder.exception.file.InvalidImageException;
import com.example.finder.exception.file.NullFileException;
import com.luciad.imageio.webp.WebPImageReaderSpi;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorImageTest {

    private ValidatorImage validatorImage;
    private static final int DEFAULT_IMAGE_WIDTH = 200;
    private static final int DEFAULT_IMAGE_HEIGHT = 200;
    private static final long MAX_IMAGE_SIZE_BYTES = 15L * 1024 * 1024;
    private static final int MAX_IMAGE_DIMENSION_PX = 4096;

    @BeforeEach
    void setUp() {
        validatorImage = new ValidatorImage();
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new WebPImageWriterSpi());
        registry.registerServiceProvider(new WebPImageReaderSpi());
    }

    @Test
    void validateImage_ShouldReturnTrueForValidJpegImage() throws IOException {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                createImageBytes("jpeg", DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT));

        assertTrue(validatorImage.validateImage(validImage));
    }

    @Test
    void validateImage_ShouldReturnTrueForValidPngImage() throws IOException {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                createImageBytes("png", DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT));

        assertTrue(validatorImage.validateImage(validImage));
    }

    @Test
    void validateImage_ShouldReturnTrueForValidWebpImage() throws IOException {
        MultipartFile validImage = new MockMultipartFile(
                "file",
                "test.webp",
                "image/webp",
                createImageBytes("png", DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT));

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
    void validateImage_ShouldAcceptJpegWithAlternativeMimeType() throws IOException {
        // Some systems may use image/jpeg
        MultipartFile jpegImage = new MockMultipartFile(
                "file",
                "test.jpeg",
                "image/jpeg",
                createImageBytes("jpeg", DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT));

        assertTrue(validatorImage.validateImage(jpegImage));
    }

    @Test
    void validateImage_ShouldHandleLargeValidImage() throws IOException {
        MultipartFile largeImage = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                createImageBytes("jpeg", 2000, 2000));

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

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionWhenFileSizeTooLarge() {
        byte[] oversizedContent = new byte[(int) (MAX_IMAGE_SIZE_BYTES + 1)];
        MultipartFile oversizedFile = new MockMultipartFile(
                "file",
                "oversized.jpg",
                "image/jpeg",
                oversizedContent);

        InvalidImageException exception = assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(oversizedFile);
        });

        assertTrue(exception.getMessage().contains("maximum file size"));
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionWhenDimensionsTooLarge() throws IOException {
        MultipartFile oversizedDimensionImage = new MockMultipartFile(
                "file",
                "too-wide.jpg",
                "image/jpeg",
                createImageBytes("jpeg", MAX_IMAGE_DIMENSION_PX + 1, 100));

        InvalidImageException exception = assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(oversizedDimensionImage);
        });

        assertTrue(exception.getMessage().contains("Image dimensions exceed maximum"));
    }

    @Test
    void validateImage_ShouldThrowInvalidImageExceptionWhenImageHeightTooLarge() throws IOException {
        MultipartFile oversizedHeightImage = new MockMultipartFile(
                "file",
                "too-tall.jpg",
                "image/jpeg",
                createImageBytes("jpeg", 100, MAX_IMAGE_DIMENSION_PX + 1));

        InvalidImageException exception = assertThrows(InvalidImageException.class, () -> {
            validatorImage.validateImage(oversizedHeightImage);
        });

        assertTrue(exception.getMessage().contains("Image dimensions exceed maximum"));
    }

    private byte[] createImageBytes(String format, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean writeResult = ImageIO.write(image, format, outputStream);

        if (!writeResult) {
            throw new IOException("Could not write test image in format: " + format);
        }

        return outputStream.toByteArray();
    }
}
