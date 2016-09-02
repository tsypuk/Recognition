package smart.java.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smart.java.services.impl.ImagesProducerServiceImpl;

public class ImageLineCutterThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLineCutterThread.class);

    private String fileName;

    @Override
    public void run() {
        LOGGER.info("Processing file: {}", fileName);
        new ImagesProducerServiceImpl().splitImageToLinePictures(fileName);
    }

    public ImageLineCutterThread(String fileName) {
        this.fileName = fileName;
    }
}