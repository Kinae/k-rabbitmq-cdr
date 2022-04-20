package eu.kinae.k_rabbitmq_cdr.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Engine {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public long start() {
        long count;
        long start = System.currentTimeMillis();
        try {
            count = consumeNProduce();
        } catch(Exception e) {
            logger.error("Error : ", e);
            throw new RuntimeException(e);
        } finally {
            onFinally();
        }

        long end = System.currentTimeMillis();
        logger.info("messages consumed and produced : {} in {}ms", count, (end - start));
        return count;
    }

    public abstract long consumeNProduce() throws Exception;

    protected abstract void onFinally();

}
