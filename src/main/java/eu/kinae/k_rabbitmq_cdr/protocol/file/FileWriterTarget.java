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

public class FileWriterTarget implements Target {

    private static final Logger logger = LoggerFactory.getLogger(FileWriterTarget.class);

    @Override
    public void push(KMessage message) throws Exception {
        String filename = Constant.FILE_PROPERTIES_PREFIX + message.deliveryTag();
        Path path = Files.createFile(Path.of(Constant.PROJECT_TMPDIR.toString(), filename));

        Files.writeString(path, new String(message.body()), StandardOpenOption.TRUNCATE_EXISTING);
        CustomObjectMapper.om.writeValue(path.toFile(), message.properties());
    }

    @Override
    public void close() throws Exception {

    }
}
