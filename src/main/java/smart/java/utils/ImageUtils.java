package smart.java.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

    public static final int BLACK_FILTER_COLOR = -1157956;
    static Color color = new Color(0, 255, 0);
    public static final int RED_COLOR_FOR_LINE = color.getRGB();

    /**
     * remove not needed pixels from up and down left and right
     */
    public static BufferedImage removeEmptyPixels(BufferedImage image) {
        final int maxPixels = 600;
//        Find top and button of the image and cut its size

        int topY = 0;
        for (int y = 0; y < maxPixels; y++) {
            if (blackPixelInRow(y, 0, image.getWidth(), image)) {
                topY = y;
                break;
            }
        }

        int bottomY = image.getHeight() - 1;
        for (int y = bottomY; y > image.getHeight() - maxPixels; y--) {
            if (blackPixelInRow(y, 0, image.getWidth(), image)) {
                bottomY = y;
                break;
            }
        }

        int leftX = 0;
        for (int x = 0; x < maxPixels; x++) {
            if (blackPixelInColumn(x, 0, image.getHeight(), image)) {
                leftX = x;
                break;
            }
        }

        int rightX = image.getWidth() - 1;
        for (int x = rightX; x > image.getWidth() - maxPixels; x--) {
            if (blackPixelInColumn(x, 0, image.getHeight(), image)) {
                rightX = x;
                break;
            }
        }

        return image.getSubimage(leftX, topY, rightX - leftX + 1, bottomY - topY + 1);
    }

    public static boolean blackPixelInRow(int y, int startX, int endX, BufferedImage image) {
        for (int x = startX; x < endX; x++) {
            if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                return true;
            }
        }
        return false;
    }

    public static boolean blackPixelInColumn(int x, int startY, int endY, BufferedImage image) {
        for (int y = startY; y < endY; y++) {
            if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                return true;
            }
        }
        return false;
    }

    /**
     * All pixels that are more 'blacker' then BLACK_FILTER_COLOR' are black too
     * This adds more solid font and much easy to recognize.
     */
    public static boolean checkForBlackPixelWithCorrection(int pixel) {
        return ((pixel < BLACK_FILTER_COLOR));
    }

    public static BufferedImage loadImageFromFile(String fileName) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage subimage = source.getSubimage(0, 0, source.getWidth(), source.getHeight());
        return subimage;
    }

    public static void drawVerticalLine(BufferedImage image, int xCordinateVerticalLine) {
        for (int y = 0; y < image.getHeight(); y++) {
            image.setRGB(xCordinateVerticalLine, y, RED_COLOR_FOR_LINE);
        }
    }

    public static void saveImageToFile(BufferedImage image, String fileName) {
        File outputfile = new File(fileName);
        try {
            outputfile.createNewFile();
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}