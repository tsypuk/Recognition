package smart.java.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import smart.java.data.Image;
import smart.java.services.ImagesProducerService;

public class ImagesProducerServiceImpl implements ImagesProducerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImagesProducerServiceImpl.class);
    public static final int BLACK_FILTER_COLOR = -1157956;
    private static final int LINE_BASE_COLOR = -986896;

    private List<Integer> xLines = new ArrayList<>();
    private List<Integer> yLines = new ArrayList<>();

    private static int autoIncrement = 0;

    @Override
    public void splitImageToLinePictures(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

//          Prepare image for processing img contains only area with text that we need to recognize
        image = prepareImage(image);

        LOGGER.info("Image resolution is: {}x{} px ", image.getWidth(), image.getHeight());
        calculateLines(image);

        int previousY = 0;

        for (Integer yLine : yLines) {
            writeImageToFile(0, xLines.get(xLines.size() - 1), previousY, yLine, new Image(image), "lines/", String.valueOf(autoIncrement++));
            previousY = yLine;
        }
    }

    /**
     * Removes the areas that do not hold information and do not take place in information recognition.
     */
    private BufferedImage prepareImage(BufferedImage source) {
        final int upperLeftConerX = 246;
        final int upperLeftConerY = 340;
        BufferedImage subimage = source.getSubimage(upperLeftConerX, upperLeftConerY, 2400, 1050);
        File outputfile = new File("image222.png");
        try {
            ImageIO.write(subimage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subimage;
    }

    /**
     * The lines split the image for cells. Each cell contains the info that we need recognize.
     * We need coordinates of every cell. We know the line color and we are scanning for it.
     */
    private void calculateLines(BufferedImage image) {
        final int offsetX = 308;
        for (int y = 0; y < image.getHeight(); y++) {
            int pixelRGB = image.getRGB(offsetX, y);
            if (pixelRGB == LINE_BASE_COLOR) {
//                The line is 2px width so double increase the counter
                y++;
                yLines.add(y);
//                System.out.println("Line Found" + ++linesCount);
            }
        }
//        We need to add the end of the image too
        yLines.add(image.getHeight());

        final int offsetY = 3;
        for (int x = 0; x < image.getWidth(); x++) {
            int pixelRGB = image.getRGB(x, offsetY);
            if (pixelRGB == LINE_BASE_COLOR) {
                xLines.add(x);
//                The line is 2px width so double increase the counter
                x++;
//                System.out.println("Column Found" + ++columnsCount);
            }
        }
        xLines.add(image.getWidth());
    }

    private void writeImageToFile(int startX, int endX, int startY, int endY, Image image, String
            path, String fileName) {
        BufferedImage subimage = image.getImage().getSubimage(startX, startY, endX - startX, endY - startY);
        File outputFile = new File("alphabet/" + path + fileName + ".png");
        try {
            outputFile.createNewFile();
            ImageIO.write(subimage, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}