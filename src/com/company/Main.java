package com.company;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Main {
    //    private static final int BLACK_FILTER_COLOUR = -11579569;
    private static final int BLACK_FILTER_COLOUR = -1157956;
    private static final int WHITE_COLOUR = -1;

    private static int width;
    private static int height;
    private static List<Integer> xLines = new ArrayList<>();
    private static List<Integer> yLines = new ArrayList<>();

    public static void main(String[] args) {
//	      Read file from FS
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("imm.png"));
            width = img.getWidth();
            height = img.getHeight();
//            readPixels(img);
            calculateLines(img);
            int previousX = 0;
            int previousY = 0;

//            for (Integer xLine : xLines) {
            for (Integer yLine : yLines) {
//                    printSymbolToScreen(previousX, previousY, xLine, yLine, img);
                readNumber(previousX, previousY, xLines.get(0), yLine, img);
                System.out.print("  :  ");
                readEnglish(xLines.get(0), previousY, xLines.get(1), yLine, img);
                System.out.print("  :  ");
                readEnglish(xLines.get(1), previousY, xLines.get(2), yLine, img);
                System.out.println();
                previousY = yLine;
            }
//                previousX = xLine;
//            }

//        Find first word
        } catch (IOException e) {
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
                // This is the end of number

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

    private static void calculateLines(BufferedImage image) {

        int offsetX = 308;
        int lineBaseColor = -1118482;
        int linesCount = 0;
        int columnsCount = 0;
        for (int y = 0; y < height; y++) {
            int pixelRGB = image.getRGB(offsetX, y);
//            System.out.println(pixelRGB);
            if (pixelRGB == lineBaseColor) {
                yLines.add(y);
                linesCount++;
//                System.out.println("Line Found" + ++linesCount);
            }
        }

        int offsetY = 1;
        for (int x = 0; x < width; x++) {
            int pixelRGB = image.getRGB(x, offsetY);
//            System.out.println(pixelRGB);
            if (pixelRGB == lineBaseColor) {
                xLines.add(x);
//                System.out.println("Column Found" + ++columnsCount);
                columnsCount++;
            }
        }

        System.out.println("Total lines: " + linesCount);
        System.out.println("Total columns: " + columnsCount);
        System.out.println(yLines);
        System.out.println(xLines);
    }

    private static void printSymbolToScreen(int startX, int startY, int endX, int endY, BufferedImage image) {
        String c = " ";
//        System.out.println("Processing unit: (x1=" + startX + ", y1=" + startY + " x2=" + endX + ",y2=" + endY + ")");
        for (int y = startY; y < endY; y++) {
            System.out.println("");
            for (int x = startX; x < endX; x++) {
                int pixel = image.getRGB(x, y);

                if (checkForBlackPixelWithCorrection(pixel)) {
                    pixel = -8;
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
//        List<Integer> integers = Arrays.asList(188, 104, 160, 161,176,193, 194, 126, 205);
//        if (!integers.contains(sum)) {
//            System.out.println(sum);
//        }
        switch (sum) {
            case 188:
                result = 0;
                break;
            case 104:
                result = 1;
                break;
            case 160:
                result = 2;
                break;
            case 161:
                result = 3;
                break;
            case 176:
                result = 4;
                break;
            case 193:
                result = 5;
                break;
            case 194:
                //6 or 9
                //double check
                if (getBlackPixelCountInColumn(startX, startY, endY, image) > 1) {
                    return 6;
                }
                return 9;
            case 126:
                result = 7;
                break;
            case 205:
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
//        List<Integer> integers = Arrays.asList(188, 104, 160, 161,176,193, 194, 126, 205);
//        if (!integers.contains(sum)) {
//            System.out.println(sum);
//        }
        switch (sum) {
            case 154:
                return "a";
            case 179:
            // b d
                int count = getBlackPixelCountInColumn(startX+3, startY, endY, image);
            if (count == 9) {
                return "b";
            } else if (count == 8)
                return "d";
            else if(count == 7) return "w";
            return "d";
            case 120:
                return "c";
            case 157:
                return "e";
            case 98:
                return "f";
            case 199:
                return "g";
            case 162:
                return "h";
            case 69:
                return "i";
            case 153:
                return "k";
            case 83:
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
            case 101:
                return "v";
            case 136:
                return "y";
            case 118:
                return "z";
            case 225:
                return "ty";
            case 326:
                return "my";
            case 188:
                return "A";
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
        int sum = 0;
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

    private static boolean checkForBlackPixelWithCorrection(int pixel) {
        return ((pixel != WHITE_COLOUR) && (pixel < BLACK_FILTER_COLOUR));
    }

}