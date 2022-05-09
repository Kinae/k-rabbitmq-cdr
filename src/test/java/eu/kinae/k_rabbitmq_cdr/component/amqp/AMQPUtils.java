package eu.kinae.k_rabbitmq_cdr.component.amqp;

import org.testcontainers.containers.RabbitMQContainer;

public class AMQPUtils {

    public static String buildAMQPURI(RabbitMQContainer rabbitmq) {
        return String.format("amqp://%s:%s@%s:%d/%s", rabbitmq.getAdminUsername(), rabbitmq.getAdminPassword(), rabbitmq.getHost(), rabbitmq.getFirstMappedPort(), "%2F");
    }

}
