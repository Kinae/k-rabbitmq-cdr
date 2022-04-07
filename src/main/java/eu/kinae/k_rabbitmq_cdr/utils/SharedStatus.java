package eu.kinae.k_rabbitmq_cdr.utils;

public class SharedStatus {

    private static SharedStatus instance = null;
    private volatile boolean consumerAlive = true;

    private SharedStatus() {
    }

    public static SharedStatus getInstance() {
        if(instance == null) {
            instance = new SharedStatus();
        }
        return instance;
    }

    // name if bad, can not find better for now
    public void notifySourceConsumerIsDone() {
        this.consumerAlive = false;
    }

    public boolean isConsumerAlive() {
        return consumerAlive;
    }

}
