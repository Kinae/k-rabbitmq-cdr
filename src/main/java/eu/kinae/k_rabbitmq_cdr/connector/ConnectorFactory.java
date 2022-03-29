package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Optional;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.protocol.Component;

public abstract class ConnectorFactory {

    private final Map<SimpleEntry<SupportedType, SupportedType>, Class<? extends Connector>> map = Map.of(
            new SimpleEntry<>(SupportedType.AMQP, SupportedType.AMQP), AMQPToAMQPConnector.class
                                                                                                         );

    private ConnectorFactory() {
    }

    public Optional<Connector> newConnector(Component source, Component target) throws Exception {
        Class<? extends Connector> c = map.get(new SimpleEntry<>(source.getSupportedType(), target.getSupportedType()));
        if(c == null) return Optional.empty();
        return Optional.of(c.getConstructor().newInstance());
    }

}
