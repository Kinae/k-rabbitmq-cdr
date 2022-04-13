package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.params.TransferType;
import eu.kinae.k_rabbitmq_cdr.protocol.amqp.AMQPComponentDirectLinked;
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
    public void start(KParameters parameters, KOptions options) {
        if(parameters.transferType() == TransferType.DIRECT) {
            KOptions sourceParams = new KOptions(options.maxMessage());
            try(AMQPComponentDirectLinked directTransfer = new AMQPComponentDirectLinked(parameters, sourceParams)) {
                directTransfer.start();
            } catch(Exception e) {
                logger.error("Unknown error, please report it", e);
                throw new RuntimeException("Unknown error, please report it", e);
            }
        }

        if(parameters.transferType() == TransferType.BUFFER) {
            if(parameters.processType() == ProcessType.SEQUENTIAL) {
                SharedQueue list = new SharedQueue(ProcessType.SEQUENTIAL);
                KOptions sourceParams = new KOptions(options.maxMessage());

                try {
                    AMQPSequentialSource source = new AMQPSequentialSource(parameters, list, sourceParams);
                    AMQPSequentialTarget target = new AMQPSequentialTarget(parameters, list);

                    source.start();
                    target.start();
                } catch(Exception e) {
                    logger.error("Unknown error, please report it", e);
                    throw new RuntimeException("Unknown error, please report it", e);
                }
            } else if(parameters.processType() == ProcessType.PARALLEL) {
                SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
                SharedStatus sharedStatus = new SharedStatus();
                KOptions sourceParams = new KOptions(options.maxMessage());

                try {
                    Thread producerThread = new Thread(new AMQPParallelSource(parameters, sharedQueue, sharedStatus, sourceParams));
                    producerThread.start();

                    for(int i = 0; i < 3; i++) {
                        Thread consumerThread = new Thread(new AMQPParallelTarget(parameters, sharedQueue, sharedStatus));
                        consumerThread.start();
                    }

                } catch(Exception e) {
                    logger.error("Unknown error, please report it", e);
                    throw new RuntimeException("Unknown error, please report it", e);
                }
            }
        }
    }
}
