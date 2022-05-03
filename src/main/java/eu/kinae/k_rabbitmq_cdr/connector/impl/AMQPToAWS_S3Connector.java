package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.connector.Connector;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialSource;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ParallelTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3SequentialTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3Writer;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

public class AMQPToAWS_S3Connector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(AMQPToAWS_S3Connector.class);

    public AMQPToAWS_S3Connector() {
    }

    @Override
    public void start(KParameters params, KOptions options) {
        if(params.processType() == ProcessType.SEQUENTIAL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);

            try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue());
                S3Client s3Client = AWS_S3ClientBuilder.build()) {

                AWS_S3Writer writer = new AWS_S3Writer(s3Client, "bucket", "prefix"); // params
                AMQPSequentialSource source = new AMQPSequentialSource(sConnection, sharedQueue, options);
                AWS_S3SequentialTarget target = new AWS_S3SequentialTarget(sharedQueue, writer);

                source.start();
                target.start();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        } else if(params.processType() == ProcessType.PARALLEL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
            SharedStatus sharedStatus = new SharedStatus();

            try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue());
                S3Client s3Client = AWS_S3ClientBuilder.build()) {

                AWS_S3Writer writer = new AWS_S3Writer(s3Client, "bucket", "prefix"); // params
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
                List<Callable<Long>> callables = IntStream.range(0, options.threads())
                        .mapToObj(ignored -> new AWS_S3ParallelTarget(sharedQueue, writer, sharedStatus))
                        .collect(Collectors.toCollection(ArrayList::new));
                callables.add(new AMQPParallelSource(sConnection, sharedQueue, sharedStatus, options));
                fixedThreadPool.invokeAll(callables);

                fixedThreadPool.shutdown();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        }
    }
}
