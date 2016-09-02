package smart.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import smart.java.data.Image;
import smart.java.services.impl.PositionsProducerServiceImpl;

public class FindUniquePositions {
    private static final Logger LOGGER = LoggerFactory.getLogger(FindUniquePositions.class);
    private static final String IMAGES_FOLDER = "alphabet/lines";
    private static final Set<Image> images = new HashSet<>();
    private static final Map<Image, Integer> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
        LOGGER.info("FindUniquePositions");

        File imagesDirectory = new File(IMAGES_FOLDER);

        LOGGER.info("Files processed: {}",
                Arrays.stream(imagesDirectory.list())
                        .filter(fileName -> fileName.endsWith(".png"))
//                        .limit(100)
                        .map(fileName -> IMAGES_FOLDER.concat("/").concat(fileName))
                        .peek(fileName -> LOGGER.info("Submitting task to process file: {}", fileName))
                        .peek(fileName -> {
                            Image image = new PositionsProducerServiceImpl().getPositionFromFile(fileName);
                            images.add(image);
                            if (map.containsKey(image)) {
                                map.put(image, map.get(image) + 1);
                            } else {
                                map.put(image, 1);
                            }

                        })
                        .count());


        calculatePositions();

        LOGGER.info("The size of unique set is {}.", images.size());
        LOGGER.info("The details are {}", map);

//        writeImageToFile(images);

    }

    private static void calculatePositions() {
        Arrays.stream(new File("position/known").list())
                .peek(System.out::println)
                .map(fileName -> "position/known/".concat(fileName))
                .peek(fileName -> {
                            try {
                                Image position = new Image(ImageIO.read(new File(fileName)));
                                LOGGER.error("{} persons found {}",fileName, map.get(position));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).count();
    }

    private static void writeImageToFile(Set<Image> imageSet) {
        int counter = 0;
        String path = "position/";
        for (Image image : imageSet) {
            File outputfile = new File(path + "image" + counter++ + ".png");
            try {
                ImageIO.write(image.getImage(), "png", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}