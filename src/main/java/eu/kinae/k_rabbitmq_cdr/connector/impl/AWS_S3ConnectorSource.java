package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ReaderInfo;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;

public class AWS_S3ConnectorSource implements ConnectorSource {

    private final AWS_S3ReaderInfo awsS3ReaderInfo;

    public AWS_S3ConnectorSource(KParameters parameters, KOptions options) {
        awsS3ReaderInfo = new AWS_S3ReaderInfo(parameters, options);
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }

    @Override
    public long countMessages(KParameters parameters) {
        return awsS3ReaderInfo.countMessages();
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return new AWS_S3Reader(awsS3ReaderInfo, sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AWS_S3Reader reader = new AWS_S3Reader(awsS3ReaderInfo, sharedStatus);
        return new SequentialComponentSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        return IntStream.range(0, options.sourceThread())
            .mapToObj(ignored -> createParallelComponent(sharedQueue, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));

    }

    private ParallelComponent createParallelComponent(SharedQueue sharedQueue, KOptions options, SharedStatus sharedStatus) {
        AWS_S3Reader reader = new AWS_S3Reader(awsS3ReaderInfo, sharedStatus);
        return new ParallelComponentSource(reader, sharedQueue, options, sharedStatus);
    }


    @Override
    public void close() {
        awsS3ReaderInfo.close();
    }

}
