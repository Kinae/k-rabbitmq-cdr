package eu.kinae.k_rabbitmq_cdr.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Engine {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void start() {
        try {
            long start = System.currentTimeMillis();
            long count = consumeNProduce();
            long end = System.currentTimeMillis();
            logger.info("messages consumed and produced : {} in {}ms", count, (end - start));
        } catch(Exception e) {
            logger.error("Error : ", e);
        } finally {
            onFinally();
            close();
        }
    }

    protected abstract long consumeNProduce() throws Exception;

    protected abstract void onFinally();

    protected abstract void close();

}
