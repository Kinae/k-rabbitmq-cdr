package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.Map;
import java.util.Set;

import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AMQPConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.AWS_S3ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorSource;
import eu.kinae.k_rabbitmq_cdr.connector.impl.FileConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import software.amazon.awssdk.utils.Pair;

public abstract class ConnectorFactory {

    public static final Set<Pair<SupportedType, SupportedType>> connectorsAvailable =
            Set.of(
                    Pair.of(SupportedType.AMQP, SupportedType.AMQP),
                    Pair.of(SupportedType.AMQP, SupportedType.FILE),
                    Pair.of(SupportedType.AMQP, SupportedType.AWS_S3),
                    Pair.of(SupportedType.FILE, SupportedType.AMQP),
                    Pair.of(SupportedType.AWS_S3, SupportedType.AMQP)
                  );

    static final Map<SupportedType, Class<? extends ConnectorSource>> connectorSources =
            Map.of(
                    SupportedType.AMQP, AMQPConnectorSource.class,
                    SupportedType.FILE, FileConnectorSource.class,
                    SupportedType.AWS_S3, AWS_S3ConnectorSource.class
                  );

    static final Map<SupportedType, Class<? extends ConnectorTarget>> connectorTargets =
            Map.of(
                    SupportedType.AMQP, AMQPConnectorTarget.class,
                    SupportedType.FILE, FileConnectorTarget.class,
                    SupportedType.AWS_S3, AWS_S3ConnectorTarget.class
                  );

    private ConnectorFactory() {
    }

    public static Connector newConnector(KParameters parameters, KOptions options) throws Exception {
        Class<? extends ConnectorSource> source = connectorSources.get(parameters.sourceType());
        Class<? extends ConnectorTarget> target = connectorTargets.get(parameters.targetType());
        if(source == null || target == null) {
            return null;
        }

        ConnectorSource connectorSource = source.getConstructor(KParameters.class, KOptions.class).newInstance(parameters, options);
        ConnectorTarget connectorTarget = target.getConstructor(KParameters.class).newInstance(parameters);
        return new Connector(connectorSource, connectorTarget);
    }

}
