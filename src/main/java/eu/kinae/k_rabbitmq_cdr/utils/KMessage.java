package eu.kinae.k_rabbitmq_cdr.utils;

import java.io.Serializable;

import com.rabbitmq.client.AMQP;

public record KMessage(AMQP.BasicProperties properties, byte[] body, long messageCount) implements Serializable {

    public KMessage(String body) {
        this(null, body.getBytes(), 0);
    }

    public KMessage(byte[] body) {
        this(null, body, 0);
    }
}
