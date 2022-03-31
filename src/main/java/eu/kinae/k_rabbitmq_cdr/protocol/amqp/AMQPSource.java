package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;

public class AMQPSource extends AMQPComponent implements Source {

    private final String queue;

    public AMQPSource(String uri, String queue) {
        super(uri);
        this.queue = queue;
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
