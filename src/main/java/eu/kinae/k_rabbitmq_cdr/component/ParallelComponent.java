package eu.kinae.k_rabbitmq_cdr.component;

import java.util.concurrent.Callable;

public interface ParallelComponent extends AutoCloseable, Callable<Long> {

}
