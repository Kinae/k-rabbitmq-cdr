package eu.kinae.k_rabbitmq_cdr.protocol;

import java.util.ArrayList;

public class ParallelComponents extends ArrayList<ParallelComponent> implements AutoCloseable {

    @Override
    public void close() throws Exception {
        for(ParallelComponent parallelComponent : this) {
            parallelComponent.close();
        }
    }
}
