package com.company;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

// TODO Create graphical alphabet: every symbol write to file 'A' -> A.png for next aplhorithm of recognition with images compare.

// TODO Parrallel the tasks for groups of text.

// TODO add gradle, slf4j.

// TODO Add JUint to test the image files library with the recognition results.

// TODO Add connection to DB using JDBC to write results of recognition to Mysql.

/**
 * This is the scratch of recognitaion.
 * The basic alhorithm that calculates the nubmer of pixel in each area and compares it to dictionary.
 */
public class Main {
    //    private static final int BLACK_FILTER_COLOR = -11579569;
    private static final int BLACK_FILTER_COLOR = -1157956;
    private static final int LINE_BASE_COLOR = -986896;

    private static int width;
    private static int height;
    private static List<Integer> xLines = new ArrayList<>();
    private static List<Integer> yLines = new ArrayList<>();

    public static void main(String[] args) {
        try {
//	        Read file from FS
            BufferedImage img = ImageIO.read(new File("image.png"));
//          Prepare image for processing
            img = prepareImage(img);
            width = img.getWidth();
            height = img.getHeight();
            System.out.println(width + "x" + height);
            calculateLines(img);
            int previousX = 0;
            int previousY = 0;

            for (Integer yLine : yLines) {
                readNumber(previousX, previousY, xLines.get(0), yLine, img);
                System.out.print("  :  ");
                readEnglish(xLines.get(0), previousY, xLines.get(1), yLine, img);
                System.out.print("  :  ");
                readEnglish(xLines.get(1), previousY, xLines.get(2), yLine, img);
                System.out.println();
                previousY = yLine;
            }
        } catch (IOException e) {
        }
    }

    /**
     *  Removes the areas that do not hold information and do not take place in information recognition.
     */
    private static BufferedImage prepareImage(BufferedImage source) {
        final int upperLeftConerX = 246;
        final int upperLeftConerY = 340;
        BufferedImage subimage = source.getSubimage(upperLeftConerX, upperLeftConerY, 2400, 1050);
//        File outputfile = new File("image222.png");
//        try {
//            ImageIO.write(subimage, "png", outputfile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return subimage;
    }

    private static void readEnglish(int startX, int startY, int endX, int endY, BufferedImage image) {
        boolean blackPixelPresent = false;
        int stx = 0;
        for (int x = startX; x < endX; x++) {
            if (blackPixelInColumn(x, startY, endY, image)) {
                if (!blackPixelPresent) {
                    stx = x;
                }
                blackPixelPresent = true;
            } else if (blackPixelPresent) {
                // This is the end of char
                //send for recognition the area x1, x2, y1, y2
                System.out.print(recognizeEnglishChar(stx, x, startY, endY, image));
                blackPixelPresent = false;
            }
        }
    }

    private static void readPixels(BufferedImage img) {
        for (int x = 0; x < 100; height++)
            for (int y = 0; y < 100; width++)
                System.out.println(img.getRGB(x, y));
    }

    /**
     * The lines split the image for cells. Each cell contains the info that we need recognize.
     * We need coordinates of every cell. We know the line color and we are scanning for it.
     */
    private static void calculateLines(BufferedImage image) {
        final int offsetX = 308;
        int rowsCount = 0;
        int columnsCount = 0;
        for (int y = 0; y < height; y++) {
            int pixelRGB = image.getRGB(offsetX, y);
            if (pixelRGB == LINE_BASE_COLOR) {
//                The line is 2px width so double increase the counter
                y++;
                yLines.add(y);
                rowsCount++;
//                System.out.println("Line Found" + ++linesCount);
            }
        }
//        We need to add the end of the image too
        yLines.add(image.getHeight());

        final int offsetY = 3;
        for (int x = 0; x < width; x++) {
            int pixelRGB = image.getRGB(x, offsetY);
            if (pixelRGB == LINE_BASE_COLOR) {
                xLines.add(x);
//                The line is 2px width so double increase the counter
                x++;
//                System.out.println("Column Found" + ++columnsCount);
                columnsCount++;
            }
        }

        System.out.println("Total lines: " + rowsCount);
        System.out.println("Total columns: " + columnsCount);
        System.out.println(yLines);
        System.out.println(xLines);
    }

