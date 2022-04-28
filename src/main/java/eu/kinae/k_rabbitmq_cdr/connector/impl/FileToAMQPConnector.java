package eu.kinae.k_rabbitmq_cdr.connector.impl;

import java.nio.file.Path;
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
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileReader;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileSequentialSource;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileToAMQPConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(FileToAMQPConnector.class);

    public FileToAMQPConnector() {
    }

    @Override
    public void start(KParameters params, KOptions options) {
        if(params.processType() == ProcessType.SEQUENTIAL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);

            try(AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue())) {

                FileSequentialSource source = new FileSequentialSource(new FileReader(Path.of(params.input())), sharedQueue, options);
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

            try(AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue())) {

                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
                List<Callable<Long>> callables = IntStream.range(0, options.threads())
                        .mapToObj(ignored -> new AMQPParallelTarget(sharedQueue, tConnection, sharedStatus))
                        .collect(Collectors.toCollection(ArrayList::new));
                callables.add(new FileParallelSource(new FileReader(Path.of(params.input())), sharedQueue, sharedStatus, options));
                fixedThreadPool.invokeAll(callables);

                fixedThreadPool.shutdown();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        }
    }
}
