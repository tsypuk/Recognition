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
import smart.java.services.GetImageFromFile;

import static smart.java.utils.ImageUtils.removeEmptyPixels;

public class PositionsProducerServiceImpl implements GetImageFromFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(PositionsProducerServiceImpl.class);

    private static final int LINE_BASE_COLOR = -986896;

    private List<Integer> xLines = new ArrayList<>();

    @Override
    public Image getPositionFromFile(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Image resolution is: {}x{} px ", image.getWidth(), image.getHeight());
        calculateLines(image);

        // Read the position image
        return new Image(
                removeEmptyPixels(
                image.getSubimage(xLines.get(1), 0, xLines.get(2) - xLines.get(1), image.getHeight()))
        );
    }

    /**
     * The lines split the image for cells. Each cell contains the info that we need recognize.
     * We need coordinates of every cell. We know the line color and we are scanning for it.
     */
    private void calculateLines(BufferedImage image) {
        final int offsetX = 308;
        int columnsCount = 0;

        final int offsetY = 3;
        for (int x = 0; x < image.getWidth(); x++) {
            int pixelRGB = image.getRGB(x, offsetY);
            if (pixelRGB == LINE_BASE_COLOR) {
                xLines.add(x);
//                The line is 2px width so double increase the counter
                x++;
//                System.out.println("Column Found" + ++columnsCount);
                columnsCount++;
            }
        }
        xLines.add(image.getWidth());

        System.out.println("Total columns: " + columnsCount);
        System.out.println(xLines);
    }

}