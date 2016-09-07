import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

// TODO create second algorithm that will compare not pixel's count in column, but images from the dictionary.

// TODO Parrallel the tasks for groups of text.

// TODO Add JUint to test the image files library with the recognition results.

// TODO Add connection to DB using JDBC to write results of recognition to Mysql.

/**
 * This is the scratch of recognition.
 * The basic algorithm that calculates the number of pixel in each area and compares it to dictionary.
 * PROS: the easiest algorithm, very visual for understanding.
 * CONS: it working only for concret resolution. If change picture resolution - the recognition fails.
 *       Really hard to support - lots of collisions in case statements.
 */

public class Main {
    alsfalsfhklshf
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);
    //    private static final int BLACK_FILTER_COLOR = -11579569;
    private static final int BLACK_FILTER_COLOR = -1157956;
    private static final int LINE_BASE_COLOR = -986896;
    private static int imagecounter = 0;

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
            LOGGER.info("Image resolution is: " + width + "x" + height);
            calculateLines(img);
            int previousX = 0;
            int previousY = 0;

            for (Integer yLine : yLines) {
                // ID
                readNumber(previousX, previousY, xLines.get(0), yLine, img);
                System.out.print("  :  ");
                // nickName
                readEnglish(xLines.get(0), previousY, xLines.get(1), yLine, img);
                System.out.print("  :  ");
                // position
                readEnglish(xLines.get(1), previousY, xLines.get(2), yLine, img);
                System.out.print("  :  ");
//                location
                readEnglish(xLines.get(3), previousY, xLines.get(4), yLine, img);
                System.out.print("  :  ");
//                name
                readEnglish(xLines.get(8), previousY, xLines.get(9), yLine, img);
                System.out.println();
                previousY = yLine;
            }
        } catch (IOException e) {
        }
    }

    /**
     * Removes the areas that do not hold information and do not take place in information recognition.
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
                // Cut letter from the image
                BufferedImage numberImage = preprocessLetter(stx, x, startY, endY, image);
                //send for recognition the area x1, x2, y1, y2
                int recognizedNumber = recognizeNumber(0, numberImage.getWidth(), 0, numberImage.getHeight(),
                        numberImage);
                System.out.print(recognizedNumber);
//                System.out.print(recognizeNumber(0, number.getWidth(), 0, number.getHeight(), image));

                writeImageToFile(0, numberImage.getWidth(), 0, numberImage.getHeight(), numberImage, "numbers/" +
                        String.valueOf
                        (recognizedNumber));
                numberPresent = false;
            }
        }
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
                BufferedImage letterImage = preprocessLetter(stx, x, startY, endY, image);
                //send for recognition the area x1, x2, y1, y2
                String recognizedLetter = recognizeEnglishChar(stx, x, startY, endY, image);
                System.out.print(recognizedLetter);
                String dir = isUpperCase(recognizedLetter) ? "capitalize/" : "char/";

                writeImageToFile(0, letterImage.getWidth(), 0, letterImage.getHeight(), letterImage, dir +
                        recognizedLetter);
                blackPixelPresent = false;
            }
        }
    }

    private static boolean isUpperCase(String str) {
        boolean result = true;
        for (char element : str.toCharArray()) {
            result &= Character.isUpperCase(element);
        }
        return result;
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
        xLines.add(image.getWidth());

        System.out.println("Total lines: " + rowsCount);
        System.out.println("Total columns: " + columnsCount);
        System.out.println(yLines);
        System.out.println(xLines);
    }

    /**
     * This is very useful method when debugging the code. Print the charecter as it is to the screen.
     * 111111
     * 111111
     * 11      11
     * 11      11
     * 11
     * 11
     * 11
     * 11
     * 11
     * 11
     * 11
     * 11
     * 11
     * 11
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


    /**
     * We are cutting the symbol rectangle from the whole image
     */
    private static BufferedImage preprocessLetter(int stx, int x, int startY, int endY, BufferedImage image) {
        final int maxPixels = 30;
//        Find top and buttom of the image and cut its size
        BufferedImage subimage = image.getSubimage(stx, startY, x - stx, endY - startY);

        int topY = 0;
        for (int y = 0; y < maxPixels; y++) {
            if (blackPixelInRow(y, 0, subimage.getWidth(), subimage)) {
                topY = y;
                break;
            }
        }

        int bottomY = subimage.getHeight() - 1;
        for (int y = bottomY; y > subimage.getHeight() - maxPixels; y--) {
            if (blackPixelInRow(y, 0, subimage.getWidth(), subimage)) {
                bottomY = y;
                break;
            }
        }

        return subimage.getSubimage(0, topY, subimage.getWidth(), bottomY - topY + 1);
    }

    private static void writeImageToFile(int stx, int x, int startY, int endY, BufferedImage image, String
            recognizedNumber) {
        BufferedImage subimage = image.getSubimage(stx, startY, x - stx, endY - startY);
        File outputfile = new File("alphabet/" + recognizedNumber + ".png");
        try {
            outputfile.createNewFile();
            ImageIO.write(subimage, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int recognizeNumber(int startX, int endX, int startY, int endY, BufferedImage image) {
//        print for debug mode the number
//        printSymbolToScreen(startX, startY, endX, endY, image);
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
        int count = 0;
        switch (sum) {
            case 4:
                return ".";
            case 48:
                // c z
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 8) {
                    return "c";
                } else if(count == 6) {
                    return "z";
                }
            case 76:
                //d b A
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 8) {
                    return "d";
                }
                if (count == 4) {
                    return "A";
                } else if (count == 18) {
                    return "b";
                }
            case 96:
                //R
                return "B";
            case 88:
                return "D";
            case 64:
                // e a
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 8) {
                    return "e";
                } else if (count == 4){
                    return "a";
                }
            case 84:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 10) {
                    return "g";
                } else if (count == 18) {
                    return "F";
                }

            case 68:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 18) {
                    return "h";
                } else if (count == 8) {
                    return "S";
                }
            case 60:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 18) {
                    return "k";
                } else if (count == 14){
                    return "C";
                }
            case 36:
                // l=18 t=14
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 18) {
                    return "l";
                } else if(count == 14) {
                    return "t";
                }
            case 52:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 18) {
                    return "L";
                } else if(count == 12) {
                    return "ry";
                }
            case 80:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 12) {
                    return "m";
                } else if (count == 18) {
                    return "E";
                }

            case 112:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 18) {
                    return "M";
                }
            case 56:
                // n o u
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 8) {
                    return "o";
                } else if (count == 12) {
                    return "n";
                } else if (count == 10) {
                    return "u";
                }
            case 72:
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 8) {
                    return "w";
                } else if (count == 2) {
                    return "p";
                } else if (count == 18) {
                    return "K";
                }

            case 28:
                // r i
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 12) {
                    return "r";
                } else if (count == 14){
                    return "i";
                }
            case 40:
                // f v s
                count = getBlackPixelCountInColumn(startX, startY, endY, image);
                if (count == 16) {
                    if (getBlackPixelCountInColumn(startX+5, startY, endY, image) == 4) {
                        return "p";
                    } else if (getBlackPixelCountInColumn(startX+5, startY, endY, image) == 0) {
                        return "f";
                    }
                } else if (count == 4) {
                    int count2 = getBlackPixelCountInColumn(startX + 3, startY, endY, image);
                    if (count2 == 4){
                        return "v";
                    }
                    else if(count2 == 6) {
                            if (getBlackPixelCountInColumn(startX + 6, startY, endY, image) == 4){
                            return "s";
                        }
                    }
                }
                break;
            case 92:
                return "ty";
            case 136:
                return "my";
            case 116:
                return "ky";
            case 128:
                return "Lv";

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

    private static boolean blackPixelInRow(int y, int startX, int endX, BufferedImage image) {
        for (int x = startX; x < endX; x++) {
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