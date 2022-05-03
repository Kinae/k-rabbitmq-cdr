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
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ClientBuilder;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3ParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3Reader;
import eu.kinae.k_rabbitmq_cdr.protocol.aws.AWS_S3SequentialSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

public class AWS_S3ToAMQPConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(AWS_S3ToAMQPConnector.class);

    public AWS_S3ToAMQPConnector() {
    }

    @Override
    public void start(KParameters params, KOptions options) {
        if(params.processType() == ProcessType.SEQUENTIAL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);

            try(AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue());
                S3Client s3Client = AWS_S3ClientBuilder.build()) {

                AWS_S3Reader reader = new AWS_S3Reader(s3Client, "bucket", "prefix", options);
                AWS_S3SequentialSource source = new AWS_S3SequentialSource(reader, sharedQueue, options);
                AMQPSequentialTarget target = new AMQPSequentialTarget(sharedQueue, tConnection);

                source.start();
                target.start();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        } else if(params.processType() == ProcessType.PARALLEL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
            SharedStatus sharedStatus = new SharedStatus();

            try(AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue());
                S3Client s3Client = AWS_S3ClientBuilder.build()) {

                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
                List<Callable<Long>> callables = IntStream.range(0, options.threads())
                        .mapToObj(ignored -> new AMQPParallelTarget(sharedQueue, tConnection, sharedStatus))
                        .collect(Collectors.toCollection(ArrayList::new));
                AWS_S3Reader reader = new AWS_S3Reader(s3Client, "bucket", "prefix", options);
                callables.add(new AWS_S3ParallelSource(reader, sharedQueue, sharedStatus, options));
                fixedThreadPool.invokeAll(callables);

                fixedThreadPool.shutdown();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        }
    }
}
