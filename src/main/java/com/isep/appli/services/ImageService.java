package com.isep.appli.services;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageService {

    public byte[] compressImage(InputStream inputStream) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ImageIO.write(originalImage, "jpg", outputStream);

        byte[] compressedImageBytes = outputStream.toByteArray();

        outputStream.close();

        return compressedImageBytes;
    }
}

