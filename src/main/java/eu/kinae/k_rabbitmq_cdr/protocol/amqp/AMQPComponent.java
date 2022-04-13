package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import eu.kinae.k_rabbitmq_cdr.protocol.Engine;

abstract class AMQPComponent extends Engine implements Component, AutoCloseable {

    private final AMQPConnection connection;

    protected AMQPComponent(String uri, String queue) throws Exception {
        this.connection = new AMQPConnection(uri, queue);
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    public GetResponse basicGet() throws IOException {
        return connection.basicGet();
    }

    public void basicPublish(AMQP.BasicProperties properties, byte[] body) throws IOException {
        connection.basicPublish(properties, body);
    }

    @Override
    public void close() {
        connection.close();
    }
}
