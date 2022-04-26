package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWriter implements Target {

    private static final Logger logger = LoggerFactory.getLogger(FileWriter.class);

    private final Path path;

    public FileWriter(Path path) {
        this.path = path;
    }

    @Override
    public void push(KMessage message) throws Exception {
        String filename = Constant.FILE_PREFIX + message.deliveryTag();
        Path fileCreatedPath = Files.createFile(Path.of(path.toString(), filename));

        Files.writeString(fileCreatedPath, new String(message.body()), StandardOpenOption.TRUNCATE_EXISTING);
        CustomObjectMapper.om.writeValue((Path.of(fileCreatedPath + Constant.FILE_PROPERTIES_SUFFIX)).toFile(), message.properties());
    }

    @Override
    public void close() throws Exception {

    }
}
