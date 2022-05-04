package eu.kinae.k_rabbitmq_cdr.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.params.TransferType;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.protocol.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.protocol.ComponentDirectLinked;
import eu.kinae.k_rabbitmq_cdr.protocol.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.protocol.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConnectorSource connectorSource;
    private final ConnectorTarget connectorTarget;

    public Connector(ConnectorSource connectorSource, ConnectorTarget connectorTarget) {
        this.connectorSource = connectorSource;
        this.connectorTarget = connectorTarget;
    }

    public void start(KParameters parameters, KOptions options) {
        if(parameters.transferType() == TransferType.DIRECT) {
            direct(parameters, options);
        } else if(parameters.transferType() == TransferType.BUFFER) {
            if(parameters.processType() == ProcessType.SEQUENTIAL) {
                bufferSequential(parameters, options);
            } else {
                bufferParallel(parameters, options);
            }
        }
    }

    private void direct(KParameters parameters, KOptions options) {
        try(Source source = connectorSource.getDirectLinked(parameters, options); Target target = connectorTarget.getDirectLinked(parameters)) {
            ComponentDirectLinked directTransfer = new ComponentDirectLinked(source, target, options);
            directTransfer.start();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    private void bufferSequential(KParameters parameters, KOptions options) {
        SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(AbstractComponentSource source = connectorSource.getSequentialComponent(sharedQueue, parameters, options);
            AbstractComponentTarget target = connectorTarget.getSequentialComponent(sharedQueue, parameters)) {

            source.start();
            target.start();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }

    private void bufferParallel(KParameters parameters, KOptions options) {
        SharedStatus sharedStatus = new SharedStatus();
        SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        try(ParallelComponent source = connectorSource.getParallelComponent(sharedQueue, parameters, options, sharedStatus);
            ParallelComponents callables = connectorTarget.getParallelComponent(sharedQueue, parameters, options, sharedStatus)) {

            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
            callables.add(source);
            fixedThreadPool.invokeAll(callables);

            fixedThreadPool.shutdown();
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        }
    }
}
