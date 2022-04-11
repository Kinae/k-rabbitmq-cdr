package eu.kinae.k_rabbitmq_cdr.utils;

public class SharedStatus {

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

}
