package eu.kinae.k_rabbitmq_cdr.utils;

import java.io.Serializable;
import java.util.Arrays;

import com.rabbitmq.client.AMQP;

public record KMessage(AMQP.BasicProperties properties, byte[] body, long messageCount, long deliveryTag) implements Serializable {

    public KMessage(String body) {
        this(null, body.getBytes(), 0, 0);
    }

    public KMessage(byte[] body) {
        this(null, body, 0, 0);
    }

    public KMessage(AMQP.BasicProperties properties, byte[] body) {
        this(properties, body, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;
        KMessage message = (KMessage) o;
        return Arrays.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(body);
    }
}
