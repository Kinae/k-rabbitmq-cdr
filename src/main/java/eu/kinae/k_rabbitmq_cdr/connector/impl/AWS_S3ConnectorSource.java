package eu.kinae.k_rabbitmq_cdr.connector.impl;

import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3SequentialSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ConnectorSource implements ConnectorSource {

    public AWS_S3ConnectorSource() {
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        return new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Reader reader = new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options);
        return new AWS_S3SequentialSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Reader reader = new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options);
        return new AWS_S3ParallelSource(reader, sharedQueue, options, sharedStatus);
    }
}
