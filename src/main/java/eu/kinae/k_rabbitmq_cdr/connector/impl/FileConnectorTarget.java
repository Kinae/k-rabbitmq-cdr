package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.file.FileWriter;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileConnectorTarget implements ConnectorTarget {

    private final Path path;

    public FileConnectorTarget(KParameters parameters) {
        path = Path.of(parameters.directory());
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.FILE;
    }

    @Override
    public Target getDirectLinked(KParameters parameters, SharedStatus sharedStatus) {
        return new FileWriter(path, sharedStatus);
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileWriter writer = new FileWriter(path, sharedStatus);
        return new SequentialComponentTarget(sharedQueue, writer, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileWriter writer = new FileWriter(path, sharedStatus);
        return IntStream.range(0, options.targetThread())
            .mapToObj(ignored -> new ParallelComponentTarget(sharedQueue, writer, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));
    }

    @Override
    public void close() throws Exception {

    }
}
