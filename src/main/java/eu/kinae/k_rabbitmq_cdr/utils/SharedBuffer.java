package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.GetResponse;
import eu.kinae.k_rabbitmq_cdr.params.ProcessType;

public class SharedBuffer {

    private static SharedBuffer instance = null;
    private final Queue<GetResponse> buffer;

    private SharedBuffer(Queue<GetResponse> buffer) {
        this.buffer = buffer;
    }

    public static SharedBuffer getInstance(ProcessType processType) {
        return getInstance(processType, null);
    }

    public static SharedBuffer getInstance(ProcessType processType, Integer capacity) {
        if(instance == null)
            instance = new SharedBuffer(type(processType, capacity));
        return instance;
    }

    private static Queue<GetResponse> type(ProcessType type, Integer capacity) {
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