    /**
     * This is very useful method when debugging the code. Print the charecter as it is to the screen.
     *   111111
     *   111111
     * 11      11
     * 11      11
     *         11
     *         11
     *         11
     *         11
     *       11
     *       11
     *     11
     *     11
     *   11
     *   11
     * 11
     * 11
     * 1111111111
     * 1111111111
     */
    private static void printSymbolToScreen(int startX, int startY, int endX, int endY, BufferedImage image) {
        String c = " ";
//        System.out.println("Processing unit: (x1=" + startX + ", y1=" + startY + " x2=" + endX + ",y2=" + endY + ")");
        for (int y = startY; y < endY; y++) {
            System.out.println("");
            for (int x = startX; x < endX; x++) {
                int pixel = image.getRGB(x, y);
                if (checkForBlackPixelWithCorrection(pixel)) {
                    c = "1";
                } else c = " ";
                System.out.print(c);
                System.out.flush();
            }
        }
    }

    private static void readNumber(int startX, int startY, int endX, int endY, BufferedImage image) {
        boolean numberPresent = false;
        int stx = 0;
        for (int x = startX; x < endX; x++) {
            if (blackPixelInColumn(x, startY, endY, image)) {
                if (!numberPresent) {
                    stx = x;
                }
                numberPresent = true;
            } else if (numberPresent) {
                // This is the end of number
                //send for recognition the area x1, x2, y1, y2
                System.out.print(recognizeNumber(stx, x, startY, endY, image));
                numberPresent = false;
            }
        }
    }

    private static int recognizeNumber(int startX, int endX, int startY, int endY, BufferedImage image) {
//        print for debug mode the number
        printSymbolToScreen(startX, startY, endX, endY, image);
        //get the sum of black pixels
        int result = 0;
        int sum = 0;
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                    sum++;
                }
            }
        }

        switch (sum) {
            case 44:
                result = 1;
                break;
            case 64:
                //3
                if (getBlackPixelCountInColumn(startX, startY, endY, image) == 4) {
                    result = 3;
                } else {
                    result = 2;
                }
                break;
            case 72:
                result = 4;
                break;
            case 193:
                result = 5;
                break;
            case 80:
                //6 or 9 0
                //double check
                if (getBlackPixelCountInColumn(startX, startY, endY, image) == 12) {
                    return 5;
                } else if (getBlackPixelCountInColumn(startX, startY, endY, image) == 8) {
                    return 9;
                } else if (
                        (getBlackPixelCountInColumn(startX, startY, endY, image) == 14)
                                &&
                                (getBlackPixelCountInColumn(startX + 2, startY, endY, image) == 6)) {
                    result = 6;
                } else {
                    return 0;
                }
                break;
            case 52:
                result = 7;
                break;
            case 84:
                result = 8;
                break;
        }
        return result;
    }

    private static String recognizeEnglishChar(int startX, int endX, int startY, int endY, BufferedImage image) {
//        print for debug mode the number
//        printSymbolToScreen(startX, startY, endX, endY, image);
        //get the sum of black pixels
        int sum = 0;
        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                    sum++;
                }
            }
        }
        switch (sum) {
            case 64:
                return "a";
            case 120:
                return "c";
            case 157:
                return "e";
            case 98:
                return "f";
            case 84:
                return "g";
            case 162:
                return "h";
            case 28:
                return "i";
            case 153:
                return "k";
            case 36:
                //l I
//                if (getBlackPixelCountInColumn(startX, startY, endY, image) > 27) {
//                    return "I";
//                }
                return "l";
            case 190:
                return "m";
            case 135:
                return "n";
            case 134:
                return "o";
            case 170:
                return "p";
            case 66:
                return "r";
            case 110:
                return "s";
            case 89:
                return "t";
            case 40:
                return "v";
            case 136:
                return "y";
            case 118:
                return "z";
            case 225:
                return "ty";
            case 326:
                return "my";
            case 76:
                int count = getBlackPixelCountInColumn(startX + 3, startY, endY, image);
                if (count == 4) {
                    return "b";
                } else {
                    return "A";
                }
            case 169:
                return "S";
            case 187:
                return "E";
            default:
//                System.out.println(sum);
        }
        return String.valueOf(sum);
    }

    private static boolean blackPixelInColumn(int x, int startY, int endY, BufferedImage image) {
        for (int y = startY; y < endY; y++) {
            if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                return true;
            }
        }
        return false;
    }

    private static int getBlackPixelCountInColumn(int x, int startY, int endY, BufferedImage image) {
        int sum = 0;
        for (int y = startY; y < endY; y++) {
            if (checkForBlackPixelWithCorrection(image.getRGB(x, y))) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * All pixels that are more 'blacker' then BLACK_FILTER_COLOR' are black too
     * This adds more solid font and much easy to recognize.
     */
    private static boolean checkForBlackPixelWithCorrection(int pixel) {
        return ((pixel < BLACK_FILTER_COLOR));
    }

}