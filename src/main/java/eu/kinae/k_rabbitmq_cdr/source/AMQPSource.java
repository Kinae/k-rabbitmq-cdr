package eu.kinae.k_rabbitmq_cdr.source;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.LongString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPSource implements Source {

    private final Path tmpdir;
    private final String queue;
    private final ConnectionFactory factory;
    private final ObjectMapper om = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(AMQPSource.class);

    public AMQPSource(Path tmpdir, String uri, String queue) {
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
        this.om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        SimpleModule amqpModule = new SimpleModule("AMQPModule");
        amqpModule.addSerializer(LongString.class, new ObjectIdSerializer());
        om.registerModule(amqpModule);

    }

    private static class ObjectIdSerializer extends JsonSerializer<LongString> {

        @Override
        public void serialize(LongString value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(new String(value.getBytes()));
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
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
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
        if (response == null) {
            logger.debug("no more message to get");
            return count;
        } else {
            if(count == 0) logger.info("estimate number of messages : {}", (response.getMessageCount() + 1));

            String lPrefix = "/" + prefix + response.getEnvelope().getDeliveryTag();
            Path papath = Path.of(tmpdir + lPrefix);
            Path path = Files.createFile(papath);
//            path.toFile().deleteOnExit();
            Files.writeString(path, new String(response.getBody()), StandardOpenOption.TRUNCATE_EXISTING);

            path = Files.createFile(Path.of(tmpdir + lPrefix + "_props.json"));
//            path.toFile().deleteOnExit();
            om.writeValue(path.toFile(), response.getProps());
        }

        return getMessage(channel, prefix, count + 1);
    }

}
