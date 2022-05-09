package eu.kinae.k_rabbitmq_cdr.component;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public interface Source extends AutoCloseable {

    KMessage pop() throws Exception;

}
