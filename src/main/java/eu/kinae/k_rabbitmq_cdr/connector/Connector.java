package eu.kinae.k_rabbitmq_cdr.connector;

import java.io.Closeable;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentSource;
import eu.kinae.k_rabbitmq_cdr.component.AbstractComponentTarget;
import eu.kinae.k_rabbitmq_cdr.component.ComponentDirectLinked;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponent;
import eu.kinae.k_rabbitmq_cdr.component.ParallelComponents;
import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.KOptions;
import eu.kinae.k_rabbitmq_cdr.params.KParameters;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.params.TransferType;
import eu.kinae.k_rabbitmq_cdr.utils.ProgressDisplayPrinter;
import eu.kinae.k_rabbitmq_cdr.utils.SharedQueue;
import eu.kinae.k_rabbitmq_cdr.utils.SharedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Closeable {

    private final ConnectorSource connectorSource;
    private final ConnectorTarget connectorTarget;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProgressDisplayPrinter progressPrinter;

    public Connector(ConnectorSource connectorSource, ConnectorTarget connectorTarget) {
        this.connectorSource = connectorSource;
        this.connectorTarget = connectorTarget;
    }

    public void start(KParameters parameters, KOptions options) {
        SharedStatus sharedStatus = new SharedStatus(options);
        progressPrinter = new ProgressDisplayPrinter(sharedStatus);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(progressPrinter, 0, Duration.ofMillis(options.interval()).toMillis(), TimeUnit.MILLISECONDS);

        try {
            if(parameters.transferType() == TransferType.DIRECT) {
                direct(parameters, options, sharedStatus);
            } else if(parameters.transferType() == TransferType.BUFFERED) {
                if(parameters.processType() == ProcessType.SEQUENTIAL) {
                    bufferedSequential(parameters, options, sharedStatus);
                } else {
                    bufferedParallel(parameters, options, sharedStatus);
                }
            }
        } catch(Exception e) {
            logger.error("Unknown error, please report it", e);
            throw new RuntimeException("Unknown error, please report it", e);
        } finally {
            scheduledExecutorService.shutdown();
        }
    }

    private void direct(KParameters parameters, KOptions options, SharedStatus sharedStatus) throws Exception {
        logger.info("initiating a direct transfer between {} => {}", connectorSource.getSupportedType(), connectorTarget.getSupportedType());
        try(Source source = connectorSource.getDirectLinked(parameters, options, sharedStatus); Target target = connectorTarget.getDirectLinked(parameters, sharedStatus)) {
            ComponentDirectLinked directTransfer = new ComponentDirectLinked(source, target, options);

            progressPrinter.printReadProgress();
            progressPrinter.printWriteProgress();
            progressPrinter.printEmptyLine();

            directTransfer.start();

            progressPrinter.printLastReadProgress();
            progressPrinter.printLastWriteProgress();
        }
    }

    private void bufferedSequential(KParameters parameters, KOptions options, SharedStatus sharedStatus) throws Exception {
        logger.info("initiating a buffered sequential transfer between {} => {}", connectorSource.getSupportedType(), connectorTarget.getSupportedType());
        SharedQueue sharedQueue = new SharedQueue(ProcessType.SEQUENTIAL);
        try(AbstractComponentSource source = connectorSource.getSequentialComponent(sharedQueue, parameters, options, sharedStatus);
            AbstractComponentTarget target = connectorTarget.getSequentialComponent(sharedQueue, parameters, options, sharedStatus)) {

            progressPrinter.printReadProgress();
            source.start();
            progressPrinter.printLastReadProgress();

            progressPrinter.printWriteProgress();
            target.start();
            progressPrinter.printLastWriteProgress();
        }
    }

    private void bufferedParallel(KParameters parameters, KOptions options, SharedStatus sharedStatus) throws Exception {
        logger.info("initiating a buffered parallel transfer between {} => {} with {} threads",
                    connectorSource.getSupportedType(), connectorTarget.getSupportedType(), options.threads());

        SharedQueue sharedQueue = new SharedQueue(ProcessType.PARALLEL);
        try(ParallelComponent source = connectorSource.getParallelComponent(sharedQueue, parameters, options, sharedStatus);
            ParallelComponents callables = connectorTarget.getParallelComponent(sharedQueue, parameters, options, sharedStatus)) {

            progressPrinter.printReadProgress();
            progressPrinter.printWriteProgress();
            progressPrinter.printEmptyLine();

            ExecutorService fixedThreadPool = Executors.newFixedThreadPool(options.threads() + 1);
            callables.add(source);
            fixedThreadPool.invokeAll(callables);
            fixedThreadPool.shutdown();

            progressPrinter.printLastReadProgress();
            progressPrinter.printLastWriteProgress();
        }
    }

    @Override
    public void close() {
        try {
            connectorSource.close();
        } catch(Exception e) {
            logger.warn("Cannot close ConnectorSource");
        }

        try {
            connectorTarget.close();
        } catch(Exception e) {
            logger.warn("Cannot close ConnectorTarget");
        }

    }
}
