package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.Map;
import java.util.Optional;

import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;

public abstract class ConnectorFactory {

    public static final Map<SupportedType, Class<? extends ConnectorSource>> connectorSources =
            Map.of(
                    SupportedType.AMQP, AMQPConnectorSource.class,
                    SupportedType.FILE, FileConnectorSource.class,
                    SupportedType.AWS_S3, AWS_S3ConnectorSource.class
                  );

    public static final Map<SupportedType, Class<? extends ConnectorTarget>> connectorTargets =
            Map.of(
                    SupportedType.AMQP, AMQPConnectorTarget.class,
                    SupportedType.FILE, FileConnectorTarget.class,
                    SupportedType.AWS_S3, AWS_S3ConnectorTarget.class
                  );

    private ConnectorFactory() {
    }

    public static Optional<Connector> newConnector(SupportedType sType, SupportedType tType) throws Exception {
        Class<? extends ConnectorSource> source = connectorSources.get(sType);
        Class<? extends ConnectorTarget> target = connectorTargets.get(tType);
        if(source == null || target == null)
            return Optional.empty();

        ConnectorSource connectorSource = source.getConstructor().newInstance();
        ConnectorTarget connectorTarget = target.getConstructor().newInstance();
        return Optional.of(new Connector(connectorSource, connectorTarget));
    }

}
