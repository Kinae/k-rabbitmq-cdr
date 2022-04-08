package eu.kinae.k_rabbitmq_cdr.protocol.amqp;

import java.io.IOException;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;
import eu.kinae.k_rabbitmq_cdr.protocol.Engine;

abstract class AMQPComponent extends Engine implements Component {

    private final AMQPConnection connection;

    protected AMQPComponent(String uri, String queue) throws Exception {
        this.connection = new AMQPConnection(uri, queue);
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    protected void close() {
        connection.close();
    }

    public GetResponse basicGet() throws IOException {
        return connection.basicGet();
    }

    public void basicPublish(GetResponse response) throws IOException {
        connection.basicPublish(response);
    }

}
