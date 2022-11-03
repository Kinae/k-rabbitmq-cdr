package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionReader;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectorSource implements ConnectorSource {

    private final AMQPConnection connection;

    public AMQPConnectorSource(KParameters parameters, KOptions options) {
        connection = new AMQPConnection(parameters.sourceURI());
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    public long countMessages(KParameters parameters) {
       return connection.countMessages(parameters.sourceQueue());
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return new AMQPConnectionReader(connection, parameters.sourceQueue(), sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionReader source = new AMQPConnectionReader(connection, parameters.sourceQueue(), sharedStatus);
        return new SequentialComponentSource(source, sharedQueue, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return IntStream.range(0, options.sourceThread())
            .mapToObj(ignored -> createParallelComponent(sharedQueue, parameters, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));
    }

    private ParallelComponent createParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionReader source = new AMQPConnectionReader(connection, parameters.sourceQueue(), sharedStatus);
        return new ParallelComponentSource(source, sharedQueue, options, sharedStatus);
    }

    @Override
    public void close() {
        connection.close();
    }
}
