package eu.kinae.k_rabbitmq_cdr.protocol;

public interface Component {

    long consumeNProduce() throws Exception;
}
