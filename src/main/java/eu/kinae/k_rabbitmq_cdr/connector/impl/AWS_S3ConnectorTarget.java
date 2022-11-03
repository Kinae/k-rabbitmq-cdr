package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ConnectorTarget implements ConnectorTarget {

    private final S3Client s3Client;

    public AWS_S3ConnectorTarget(KParameters parameters) {
        s3Client = AWS_S3ClientBuilder.build(parameters);
    }

    public AWS_S3ConnectorTarget(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }

    @Override
    public Target getDirectLinked(KParameters parameters, SharedStatus sharedStatus) {
        return new AWS_S3Writer(s3Client, parameters.bucket(), parameters.prefix(), sharedStatus);
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AWS_S3Writer writer = new AWS_S3Writer(s3Client, parameters.bucket(), parameters.prefix(), sharedStatus);
        return new SequentialComponentTarget(sharedQueue, writer, options);
    }

    @Override
    public ParallelComponents getParallelComponents(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        AWS_S3Writer writer = new AWS_S3Writer(s3Client, parameters.bucket(), parameters.prefix(), sharedStatus);
        return IntStream.range(0, options.targetThread())
            .mapToObj(ignored -> new ParallelComponentTarget(sharedQueue, writer, options, sharedStatus))
            .collect(Collectors.toCollection(ParallelComponents::new));
    }

    @Override
    public void close() throws Exception {
        s3Client.close();
    }
}
