package com.example.finder.utils;

import com.example.finder.model.AnnounceType;
import com.example.finder.model.Category;
import com.luciad.imageio.webp.WebPImageReaderSpi;
import com.luciad.imageio.webp.WebPImageWriterSpi;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageUtil {
    private final String apiDomain;
    private final String photoDirectory;

    @Autowired
    public ImageUtil(
            @Value("${api.domain}") String apiDomain,
            @Value("${photo.public.path}") String photoDirectory
    ) {
        this.apiDomain = apiDomain;
        this.photoDirectory = photoDirectory;
    }

    @PostConstruct
    public void registerWebPPlugin() {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new WebPImageWriterSpi());
        registry.registerServiceProvider(new WebPImageReaderSpi());
    }


    public String getPhotoDirectory() {
        return photoDirectory;
    }

    public Path getLocalImagePath(String imageName){
        Path dir = Paths.get(photoDirectory);
        Path imagePath = dir.resolve(imageName);
        return imagePath;
    }

    public String getWebPathToPhoto(String photoName) {
        return StringUtil.concatJoined(
                apiDomain,
                "/",
                photoDirectory,
                "/",
                photoName
        );
    }

    public static String createImageName(
            AnnounceType announceType,
            Category category
    ) throws Exception{
        if(announceType == null){
            throw new Exception("Announce type can't be null when generating an image name");
        }
        if(category == null){
            throw new Exception("Category can't be null when generating an image name");
        }
        return StringUtil.concatJoined(
                announceType.getName(),
                "-",
                category.getName(),
                "-",
                String.valueOf(System.currentTimeMillis()),
                ".webp"
        );
    }

    public void saveImage(MultipartFile multipartFile, String savedImageName) throws IOException {
        Path uploadDir = Paths.get(photoDirectory);
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(savedImageName);

        BufferedImage bufferedImage = getImageContentFromMultipartFile(multipartFile);
        createWEBPImage(bufferedImage, filePath.toFile());
    }

    public BufferedImage getImageContentFromMultipartFile(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            if (bufferedImage == null) {
                throw new IOException("Could not read image from uploaded file.");
            }
            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createWEBPImage(BufferedImage bufferedImageContent, File imageFile) throws IOException{
        boolean result = ImageIO.write(bufferedImageContent, "webp", imageFile);
        if (!result){
            throw new IOException("Could not save image with name \""+imageFile.getName()+"\"");
        }
    }
}
