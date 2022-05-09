package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ParallelTarget;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3SequentialTarget;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorTarget;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ConnectorTarget implements ConnectorTarget {

    public AWS_S3ConnectorTarget() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }

    @Override
    public Target getDirectLinked(KParameters parameters) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        return new AWS_S3Writer(s3Client, parameters.bucket(), parameters.bucket());
    }

    @Override
    public AbstractComponentTarget getSequentialComponent(SharedQueue sharedQueue, KParameters parameters) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Writer writer = new AWS_S3Writer(s3Client, parameters.bucket(), parameters.bucket());
        return new AWS_S3SequentialTarget(sharedQueue, writer);
    }

    @Override
    public ParallelComponents getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Writer writer = new AWS_S3Writer(s3Client, parameters.bucket(), parameters.bucket());
        return IntStream.range(0, options.threads())
                .mapToObj(ignored -> new AWS_S3ParallelTarget(sharedQueue, writer, sharedStatus))
                .collect(Collectors.toCollection(ParallelComponents::new));
    }
}
