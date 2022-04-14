package eu.kinae.k_rabbitmq_cdr.protocol;

import eu.kinae.k_rabbitmq_cdr.utils.KMessage;

public interface Source {

    KMessage pop() throws Exception;

}
