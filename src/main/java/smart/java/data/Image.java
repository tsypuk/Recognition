package smart.java.data;

import java.awt.image.BufferedImage;

import static smart.java.services.impl.ImagesProducerServiceImpl.BLACK_FILTER_COLOR;

public class Image {
    private BufferedImage image;
    private int hashCode = 0;

    public Image(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return hashCode == image.hashCode;

    }

    @Override
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        } else {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    hashCode += (image.getRGB(x, y) < BLACK_FILTER_COLOR) ? x + y : 1;
                }
            }
            return hashCode;
        }
    }

}