package com.jay.timestamp;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("")
public class ImageController {
    @PostMapping("/image")
    public ResponseEntity<Map<String, String>> watermarkImage(@RequestParam("text") String text, @RequestParam(name = "file") MultipartFile file) {
        HashMap<String, String> map = new HashMap<>();
        if (file != null) {
            if (file.getContentType() != null && (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))) {
                try {
                    File watermarkImage = addTextWatermark(text, multipartToFile(file));
                    byte[] bytes = readFileToByteArray(watermarkImage);
                    byte[] encoded = Base64.encodeBase64(bytes);
                    String encodedString = new String(encoded);
                    map.put("watermark_image", encodedString);
                    return new ResponseEntity<>(map, HttpStatus.OK);
                } catch (Exception e) {
                    map.put("watermark_image", "Something went wrong!");
                    e.printStackTrace();
                    return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                map.put("error", "Only PNG & JPEG files are supported");
                return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
            }
        } else {
            map.put("error", "file can't be empty");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    private File addTextWatermark(String text, File sourceImageFile) {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            Graphics2D graphics2D = (Graphics2D) sourceImage.getGraphics();
            // initializes necessary graphic properties
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
            graphics2D.setComposite(alphaComposite);
            graphics2D.setColor(Color.BLUE);
            graphics2D.setFont(new Font("Arial", Font.BOLD, 64));
            FontMetrics fontMetrics = graphics2D.getFontMetrics();
            Rectangle2D rectangle2D = fontMetrics.getStringBounds(text, graphics2D);
            // calculates the coordinate where the String is painted
            int centerX = (sourceImage.getWidth() - (int) rectangle2D.getWidth()) / 2;
            int centerY = sourceImage.getHeight() / 2;
            // paints the textual watermark
            graphics2D.drawString(text, centerX, centerY);
            ImageIO.write(sourceImage, "png", sourceImageFile);
            graphics2D.dispose();
            System.out.println("The tex watermark is added to the image.");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return sourceImageFile;
    }

    private File multipartToFile(MultipartFile multipartFile) {
        try {
            File file = new File(multipartFile.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] readFileToByteArray(File file) {
        FileInputStream fileInputStream;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bArray);
            fileInputStream.close();
        } catch (IOException ioExp) {
            ioExp.printStackTrace();
            return null;
        }
        return bArray;
    }
}
