package eu.kinae.k_rabbitmq_cdr.component.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileWriter implements Target {

    private static final Logger logger = LoggerFactory.getLogger(FileWriter.class);

    private final Path path;
    private final SharedStatus sharedStatus;

    public FileWriter(Path path) {
        this(path, null);
    }

    public FileWriter(Path path, SharedStatus sharedStatus) {
        this.path = path;
        this.sharedStatus = sharedStatus;
        logger.info("writing files in {}", path);
    }

    @Override
    public void push(KMessage message, KOptions options) throws Exception {
        String filename = Constant.FILE_PREFIX + message.deliveryTag();
        Path fileCreatedPath = Files.createFile(Path.of(path.toString(), filename));
        Files.writeString(fileCreatedPath, new String(message.body()), StandardOpenOption.TRUNCATE_EXISTING);
        if(message.properties() != null) {
            CustomObjectMapper.om.writeValue((Path.of(fileCreatedPath + Constant.FILE_PROPERTIES_SUFFIX)).toFile(), message.properties());
        }
        if(sharedStatus != null) {
            sharedStatus.incrementWrite();
        }
    }

    @Override
    public void close() throws Exception {

    }
}
