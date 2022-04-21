package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.params.TransferType;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPComponentDirectLinked;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPConnection;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPParallelTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialSource;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPSequentialTarget;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPToAMQPConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(AMQPToAMQPConnector.class);

    public AMQPToAMQPConnector() {
    }

    @Override
    public void start(KParameters params, KOptions options) {
        if(params.transferType() == TransferType.DIRECT) {
            try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue());
                AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue())) {

                AMQPComponentDirectLinked directTransfer = new AMQPComponentDirectLinked(sConnection, tConnection, options);
                directTransfer.start();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        }

        if(params.transferType() == TransferType.BUFFER) {
            if(params.processType() == ProcessType.SEQUENTIAL) {
                SharedQueue list = new SharedQueue(ProcessType.SEQUENTIAL);
                KOptions sourceParams = new KOptions(options.maxMessage());

                try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue());
                    AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue())) {

                    AMQPSequentialSource source = new AMQPSequentialSource(sConnection, list, sourceParams);
                    AMQPSequentialTarget target = new AMQPSequentialTarget(tConnection, list);

                    source.start();
                    target.start();
                } catch(Exception e) {
                    logger.error("Unknown error, please report it", e);
                    throw new RuntimeException("Unknown error, please report it", e);
                }
            } else if(params.processType() == ProcessType.PARALLEL) {
                SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
                SharedStatus sharedStatus = new SharedStatus();
                KOptions sourceParams = new KOptions(options.maxMessage());

                try(AMQPConnection sConnection = new AMQPConnection(params.sourceURI(), params.sourceQueue());
                    AMQPConnection tConnection = new AMQPConnection(params.targetURI(), params.targetQueue())) {

                    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
                    List<Callable<Long>> callables = IntStream.range(0, 3)
                            .mapToObj(integer -> new AMQPParallelTarget(tConnection, sharedQueue, sharedStatus))
                            .collect(Collectors.toCollection(ArrayList::new));
                    callables.add(new AMQPParallelSource(sConnection, sharedQueue, sharedStatus, sourceParams));
                    fixedThreadPool.invokeAll(callables);

                    fixedThreadPool.shutdown();
                } catch(Exception e) {
                    logger.error("Unknown error, please report it", e);
                    throw new RuntimeException("Unknown error, please report it", e);
                }
            }
        }
    }
}
