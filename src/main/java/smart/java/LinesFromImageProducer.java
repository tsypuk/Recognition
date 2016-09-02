package smart.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import smart.java.thread.ImageLineCutterThread;

public class LinesFromImageProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinesFromImageProducer.class);
    public static final String IMAGES_FOLDER = "images/";
    private static final int N_THREADS = 10;

    public static void main(String[] args) throws IOException {
        LOGGER.info("hello");

        File imagesDirectory = new File(IMAGES_FOLDER);

        ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        LOGGER.info("Files processed: {}",
                Arrays.stream(imagesDirectory.list())
                        .filter(fileName -> fileName.endsWith(".png"))
                        .map(fileName -> IMAGES_FOLDER.concat(fileName))
                        .peek(fileName -> LOGGER.info("Submitting task to process file: {}", fileName))
                        .peek(fileName -> executorService.submit(new ImageLineCutterThread(fileName))).count());
        executorService.shutdown();
    }
}