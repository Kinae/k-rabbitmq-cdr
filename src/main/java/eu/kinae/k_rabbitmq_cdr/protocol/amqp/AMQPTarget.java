package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.regex.Pattern;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPTarget extends AMQPComponent implements Target {

    private final String queue;
    private final ConnectionFactory factory;
    private final Logger logger = LoggerFactory.getLogger(AMQPTarget.class);

    public AMQPTarget(String uri, String queue) {
        logger.info("connection validation ...");
        this.queue = queue;
        this.factory = new ConnectionFactory();
        try {
            this.factory.setUri(uri);
            logger.info("connection validated");
        } catch(URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    @Override
    public boolean run() throws Exception {
        logger.info("""
                            starting connection on ...
                              Host : {}
                              Port : {}
                              Vhost : {}
                              Username : {}
                            """, factory.getHost(), factory.getPort(), factory.getVirtualHost(), factory.getUsername());
        try(Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            logger.info("connected and channel created");

            logger.info("listing files ...");
            Pattern p = Pattern.compile(".*[^.json]$");
            File[] files = Constant.PROJECT_TMPDIR.toFile().listFiles(it -> p.matcher(it.getName()).matches());
            if(files == null) {
                logger.warn("no files were listed !");
                return false;
            }
            logger.info("number of files listed : {}", files.length);
            for(File file : files) {
                byte[] body = Files.readAllBytes(file.toPath());
                AMQP.BasicProperties props = CustomObjectMapper.om.readValue(new File(file.getPath() + Constant.FILE_PROPERTIES_SUFFIX), AMQP.BasicProperties.class);
                channel.basicPublish("", queue, false, false, props, body);
            }
        }

        return false;
    }

}
