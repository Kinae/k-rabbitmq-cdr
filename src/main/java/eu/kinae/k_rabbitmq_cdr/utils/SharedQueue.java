package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.component.Source;
import eu.kinae.k_rabbitmq_cdr.component.Target;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;

public class SharedQueue implements Source, Target {

    private final Queue<KMessage> queue;

    public SharedQueue(ProcessType processType) {
        this(processType, null);
    }

    public SharedQueue(ProcessType processType, Integer capacity) {
        this.queue = queueByProcessType(processType, capacity);
    }

    private static Queue<KMessage> queueByProcessType(ProcessType type, Integer capacity) {
        return switch(type) {
            case SEQUENTIAL -> new LinkedList<>();
            case PARALLEL -> capacity != null ? new LinkedBlockingQueue<>(capacity) : new LinkedBlockingQueue<>();
        };
    }

    public int size() {
        return this.queue.size();
    }

    public void push(KMessage response) throws Exception {
        if(queue instanceof BlockingQueue<KMessage> bq) {
            bq.put(response);
        } else {
            queue.add(response);
        }
    }

    public KMessage pop() throws Exception {
        if(queue instanceof BlockingQueue<KMessage> bq) {
            return bq.poll(500, TimeUnit.MILLISECONDS);
        } else {
            return queue.poll();
        }
    }

    @Override
    public void close() {

    }
}
