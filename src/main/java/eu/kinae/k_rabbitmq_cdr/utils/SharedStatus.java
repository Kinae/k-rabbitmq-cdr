package eu.kinae.k_rabbitmq_cdr.utils;

import java.util.concurrent.atomic.AtomicLong;

public class SharedStatus {

    private long total;
    private final AtomicLong read = new AtomicLong();
    private final AtomicLong write = new AtomicLong();
    private volatile boolean consumerAlive = true;

    public SharedStatus() {
    }

    // name if bad, can not find better for now
    public void notifySourceConsumerIsDone() {
        this.consumerAlive = false;
    }

    public boolean isConsumerAlive() {
        return consumerAlive;
    }

    public void setTotal(long total) {
        if(this.total == 0) {
            this.total = total;
        }
    }

    public long getTotal() {
        return total;
    }

    public long getRead() {
        return read.get();
    }

    public long getWrite() {
        return write.get();
    }

    public void incrementRead() {
        read.incrementAndGet();
    }

    public void incrementWrite() {
        write.incrementAndGet();
    }

}
