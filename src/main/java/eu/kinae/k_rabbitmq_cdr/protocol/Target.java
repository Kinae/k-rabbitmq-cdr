package eu.kinae.k_rabbitmq_cdr.protocol;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public interface Target extends AutoCloseable {

    void push(KMessage message) throws Exception;

}
