package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.file.FileParallelTarget;
import eu.kinae.k_rabbitmq_cdr.component.file.FileSequentialTarget;
import eu.kinae.k_rabbitmq_cdr.component.file.FileWriter;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileConnectorTarget implements ConnectorTarget {

    public FileConnectorTarget() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.FILE;
    }

    @Override
    public Target getDirectLinked(KParameters parameters) {
        return new FileWriter(Path.of(parameters.directory()));
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters) {
        FileWriter writer = new FileWriter(Path.of(parameters.directory()));
        return new FileSequentialTarget(sharedQueue, writer);
    }

    @Override
    public ParallelComponents getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileWriter writer = new FileWriter(Path.of(parameters.directory()));
        return IntStream.range(0, options.threads())
                .mapToObj(ignored -> new FileParallelTarget(sharedQueue, writer, sharedStatus))
                .collect(Collectors.toCollection(ParallelComponents::new));
    }
}
