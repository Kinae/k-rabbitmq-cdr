package eu.kinae.k_rabbitmq_cdr.connector.impl;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.SequentialComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.component.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.connector.ConnectorSource;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.SupportedType;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ConnectorSource implements ConnectorSource {

    public AWS_S3ConnectorSource() {
    }

    @Override
    public SupportedType getSupportedType() {
        return SupportedType.AWS_S3;
    }

    @Override
    public Source getDirectLinked(KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        return new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options, sharedStatus);
    }

    @Override
    public AbstractComponentSource getSequentialComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Reader reader = new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options, sharedStatus);
        return new SequentialComponentSource(reader, sharedQueue, options);
    }

    @Override
    public ParallelComponent getParallelComponent(SharedQueue sharedQueue, KParameters parameters, KOptions options, SharedStatus sharedStatus) {
        S3Client s3Client = AWS_S3ClientBuilder.build(parameters);
        AWS_S3Reader reader = new AWS_S3Reader(s3Client, parameters.bucket(), parameters.prefix(), options, sharedStatus);
        return new ParallelComponentSource(reader, sharedQueue, options, sharedStatus);
    }
}
