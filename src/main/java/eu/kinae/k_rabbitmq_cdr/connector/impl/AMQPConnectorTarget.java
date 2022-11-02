package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.component.amqp.AMQPConnectionWriter;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AMQPConnectorTarget implements ConnectorTarget {

    private final AMQPConnection connection;

    public AMQPConnectorTarget(KParameters parameters) {
        connection = new AMQPConnection(parameters.targetURI());
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AMQP;
    }

    @Override
    public Target getDirectLinked(KParameters parameters, SharedStatus sharedStatus) {
        return new AMQPConnectionWriter(connection, parameters.targetQueue(), sharedStatus);
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionWriter target = new AMQPConnectionWriter(connection, parameters.targetQueue(), sharedStatus);
        return new SequentialComponentTarget(sharedQueue, target, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return IntStream.range(0, options.targetThread())
            .mapToObj(ignored -> createParallelComponent(sharedQueue, parameters, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));
    }

    private ParallelComponent createParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AMQPConnectionWriter writer = new AMQPConnectionWriter(connection, parameters.targetQueue(), sharedStatus);
        return new ParallelComponentTarget(sharedQueue, writer, options, sharedStatus);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

}
