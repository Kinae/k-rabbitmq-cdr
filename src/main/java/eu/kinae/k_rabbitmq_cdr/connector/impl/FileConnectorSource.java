package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.component.file.FileReaderInfo;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class FileConnectorSource implements ConnectorSource {

    private final FileReaderInfo fileReaderInfo;

    public FileConnectorSource(KParameters parameters, KOptions options) {
        fileReaderInfo = new FileReaderInfo(Path.of(parameters.directory()), options);
    }

    public FileConnectorSource(FileReaderInfo fileReaderInfo) {
        this.fileReaderInfo = fileReaderInfo;
    }

    @Override
    public long countMessages(KParameters parameters) {
        return fileReaderInfo.countMessages();
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.FILE;
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return new FileReader(fileReaderInfo, sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        FileReader reader = new FileReader(fileReaderInfo, sharedStatus);
        return new SequentialComponentSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return IntStream.range(0, options.sourceThread())
            .mapToObj(ignored -> createParallelComponent(sharedQueue, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));
    }

    private ParallelComponent createParallelComponent(SharedQueue sharedQueue, KOptions options, SharedStatus sharedStatus) {
        FileReader reader = new FileReader(fileReaderInfo, sharedStatus);
        return new ParallelComponentSource(reader, sharedQueue, options, sharedStatus);
    }

    @Override
    public void close() {
    }
}
