package eu.kinae.k_rabbitmq_cdr.protocol.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.rabbitmq.client.AMQP;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import eu.kinae.k_rabbitmq_cdr.utils.KMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader implements Source {

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    private final Iterator<File> it;

    public FileReader(Path path) {
        logger.info("listing files ...");
        Pattern p = Pattern.compile(".*[^.json]$");
        File[] files = path.toFile().listFiles(it -> p.matcher(it.getName()).matches());
        //        File[] files = Constant.PROJECT_TMPDIR.toFile().listFiles(it -> p.matcher(it.getName()).matches());
        if(files == null) {
            throw new RuntimeException("pathname does not denote a directory");
        }

        ;
        logger.info("number of files listed : {}", files.length);
        it = Arrays.stream(files).sorted(Comparator.comparing(it -> Long.valueOf(it.getName().substring(Constant.FILE_PREFIX.length())))).iterator();
    }

    @Override
    public KMessage pop() throws Exception {
        if(!it.hasNext())
            return null;
        File file = it.next();
        byte[] body = Files.readAllBytes(file.toPath());
        AMQP.BasicProperties props = CustomObjectMapper.om.readValue(new File(file.getPath() + Constant.FILE_PROPERTIES_SUFFIX), AMQP.BasicProperties.class);
        return new KMessage(props, body);
    }

    @Override
    public void close() throws Exception {

    }
}