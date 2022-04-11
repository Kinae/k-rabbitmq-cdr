package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Optional;

import eu.kinae.k_rabbitmq_cdr.params.SupportedType;

public abstract class ConnectorFactory {

    public static final Map<SimpleEntry<SupportedType, SupportedType>, Class<? extends Connector>> knownConnectors =
            Map.of(
                    new SimpleEntry<>(SupportedType.AMQP, SupportedType.AMQP), AMQPToAMQPConnector.class
                  );

    private ConnectorFactory() {
    }

    public static Optional<Connector> newConnector(SupportedType sType, SupportedType tType) throws Exception {
        Class<? extends Connector> c = knownConnectors.get(new SimpleEntry<>(sType, tType));
        if(c == null)
            return Optional.empty();
        return Optional.of(c.getConstructor().newInstance());
    }

}
