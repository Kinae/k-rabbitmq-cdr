package eu.kinae.k_rabbitmq_cdr.component.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader implements Source {

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    private long total;
    private final Iterator<File> it;
    private final SharedStatus sharedStatus;

    public FileReader(Path path, KOptions options) {
        this(path, options, null);
    }

    public FileReader(Path path, KOptions options, SharedStatus sharedStatus) {
        this.sharedStatus = sharedStatus;

        logger.info("listing files in {}", path);
        Pattern p = Pattern.compile(".*[^.json]$");
        File[] files = path.toFile().listFiles(it -> p.matcher(it.getName()).matches());
        if(files == null) {
            throw new RuntimeException("pathname does not denote a directory");
        }

        total = files.length;
        logger.info("number of files listed : {}", total);
        if(sharedStatus != null) {
            sharedStatus.setTotal(files.length);
        }
        if(options.sorted()) {
            logger.info("sorting filename by ascending number");
            it = Arrays.stream(files).sorted(Comparator.comparing(it -> Constant.extractDeliveryTagFromKey(it.getName()))).iterator();
        } else {
            it = Arrays.stream(files).iterator();
        }
    }

    @Override
    public KMessage pop(KOptions options) throws Exception {
        if(!it.hasNext()) {
            return null;
        }
        if(sharedStatus != null) {
            sharedStatus.incrementRead();
        }

        File file = it.next();
        byte[] body = Files.readAllBytes(file.toPath());
        AMQP.BasicProperties props = null;
        if(!options.bodyOnly()) {
            props = CustomObjectMapper.om.readValue(new File(file.getPath() + Constant.FILE_PROPERTIES_SUFFIX), AMQP.BasicProperties.class);
        }
        long deliveryTag = Constant.extractDeliveryTagFromKey(file.getName());
        return new KMessage(props, body, total--, deliveryTag);
    }

    @Override
    public void close() throws Exception {

    }
}
