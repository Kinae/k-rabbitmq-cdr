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
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialSource;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileParallelTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileSequentialTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.file.FileWriter;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPToFileConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(AMQPToFileConnector.class);

    public AMQPToFileConnector() {
    }

    @Override
    public void start(KParameters params, KOptions options) {
        if(params.processType() == ProcessType.SEQUENTIAL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);

            try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue())) {

                AMQPSequentialSource source = new AMQPSequentialSource(sConnection, sharedQueue, options);
                FileSequentialTarget target = new FileSequentialTarget(sharedQueue, new FileWriter(Path.of(params.output())));

                source.start();
                target.start();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        } else if(params.processType() == ProcessType.PARALLEL) {
            SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
            SharedStatus sharedStatus = new SharedStatus();

            try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue())) {

                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
                List<Callable<Long>> callables = IntStream.range(0, options.threads())
                        .mapToObj(ignored -> new FileParallelTarget(sharedQueue, new FileWriter(Path.of(params.output())), sharedStatus))
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
