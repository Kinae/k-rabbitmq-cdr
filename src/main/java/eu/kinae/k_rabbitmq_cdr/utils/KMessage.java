package eu.kinae.k_rabbitmq_cdr.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import com.rabbitmq.client.AMQP;

public record KMessage(AMQP.BasicProperties properties, byte[] body, long messageCount, long deliveryTag) implements Serializable {

    public KMessage(AMQP.BasicProperties properties, byte[] body) {
        this(properties, body, 0, 0);
    }

    public KMessage(AMQP.BasicProperties properties, byte[] body, long deliveryTag) {
        this(properties, body, 0, deliveryTag);
    }

    public KMessage(AMQP.BasicProperties properties, String body, long deliveryTag) {
        this(properties, body.getBytes(), 0, deliveryTag);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        KMessage message = (KMessage) o;
        return Objects.equals(properties, message.properties) && Arrays.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(properties);
        result = 31 * result + Arrays.hashCode(body);
        return result;
    }

}
