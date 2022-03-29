package eu.kinae.k_rabbitmq_cdr.connector;

import eu.kinae.k_rabbitmq_cdr.params.JCommanderParams;

public interface Connector {

    void run(JCommanderParams params);

}
