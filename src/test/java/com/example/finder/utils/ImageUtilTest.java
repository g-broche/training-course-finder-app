package com.example.finder.utils;

import com.example.finder.model.AnnounceType;
import com.example.finder.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageUtilTest {

    @Mock
    private AnnounceType announceType;

    @Mock
    private Category category;

    private ImageUtil imageUtil;
    private static final String TEST_API_DOMAIN = "http://localhost:8080";
    private static final String TEST_PHOTO_DIRECTORY = "test-uploads/photos";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        imageUtil = new ImageUtil(TEST_API_DOMAIN, TEST_PHOTO_DIRECTORY);
    }

    @Test
    void getPhotoDirectory_ShouldReturnConfiguredDirectory() {
        String result = imageUtil.getPhotoDirectory();
        assertEquals(TEST_PHOTO_DIRECTORY, result);
    }

    @Test
    void getBaseWebPathForPhotos_ShouldReturnCorrectUrl() {
        String result = imageUtil.getBaseWebPathForPhotos();

        assertEquals(TEST_API_DOMAIN + "/" + TEST_PHOTO_DIRECTORY + "/", result);
    }

    @Test
    void getWebPathToPhoto_ShouldReturnCorrectUrl() {
        String photoName = "test-photo.webp";
        String result = imageUtil.getWebPathToPhoto(photoName);

        assertEquals(TEST_API_DOMAIN + "/" + TEST_PHOTO_DIRECTORY + "/" + photoName, result);
    }

    @Test
    void createImageName_ShouldGenerateCorrectFormat() throws Exception {
        when(announceType.getName()).thenReturn("LOST");
        when(category.getName()).thenReturn("Electronics");

        String result = ImageUtil.createImageName(announceType, category);

        assertTrue(result.startsWith("LOST-Electronics-"));
        assertTrue(result.endsWith(".webp"));
    }

    @Test
    void createImageName_ShouldIncludeTimestamp() throws Exception {
        when(announceType.getName()).thenReturn("FOUND");
        when(category.getName()).thenReturn("Keys");

        long beforeTime = System.currentTimeMillis();
        String result = ImageUtil.createImageName(announceType, category);
        long afterTime = System.currentTimeMillis();

        String[] parts = result.replace(".webp", "").split("-");
        assertTrue(parts.length >= 3);
        long timestamp = Long.parseLong(parts[2]);

        assertTrue(timestamp >= beforeTime && timestamp <= afterTime);
    }

    @Test
    void createImageName_ShouldGenerateDifferentNamesOnConsecutiveCalls() throws Exception {
        when(announceType.getName()).thenReturn("LOST");
        when(category.getName()).thenReturn("Electronics");

        String name1 = ImageUtil.createImageName(announceType, category);
        // Small delay to ensure different timestamps
        Thread.sleep(2);
        String name2 = ImageUtil.createImageName(announceType, category);

        assertNotEquals(name1, name2);
    }

    @Test
    void getImageContentFromMultipartFile_ShouldReturnNullForInvalidImage() throws IOException {
        byte[] invalidContent = "not an image".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                invalidContent);

        BufferedImage result = imageUtil.getImageContentFromMultipartFile(multipartFile);

        assertNull(result);
    }

    @Test
    void getImageContentFromMultipartFile_ShouldReturnBufferedImageForValidImage() throws IOException {
        // Create a simple 10x10 pixel image
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                imageBytes);

        BufferedImage result = imageUtil.getImageContentFromMultipartFile(multipartFile);

        assertNotNull(result);
        assertEquals(10, result.getWidth());
        assertEquals(10, result.getHeight());
    }

    @Test
    void createWEBPImage_ShouldThrowExceptionForNullBufferedImage() {
        File outputFile = new File(tempDir.toFile(), "output.webp");

        assertThrows(Exception.class, () -> {
            imageUtil.createWEBPImage(null, outputFile);
        });
    }

    @Test
    void saveImage_ShouldCreateDirectoryIfNotExists() throws IOException {
        // Create a test image
        BufferedImage testImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                imageBytes);

        // Use temp directory for this test
        ImageUtil tempImageUtil = new ImageUtil(TEST_API_DOMAIN, tempDir.toString());
        String savedImageName = "test-saved.webp";

        tempImageUtil.saveImage(multipartFile, savedImageName);

        Path expectedPath = tempDir.resolve(savedImageName);
        assertTrue(Files.exists(expectedPath));
    }

    @Test
    void getLocalImagePath_ShouldResolveRelativeToPhotoDirectory() {
        String imageName = "example.webp";
        Path result = imageUtil.getLocalImagePath(imageName);

        Path expectedPath = Paths.get(TEST_PHOTO_DIRECTORY).resolve(imageName);
        assertEquals(expectedPath, result);
    }
}
