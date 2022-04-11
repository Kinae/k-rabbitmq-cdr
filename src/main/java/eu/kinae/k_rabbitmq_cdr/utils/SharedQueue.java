package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;

public class SharedQueue {

    private final Queue<GetResponse> buffer;

    public SharedQueue(ProcessType processType) {
        this(processType, null);
    }

    public SharedQueue(ProcessType processType, Integer capacity) {
        this.buffer = queueByProcessType(processType, capacity);
    }

    public Class<? extends Queue> getBufferType() {
        return buffer.getClass();
    }

    public int size() {
        return this.buffer.size();
    }

    private static Queue<GetResponse> queueByProcessType(ProcessType type, Integer capacity) {
        return switch(type) {
            case SEQUENTIAL -> new LinkedList<>();
            case PARALLEL -> capacity != null ? new LinkedBlockingQueue<>(capacity) : new LinkedBlockingQueue<>();
        };
    }

    public void push(GetResponse response) throws InterruptedException {
        if(buffer instanceof BlockingQueue<GetResponse> bq) {
            bq.put(response);
        } else {
            buffer.add(response);
        }
    }

    public GetResponse pop() throws InterruptedException {
        if(buffer instanceof BlockingQueue<GetResponse> bq) {
            return bq.poll(500, TimeUnit.MILLISECONDS);
        } else {
            return buffer.poll();
        }
    }

}
