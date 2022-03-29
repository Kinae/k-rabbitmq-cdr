package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPSource extends AMQPComponent implements Source {

    private final String queue;
    private final ConnectionFactory factory;
    private final Logger logger = LoggerFactory.getLogger(AMQPSource.class);

    public AMQPSource(String uri, String queue) {
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

            String prefix = queue + "_";
            logger.debug("prefixing message with '{}'", prefix);

            logger.info("retrieving message from '{}' ...", queue);
            long start = System.currentTimeMillis();
            int count = getMessage(channel, prefix, 0);
            long end = System.currentTimeMillis();
            logger.info("message retrieved : {} in {}ms", count, (end - start));

            return count > 0;
        }

    }

    private int getMessage(Channel channel, String prefix, int count) throws IOException {
        GetResponse response = channel.basicGet(queue, false);
        if(response == null) {
            logger.debug("no more message to get");
            return count;
        } else {
            if(count == 0)
                logger.info("estimate number of messages : {}", (response.getMessageCount() + 1));

            String filename = prefix + response.getEnvelope().getDeliveryTag();
            Path path = Files.createFile(Path.of(Constant.PROJECT_TMPDIR.toString(), filename));
            path.toFile().deleteOnExit();
            Files.writeString(path, new String(response.getBody()), StandardOpenOption.TRUNCATE_EXISTING);

            path = Files.createFile(Path.of(Constant.PROJECT_TMPDIR.toString(), filename + Constant.FILE_PROPERTIES_SUFFIX));
            path.toFile().deleteOnExit();
            CustomObjectMapper.om.writeValue(path.toFile(), response.getProps());
        }

        return getMessage(channel, prefix, count + 1);
    }

}
