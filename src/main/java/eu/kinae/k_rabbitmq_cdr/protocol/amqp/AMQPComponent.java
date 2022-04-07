package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AMQPComponent implements Component {

    private final AMQPConnection connection;
    private final String queue;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AMQPComponent(String uri, String queue) throws Exception {
        this.connection = new AMQPConnection(uri);
        this.queue = queue;
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    protected abstract void start();

    protected void close() {
        connection.close();
    }

    public GetResponse basicGet() throws IOException {
        return connection.basicGet(queue);
    }

    public void basicPublish(GetResponse response) throws IOException {
        connection.basicPublish(queue, response);
    }

}
