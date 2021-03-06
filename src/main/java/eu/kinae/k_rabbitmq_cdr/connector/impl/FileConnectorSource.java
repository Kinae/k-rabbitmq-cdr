package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.file.FileParallelSource;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.component.file.FileSequentialSource;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileConnectorSource implements ConnectorSource {

    public FileConnectorSource() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.FILE;
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return new FileReader(Path.of(parameters.directory()), options, sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileReader reader = new FileReader(Path.of(parameters.directory()), options, sharedStatus);
        return new FileSequentialSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileReader reader = new FileReader(Path.of(parameters.directory()), options, sharedStatus);
        return new FileParallelSource(reader, sharedQueue, options, sharedStatus);
    }
}
