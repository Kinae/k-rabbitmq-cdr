package eu.kinae.k_rabbitmq_cdr.target;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPTarget implements Target {

    private final Path tmpdir;
    private final String queue;
    private final ConnectionFactory factory;
    private final ObjectMapper om = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(AMQPTarget.class);

    public AMQPTarget(Path tmpdir, String uri, String queue) {
        logger.info("connection validation ...");
        this.queue = queue;
        this.factory = new ConnectionFactory();
        try {
            this.factory.setUri(uri);
            logger.info("connection validated");
        } catch (URISyntaxException e) {
            logger.error("Error URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
            throw new RuntimeException("Error AMQP URI syntax (e.g. amqp://admin:admin@localhost:5672/%2F)", e);
        } catch (Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }

        this.tmpdir = tmpdir;
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            logger.info("connected and channel created");

            Pattern p = Pattern.compile(".*[^.json]$");
            File[] files = tmpdir.toFile().listFiles(it -> p.matcher(it.getName()).matches());
            if(files == null) return false;
            for (File file : files) {
                byte[] body = Files.readAllBytes(file.toPath());
                AMQP.BasicProperties props = om.readValue(new File(file.getPath() + "_props.json"), AMQP.BasicProperties.class);
                channel.basicPublish("", queue, false, false, props, body);
            }
        }

        return false;
    }

}
