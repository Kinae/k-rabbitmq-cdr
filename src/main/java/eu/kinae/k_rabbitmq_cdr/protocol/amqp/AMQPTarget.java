package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.File;
import java.nio.file.Files;
import java.util.regex.Pattern;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.Constant;
import eu.kinae.k_rabbitmq_cdr.utils.CustomObjectMapper;

public class AMQPTarget extends AMQPComponent implements Target {

    private final String queue;

    public AMQPTarget(String uri, String queue) {
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
