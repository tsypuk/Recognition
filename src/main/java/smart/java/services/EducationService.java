package smart.java.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import smart.java.data.Image;
import smart.java.utils.ImageUtils;

import static smart.java.utils.ImageUtils.blackPixelInColumn;
import static smart.java.utils.ImageUtils.copyImage;
import static smart.java.utils.ImageUtils.drawVerticalLine;
import static smart.java.utils.ImageUtils.loadImageFromFile;
import static smart.java.utils.ImageUtils.saveImageToFile;

public class EducationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EducationService.class);

    /**
     * The size of alphabet will be
     * 0-9
     * a-z
     * A-Z
     */
    Map<Image, String> alphabet = new HashMap<>();
    private static final int MAX_CHAR_PIXEL_WIDTH = 15;

    public static void main(String[] args) {
        EducationService service = new EducationService();
        service.learn();
    }

    public void learn() {
        Map<Image, String> imageStringMap = prepareForLearning();
        long count = imageStringMap.keySet().stream()
                .peek(image -> learnFromImage(image.getImage(), imageStringMap.get(image)))
                .count();
        LOGGER.info("Were processed {} images for learning the alphabet", count);
    }

    private Map<Image, String> prepareForLearning() {
        HashMap<Image, String> imageStringHashMap = new HashMap<>();

        imageStringHashMap.put(new Image(loadImageFromFile("position/known/intermide.png")),
                "AbilitonIntermediateSoftwareEngineer");
        return imageStringHashMap;
    }

    private void learnFromImage(BufferedImage image, String controlString) {
        // Mark the image by vertical lines with width < then MAX_CHAR_PIXEL_WIDTH
        BufferedImage copyImage = copyImage(image);
        int charsCounter = 0;
        boolean previousSpace = true;
        List<Integer> charsCoordinates = new ArrayList<>();
        for (int x = 0; x < copyImage.getWidth(); x++) {
            if (blackPixelInColumn(x, 0, copyImage.getHeight(), copyImage)) {
                // the char started
                if (previousSpace) {
                    charsCoordinates.add(x);
                    charsCounter++;
                }
                    previousSpace = false;
            } else {
                drawVerticalLine(copyImage, x);
                previousSpace = true;
            }
        }
        charsCoordinates.add(copyImage.getWidth());
        LOGGER.info("Graphical charcounter={} String chars={} string={}", charsCounter, controlString.length(),controlString);
        LOGGER.info("Char coordinates size={} elements: {}",charsCoordinates.size(), charsCoordinates);
        saveImageToFile(copyImage, "position/known/test.png");

        // Verify that number of chars int the control string is the same as in recognizing image
        if (charsCounter == controlString.length()) {
            // Put every char to alphabet
            int previousX = 0;
            int previousY = 0;

            for (int i = 0; i < charsCoordinates.size() - 1; i++) {
/*
                LOGGER.info("previousX={}, previousY={}, width={}, height={}, charcoordinat={}", previousX,
                        previousY,
                        charsCoordinates.get(i + 1) - charsCoordinates.get(i),
                        image.getHeight(),
                        charsCoordinates.get(i));
*/

                BufferedImage letter = ImageUtils.removeEmptyPixels(image.getSubimage(
                        previousX,
                        previousY,
                        charsCoordinates.get(i + 1) - charsCoordinates.get(i),
                        image.getHeight()
                        ));
                previousX = charsCoordinates.get(i);
                alphabet.put(new Image(letter), String.valueOf(controlString.charAt(i)));
                LOGGER.info("Letter {} added to alphabet.", String.valueOf(controlString.charAt(i)));
            }
        }
        else {
            throw new RuntimeException("Learning failed. Chars count in patern and image is not the same.");
        }

        showAlphabet();

        // In case when two ore more chars are to tight to each other need to be added additional algorithm

        //

    }

    private void showAlphabet() {
        LOGGER.info(alphabet.keySet().stream().collect(Collectors.toList()).toString());
    }
}
