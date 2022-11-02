package eu.kinae.k_rabbitmq_cdr.component.file;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReaderInfo {

    private final BlockingQueue<File> queue;
    private final Logger logger = LoggerFactory.getLogger(FileReaderInfo.class);

    public FileReaderInfo(Path path, KOptions options) {
        logger.info("listing files in {}", path);
        Pattern p = Pattern.compile(".*[^.json]$");
        File[] files = path.toFile().listFiles(it -> p.matcher(it.getName()).matches());
        if(files == null) {
            logger.error("pathname does not denote a directory");
            throw new RuntimeException("pathname does not denote a directory");
        }

        logger.info("number of files listed : {}", files.length);
        Stream<File> fileStream = Arrays.stream(files);
        if(options.sorted()) {
            logger.info("sorting filename by ascending number");
            fileStream = fileStream.sorted(Comparator.comparing(it -> Constant.extractDeliveryTagFromKey(it.getName())));
        }
        queue = fileStream.collect(Collectors.toCollection(LinkedBlockingQueue::new));
    }

    public long countMessages() {
        return queue.size();
    }

    public File pop() throws Exception {
        return queue.poll(500, TimeUnit.MILLISECONDS);
    }


}
