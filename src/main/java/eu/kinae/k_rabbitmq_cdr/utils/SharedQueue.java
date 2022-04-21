package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import eu.kinae.k_rabbitmq_cdr.params.ProcessType;
import eu.kinae.k_rabbitmq_cdr.protocol.Source;
import eu.kinae.k_rabbitmq_cdr.protocol.Target;

public class SharedQueue implements Source, Target {

    private final Queue<KMessage> buffer;

    public SharedQueue(ProcessType processType) {
        this(processType, null);
    }

    public SharedQueue(ProcessType processType, Integer capacity) {
        this.buffer = queueByProcessType(processType, capacity);
    }

    private static Queue<KMessage> queueByProcessType(ProcessType type, Integer capacity) {
        return switch(type) {
            case SEQUENTIAL -> new LinkedList<>();
            case PARALLEL -> capacity != null ? new LinkedBlockingQueue<>(capacity) : new LinkedBlockingQueue<>();
        };
    }

    public int size() {
        return this.buffer.size();
    }

    public void push(KMessage response) throws Exception {
        if(buffer instanceof BlockingQueue<KMessage> bq) {
            bq.put(response);
        } else {
            buffer.add(response);
        }
    }

    public KMessage pop() throws Exception {
        if(buffer instanceof BlockingQueue<KMessage> bq) {
            return bq.poll(500, TimeUnit.MILLISECONDS);
        } else {
            return buffer.poll();
        }
    }

    @Override
    public void close() {

    }
}
